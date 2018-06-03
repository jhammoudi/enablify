package com.hammoudij.enablify.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.api.Camera.CameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.hammoudij.enablify.activity.MainCameraActivity.FIREBASE_TEXT;

public class CameraPresenter implements MainMVP.CameraPresenter {

    public static final int SETTINGS_REQUEST_CODE = 101;
    public static final String TEMP_IMAGE = "tempImage";

    public void onToggleFlashClicked(boolean checked, Camera camera) {

        if (checked) {

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            camera.setParameters(parameters);

        } else {

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
        }
    }

    public void onCaptureBtnClick(Camera camera, Activity activity) {
        camera.takePicture(null, null, getPictureCallback(activity));
    }

    public void startIntent(Activity activity, Class c) {
        Intent intent = new Intent(activity, c);
        activity.startActivity(intent);
    }

    public void showSettingsDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.need_permission_string);
        builder.setMessage(R.string.need_permission_info_string);
        builder.setPositiveButton(R.string.go_to_settings_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings(activity);
            }
        });
        builder.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void openSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(activity.getString(R.string.package_string), activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    public Camera.PictureCallback getPictureCallback(final Activity activity) {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                createImageFromBitmap(cameraBitmap, activity);
            }
        };
        return picture;
    }

    private String createImageFromBitmap(Bitmap bitmap, Activity activity) {
        String fileName = TEMP_IMAGE;//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(activity.getLocalClassName(), e.getMessage());
            fileName = null;
        }
        return fileName;
    }


    private class TextRecognitionAsyncTask extends AsyncTask<Void, Void, Void> {

        Activity activity;
        Class c;
        ProgressDialog dialog;
        Intent intent;

        public TextRecognitionAsyncTask(Activity activity, Class c) {
            this.activity = activity;
            this.dialog = new ProgressDialog(activity);
            this.c = c;
            this.intent = new Intent(activity, c);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(activity.getApplicationContext().getResources().getString(R.string.extract_text));
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Bitmap test = null;
            try {
                test = BitmapFactory.decodeStream(activity.openFileInput(TEMP_IMAGE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(test);
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                    .getVisionTextDetector();
            detector.detectInImage(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText texts) {
                                    String textFireBase = getTextFromFireBase(texts);
                                    intent.putExtra(FIREBASE_TEXT, textFireBase);

                                    if (!textFireBase.equals("")) {
                                        startIntent();
                                    } else {
                                        Toast.makeText(activity, R.string.no_text_detected_string, Toast.LENGTH_LONG).show();
                                    }

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(activity.getLocalClassName(), e.getMessage());
                                    // Task failed with an exception
                                    //Generic exception must be handled as it is overridden
                                    e.printStackTrace();
                                }
                            });
            return null;
        }

        private void startIntent() {
            activity.startActivity(intent);
        }
    }

    public void runTextRecognition(Activity activity, Class c) {
        new TextRecognitionAsyncTask(activity, c).execute();
    }

    public String getTextFromFireBase(FirebaseVisionText texts) {

        StringBuilder s = new StringBuilder();

        for (FirebaseVisionText.Block block : texts.getBlocks()) {
            for (FirebaseVisionText.Line line : block.getLines()) {
                String text = line.getText();
                s.append(text);
            }
            s.append("\n");
        }

        return s.toString();
    }

    public Camera checkDeviceCamera(Activity activity) {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            Log.d(activity.getLocalClassName(), e.getMessage());
            //There are no specific exceptions to handle when opening the Camera
            //however want to catch all kinds of errors that may occur, as it is appropriate
            e.printStackTrace();
        }
        return camera;
    }

}
