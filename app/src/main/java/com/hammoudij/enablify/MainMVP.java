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

package com.hammoudij.enablify;

import android.app.Activity;
import android.hardware.Camera;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.hammoudij.enablify.activity.AudioAdapter;
import com.hammoudij.enablify.api.TextToSpeech.ApiInterface;
import com.hammoudij.enablify.db.AppDatabase;
import com.hammoudij.enablify.model.Audio;
import com.hammoudij.enablify.model.AudioConfig;
import com.hammoudij.enablify.model.Input;
import com.hammoudij.enablify.model.Voice;

import java.util.List;

/**
 * The Main Interface which encapsulates all other interfaces in the MVP Architecture
 */

public interface MainMVP {

    interface CameraPresenter {
        void onToggleFlashClicked(boolean checked, Camera camera);

        void onCaptureBtnClick(Camera camera, Activity activity);

        void startIntent(Activity activity, Class c);

        void showSettingsDialog(final Activity activity);

        void openSettings(Activity activity);

        Camera.PictureCallback getPictureCallback(final Activity activity);

        void runTextRecognition(Activity activity, Class c);

        String getTextFromFireBase(FirebaseVisionText texts);

        Camera checkDeviceCamera(Activity activity);
    }

    interface AudioPresenter {
        void setUpActivity(RecyclerView recyclerView, Activity activity);

        void removeItem(int position, List<Audio> audioList, AppDatabase db, AudioAdapter audioAdapter);

        void shareItem(View v, int position, List<Audio> audioList);

        void clickItem(View v, int position, List<Audio> audioList);
    }

    interface CreateAudioPresenter {

        void getLanguageCodeDoInBackground(ApiInterface apiService, String API_KEY, final List<String> listOfLanguageCodes,
                                           final List<String> listOfLanguages, final Spinner languageCodeSpinner, final Activity activity);

        void getVoiceTypeDoInBackground(ApiInterface apiService, String languageCode, String API_KEY,
                                        final List<String> listOfVoiceTypes, final Spinner voiceTypeSpinner, final Activity activity);

        void synthesizeTextDoInBackground(Input input, Voice voice, AudioConfig audioConfig, ApiInterface apiService,
                                          String API_KEY, final Activity activity, final String audioName, final AppDatabase db);
    }
}
