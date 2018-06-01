package com.hammoudij.enablify.activity;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.api.Camera.CameraPreview;
import com.hammoudij.enablify.presenter.CameraPresenter;
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

public class MainCameraActivity extends AppCompatActivity implements MainMVP.EnablifyView {

    public static final String FIREBASE_TEXT = "Firebase";
    private Camera mCamera;
    private CameraPreview mPreview;
    private MainMVP.CameraPresenter mPresenter;

    @BindView(R.id.capture_btn)
    Button mCaptureBtn;

    @BindView(R.id.settings_btn)
    Button mSettingsBtn;

    @BindView(R.id.confirm_image_btn)
    Button mConfirmBtn;

    @BindView(R.id.cancel_image_btn)
    Button mCancelBtn;

    @BindView(R.id.audio_list_btn)
    Button mAudioListBtn;

    @BindView(R.id.toggle_flash)
    ToggleButton mCameraToggleFlash;

    @BindView(R.id.camera_buttons)
    RelativeLayout mMainCameraButtons;

    @BindView(R.id.verify_image_buttons)
    RelativeLayout mVerifyImageButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);
        setTheme(R.style.splashScreenTheme);
        setupMVP();
        askCameraPermissions();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
    }

    private void setupMVP() {
        mPresenter = new CameraPresenter();
    }

    @OnCheckedChanged(R.id.toggle_flash)
    public void onToggleFlashClicked(boolean checked) {
        mPresenter.onToggleFlashClicked(checked, mCamera);
    }

    @OnClick(R.id.capture_btn)
    public void onCaptureBtnClick() {
        mPresenter.onCaptureBtnClick(mCamera, this);
        mMainCameraButtons.setVisibility(View.INVISIBLE);
        mVerifyImageButtons.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.cancel_image_btn)
    public void onCancelImageBtnClick() {
        mMainCameraButtons.setVisibility(View.VISIBLE);
        mVerifyImageButtons.setVisibility(View.INVISIBLE);
        mCamera.startPreview();
    }

    @OnClick(R.id.confirm_image_btn)
    public void onConfirmImageBtnClick() {
        mPresenter.runTextRecognition(this, CreateAudioActivity.class);
    }

    @OnClick(R.id.settings_btn)
    public void onSettingsBtnClick() {
        mPresenter.startIntent(this, SettingsActivity.class, 2);
    }

    @OnClick(R.id.audio_list_btn)
    public void onAudioListBtnClick() {
        mPresenter.startIntent(this, AudioListActivity.class, 3);
    }

    public void askCameraPermissions() {
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
                            mPresenter.showSettingsDialog(MainCameraActivity.this);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
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

    public Camera checkDeviceCamera() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            // release the camera for other applications
            mPreview.getHolder().removeCallback(mPreview);
            mCamera = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMainCameraButtons.setVisibility(View.VISIBLE);
        mVerifyImageButtons.setVisibility(View.INVISIBLE);

        if (mCamera == null) {
            connectCamera();
        }
    }
}
