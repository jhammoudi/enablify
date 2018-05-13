package com.hammoudij.enablify.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hammoudij.enablify.R;
import com.hammoudij.enablify.api.Camera.CameraCapture;
import com.hammoudij.enablify.api.Camera.CameraPreview;

public class MainCameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //request permission to use camera
        //onRequestPermissionsResult() will be called as a result
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                1);

        Button captureBtn = (Button) findViewById(R.id.capture_btn);
        captureBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCamera.takePicture(null,null, CameraCapture.getPictureCallback());
            }
        });

        Button settingsBtn = (Button) findViewById(R.id.settings_btn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainCameraActivity.this,
                        SettingsActivity.class);
                startActivity(intent);
            }
        });

        Button audioListBtn = (Button) findViewById(R.id.audio_list_btn);
        audioListBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainCameraActivity.this,
                        AudioListActivity.class);
                startActivity(intent);
            }
        });


        ToggleButton cameraToggleFlash = (ToggleButton) findViewById(R.id.toggle_flash);

        cameraToggleFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {

                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    mCamera.setParameters(parameters);

                } else {

                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(parameters);

                }
            }
        });

    }

    public void setUpCamera() {
        // Create an instance of Camera
        mCamera = checkDeviceCamera();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    public Camera checkDeviceCamera(){
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mCamera != null){
//            mCamera.release();
//            // release the camera for other applications
//            mCamera = null;
//        }
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
