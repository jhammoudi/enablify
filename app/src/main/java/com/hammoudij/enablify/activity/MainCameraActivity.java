package com.hammoudij.enablify.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.api.Camera.CameraCapture;
import com.hammoudij.enablify.api.Camera.CameraPreview;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainCameraActivity extends AppCompatActivity implements MainMVP.EnablifyView{

    private Camera mCamera;
    private CameraPreview mPreview;
    private MainMVP.EnablifyPresenter mPresenter;

    @BindView(R.id.capture_btn)
    Button mCaptureBtn;

    @BindView(R.id.settings_btn)
    Button mSettingsBtn;

    @BindView(R.id.audio_list_btn)
    Button mAudioListBtn;

    @BindView(R.id.toggle_flash)
    ToggleButton mCameraToggleFlash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);
        askCameraPermissions();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ButterKnife.bind(this);
    }

    @OnCheckedChanged(R.id.toggle_flash)
    public void onToggleFlashClicked(boolean checked) {
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

    @OnClick(R.id.capture_btn)
    public void onCaptureBtnClick() {

        mCamera.takePicture(null,null, CameraCapture.getPictureCallback());
    }

    @OnClick(R.id.settings_btn)
    public void onSettingsBtnClick() {
        Intent intent = new Intent(MainCameraActivity.this,
                SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.audio_list_btn)
    public void onAudioListBtnClick() {
        Intent intent = new Intent(MainCameraActivity.this,
                AudioListActivity.class);
        startActivity(intent);
    }

    private void askCameraPermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        connectCamera();
                        // permission is granted, open the camera
                        //Log.d("Test","askCameraPermission");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainCameraActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
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

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public void connectCamera() {
        // Create an instance of Camera
        mCamera = checkDeviceCamera();
        // Create our Preview view and set it as the content of our activity.
        mCamera.setDisplayOrientation(90);
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
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            connectCamera();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            // release the camera for other applications
            mPreview.getHolder().removeCallback(mPreview);
            mCamera = null;
        }
    }

}
