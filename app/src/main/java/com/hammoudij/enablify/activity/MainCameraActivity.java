/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hammoudij.enablify.activity;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
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

/**
 * The Main Camera Activity Class shows the Camera preview where users can capture images
 */

public class MainCameraActivity extends AppCompatActivity {

    public static final String FIREBASE_TEXT = "Firebase";
    public static final int PORTRAIT_ORIENTATION = 90;
    private Camera mCamera;
    private CameraPreview mPreview;
    private MainMVP.CameraPresenter mPresenter;
    boolean shouldExecuteOnResume;

    @BindView(R.id.capture_btn)
    Button mCaptureBtn;

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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);
        setTheme(R.style.splashScreenTheme);
        setupMVP();
        askCameraPermissions();
        ButterKnife.bind(this);
        shouldExecuteOnResume = false;
    }

    /**
     * Sets up the MVP Architecture
     */
    private void setupMVP() {
        //Initialising the Presenter
        mPresenter = new CameraPresenter();
    }

    /**
     * Handles the toggle image is pressed, the camera flash mode toggles, called from the presenter
     */
    @OnCheckedChanged(R.id.toggle_flash)
    public void onToggleFlashClicked(boolean checked) {
        mPresenter.onToggleFlashClicked(checked, mCamera);
    }

    /**
     * Handles the capture button click
     * this will also show new buttons where users can either cancel or confirm the image
     */
    @OnClick(R.id.capture_btn)
    public void onCaptureBtnClick() {
        mPresenter.onCaptureBtnClick(mCamera, this);
        mMainCameraButtons.setVisibility(View.INVISIBLE);
        mVerifyImageButtons.setVisibility(View.VISIBLE);
    }

    /**
     * Handles the cancel button which resumes the camera
     */
    @OnClick(R.id.cancel_image_btn)
    public void onCancelImageBtnClick() {
        mMainCameraButtons.setVisibility(View.VISIBLE);
        mVerifyImageButtons.setVisibility(View.INVISIBLE);
        mCamera.startPreview();
    }

    /**
     * handles the confirm button click which runs the text recognition and starts new activity
     */
    @OnClick(R.id.confirm_image_btn)
    public void onConfirmImageBtnClick() {
        mPresenter.runTextRecognition(this, CreateAudioActivity.class);
    }

    /**
     * handles the audio list button click which starts new intent to the AudioListActivity
     */
    @OnClick(R.id.audio_list_btn)
    public void onAudioListBtnClick() {
        mPresenter.startIntent(this, AudioListActivity.class);
    }

    /**
     * Handles the run-time permission for the Camera
     */
    public void askCameraPermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //if permission is granted, connect the camera
                        connectCamera();
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // if permission is not granted, show user a dialog asking user to accept permission to use application
                        if (response.isPermanentlyDenied()) {
                            mPresenter.showSettingsDialog(MainCameraActivity.this);
                        }
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Connects camera to the camera preview
     */
    public void connectCamera() {
        // Create an instance of the Camera
        mCamera = mPresenter.checkDeviceCamera(this);
        //set the orientation of the camera to portrait
        mCamera.setDisplayOrientation(PORTRAIT_ORIENTATION);
        // Create a Preview view and set it as the context of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    /**
     * This method is called to disconnect the Camera when not in use, to allow other applications to use it
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            // release the camera for other applications
            mPreview.getHolder().removeCallback(mPreview);
            mCamera = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
        //when onPause is called, disconnect camera to allow other applications to connect
        releaseCamera();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        mMainCameraButtons.setVisibility(View.VISIBLE);
        mVerifyImageButtons.setVisibility(View.INVISIBLE);

        Toast.makeText(this, R.string.rotate_camera_for_text_recognition_string, Toast.LENGTH_LONG).show();

        //this if statement is to ensure that the camera is not connected before permissions are given
        // as onResume is called after onCreate, if permission are not given yet, the app will crash as it can not connect
        //this ensures that when app is first opened, this method is not called
        if (shouldExecuteOnResume) {
            if (mCamera == null) {
                //reconnect the camera after onResume called as it is needed
                connectCamera();
            }
        } else {
            shouldExecuteOnResume = true;
        }
    }
}
