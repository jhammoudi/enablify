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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.hammoudij.enablify.activity.MainCameraActivity.FIREBASE_TEXT;

/**
 * The Camera Presenter which handles the processing corresponding to the Camera and text recognition from camera image
 */

public class CameraPresenter implements MainMVP.CameraPresenter {

    public static final int SETTINGS_REQUEST_CODE = 101;
    public static final String TEMP_IMAGE = "tempImage";

    /**
     * Handles the toggling of flash modes in the camera parameter
     */
    public void onToggleFlashClicked(boolean checked, Camera camera) {

        //TODO When flash is enabled, the camera capturing is being delayed by a significant time. Need to fix this bug

        if (checked) {
            //if toggle image is checked, set flash mode on
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            camera.setParameters(parameters);

        } else {
            //if toggle image is un-checked, set flash mode off
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
        }
    }

    /**
     * Handle the capturing of image by camera
     */
    public void onCaptureBtnClick(Camera camera, Activity activity) {
        //takes a picture which saves bitmap to local storage
        camera.takePicture(null, null, getPictureCallback(activity));
    }

    /**
     * Handles starts to intents used by activities
     */
    public void startIntent(Activity activity, Class c) {
        Intent intent = new Intent(activity, c);
        activity.startActivity(intent);
    }

    /**
     * Starts the Settings Dialog when user does not accepts run-time permissions
     */
    public void showSettingsDialog(final Activity activity) {
        // an Alert dialog is built, asking users to accept permission to use the application
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.need_permission_string);
        builder.setMessage(R.string.need_permission_info_string);

        //if user accepts, start an intent to open the setting application to manually accept permissions
        builder.setPositiveButton(R.string.go_to_settings_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings(activity);
            }
        });

        //if user doesn't accept, they can not use the application
        builder.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * Handles the intent to open the settings application
     */
    public void openSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(activity.getString(R.string.package_string), activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    /**
     * Returns the picture call back which handles the camera capture.
     */
    public Camera.PictureCallback getPictureCallback(final Activity activity) {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //when picture is taken, create a Bitmap using the data from image
                Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //stores bitmap to local storage
                createImageFromBitmap(cameraBitmap, activity);
            }
        };
        return picture;
    }

    /**
     * Creates an Image in local storage with Bitmap parameter
     */
    private String createImageFromBitmap(Bitmap bitmap, Activity activity) {
        //sets a temporary file name
        String fileName = TEMP_IMAGE;//no .png or .jpg needed

        //creates image which will then be retrieved later for text recognition
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

    /**
     * TextRecognitionAsyncTask asyncTask handling the text recognition of the saved image
     */
    private class TextRecognitionAsyncTask extends AsyncTask<Void, Void, Void> {

        Activity activity;
        Class c;
        ProgressDialog dialog;
        Intent intent;

        /**
         * TextRecognitionAsyncTask Constructor
         */
        public TextRecognitionAsyncTask(Activity activity, Class c) {
            this.activity = activity;
            this.dialog = new ProgressDialog(activity);
            this.c = c;
            this.intent = new Intent(activity, c);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(activity.getApplicationContext().getResources().getString(R.string.extract_text));
            dialog.show();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground(Void... voids) {

            //Retrieves the bitmap that was in local storage with the temporary image name
            Bitmap test = null;
            try {
                test = BitmapFactory.decodeStream(activity.openFileInput(TEMP_IMAGE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //creates a firebasevision image which is needed to detect text
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(test);

            //starts the text detection using the FirebaseVisionImage
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                    .getVisionTextDetector();
            detector.detectInImage(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText texts) {
                                    //if the text detection succeeded, call getTextFromFireBase to retrieve a string
                                    String textFireBase = getTextFromFireBase(texts);

                                    //store this string into an intent, which will be started later
                                    intent.putExtra(FIREBASE_TEXT, textFireBase);

                                    if (!textFireBase.equals("")) {
                                        //if text was detected, start intent
                                        startIntent();
                                    } else {
                                        //if text was not detected, display Toast to notify users to take another picture
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

        /**
         * Starts the intent with the global variable intent with the text added to it as an intent extra
         */
        private void startIntent() {
            activity.startActivity(intent);
        }
    }

    /**
     * Calls the text recognition asynctask.
     * Enables other classes to call the asynctask by calling this method
     */
    public void runTextRecognition(Activity activity, Class c) {
        new TextRecognitionAsyncTask(activity, c).execute();
    }

    /**
     * Returns a string created from the FireBaseVisionText object
     */
    public String getTextFromFireBase(FirebaseVisionText texts) {

        StringBuilder s = new StringBuilder();

        //loop over each block in the Firebasevisiontext
        for (FirebaseVisionText.Block block : texts.getBlocks()) {

            //for each block, loop over its lines
            for (FirebaseVisionText.Line line : block.getLines()) {

                //add the text in each line to the buffer string
                String text = line.getText();
                s.append(text);
            }
            //add a line space after each block
            s.append("\n");
        }

        //returns a string with text from each line of each block in the FirebaseVisionText object
        return s.toString();
    }

    /**
     * Opens the camera, used to connect camera to the camera preview
     */
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
