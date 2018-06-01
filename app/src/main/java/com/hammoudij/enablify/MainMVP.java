package com.hammoudij.enablify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.hammoudij.enablify.api.Camera.CameraPreview;

public interface MainMVP {

    interface CameraPresenter{

        void onToggleFlashClicked(boolean checked, Camera camera);
        void onCaptureBtnClick(Camera camera, Activity activity);
        void startIntent(Activity activity, Class c, int requestCode);
//        void connectCamera(Activity activity);
        void showSettingsDialog(final Activity activity);
        void openSettings(Activity activity);
//        Camera checkDeviceCamera();
//        void onResume(Activity activity, Camera camera);
//        void releaseCamera(Camera camera, CameraPreview cameraPreview);
        Camera.PictureCallback getPictureCallback(final Activity activity);
        void runTextRecognition(Activity activity,Class c);
        String getTextFromFireBase(FirebaseVisionText texts);
    }

    interface AudioPresenter{

    }


    interface EnablifyView{



         //                                                 I am writing a test




    }
}
