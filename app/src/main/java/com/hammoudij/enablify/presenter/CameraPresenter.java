package com.hammoudij.enablify.presenter;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.hammoudij.enablify.MainMVP;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static com.hammoudij.enablify.activity.MainCameraActivity.FIREBASE_TEXT;

public class CameraPresenter implements MainMVP.CameraPresenter {

    private static final String TAG = "Error";
    private Bitmap mCameraBitmap;

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
        camera.takePicture(null,null, getPictureCallback(activity));
    }

    public void startIntent(Activity activity, Class c, int requestCode) {
        Intent intent = new Intent(activity, c);
        activity.startActivityForResult(intent,requestCode);
    }

    public void showSettingsDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings(activity);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void openSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }

    public Camera.PictureCallback getPictureCallback(final Activity activity) {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
                createImageFromBitmap(cameraBitmap, activity);
            }
        };
        return picture;
    }

    private String createImageFromBitmap(Bitmap bitmap, Activity activity) {
        String fileName = "tempImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }


    public void runTextRecognition(final Activity activity, final Class c, final int requestCode) {

        Bitmap test = null;
        try {
            test = BitmapFactory.decodeStream(activity.openFileInput("tempImage"));
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
                                processTextRecognitionResult(activity, c, requestCode, texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });

    }

    public void processTextRecognitionResult(Activity activity, Class c, int requestCode,FirebaseVisionText texts) {
        Intent intent = new Intent(activity, c);
        intent.putExtra(FIREBASE_TEXT, getTextFromFireBase(texts));
        activity.startActivityForResult(intent,requestCode);
    }

    public String getTextFromFireBase(FirebaseVisionText texts) {

        StringBuilder s = new StringBuilder();

        for (FirebaseVisionText.Block block: texts.getBlocks()) {
            for (FirebaseVisionText.Line line: block.getLines()) {
                String text = line.getText();
                s.append(text);
            }
            s.append("\n");
        }

        return s.toString();
    }

}
