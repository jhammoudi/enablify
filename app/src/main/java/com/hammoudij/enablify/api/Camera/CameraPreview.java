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

package com.hammoudij.enablify.api.Camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Camera Preview class that handles interactions with the Camera and surface preview
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private float mDist = 0;

    /**
     * Camera Preview constructor
     */
    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handles the pinch to zoom feature with the Camera preview
     * Handles the touch to focus feature with the Camera preview
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();

        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE
                    && params.isZoomSupported()) {
                mCamera.cancelAutoFocus();

                //handle the zoom of camera according to finger spacing distance
                //this method gets called continuously as the screen is touched
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                //focus with single touch
                handleFocus(event, params);
            }
        }
        return true;
    }

    /**
     * Handles the pinch to zoom feature of camera
     */
    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //handles zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //handles zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        //set the new zoom to the camera parameter
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    /**
     * Handles the touch to focus feature of the camera
     */
    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // calculates the current position of pointer
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null
                && supportedFocusModes
                .contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /**
     * Determines the distance between the two fingers
     */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Method called when surface gets created
     * Similar to onCreate method in an Activity class
     */
    public void surfaceCreated(SurfaceHolder holder) {

        //TODO Create dynamic rotation of camera sensor to enable user to rotate phone, and still detect text in image
        //The default rotation to recognise text is to spin phone 90 degrees anti-clockwise, in landscape mode.

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called when surface gets destroyed
     * Similar to onDestroy method in an Activity class
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Took care of releasing the Camera preview in your activity and presenter.
    }

    /**
     * Method called when surface gets changed
     * Similar to onResume method in an Activity class where it will be called on screen rotation etc
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        // preview surface does not exist
        if (mHolder.getSurface() == null) {
            //then don't do anything
            return;
        }

        // stop preview before making changes, as it can not be changed while previewing
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            //There are no specific exceptions to handle when opening the Camera
            //however want to catch all kinds of errors that may occur, as it is appropriate
            // ignore: tried to stop a non-existent preview
        }

        //set the preview of the camera
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            //There are no specific exceptions to handle when opening the Camera
            //however want to catch all kinds of errors that may occur, as it is appropriate
            e.printStackTrace();
        }
    }
}
