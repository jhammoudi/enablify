package com.hammoudij.enablify.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hammoudij.enablify.R;
import com.hammoudij.enablify.api.CameraPreview;

//import android.graphics.Camera;

public class MainCameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.release();
            // release the camera for other applications
            mCamera = null;
        }
    }

    private void setUpCamera() {
        // Create an instance of Camera
        mCamera = checkDeviceCamera();

        mCamera.setDisplayOrientation(90);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }



    private Camera checkDeviceCamera(){
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpCamera();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to access Camera", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
