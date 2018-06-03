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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.api.TextToSpeech.ApiClient;
import com.hammoudij.enablify.api.TextToSpeech.ApiInterface;
import com.hammoudij.enablify.db.AppDatabase;
import com.hammoudij.enablify.model.AudioConfig;
import com.hammoudij.enablify.model.Input;
import com.hammoudij.enablify.model.Voice;
import com.hammoudij.enablify.presenter.CreateAudioPresenter;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hammoudij.enablify.activity.MainCameraActivity.FIREBASE_TEXT;

/**
 * The Create Audio Activity Class that contains form for users to customise the audio to be created.
 * Includes three async tasks used when retrieving data from API call
 */

public class CreateAudioActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String ENCODING_MP3 = "MP3";
    public static final int PITCH_SEEKBAR_DEFAULT = 0;
    public static final int SPEED_SEEKBAR_DEFAULT = 1;

    @BindView(R.id.audio_name)
    public EditText mAudioNameEditTxt;

    @BindView(R.id.audio_text_input)
    public TextInputLayout mAudioNameTextInput;

    @BindView(R.id.text_from_image)
    public TextView mImageText;

    @BindView(R.id.language_code_spinner)
    public Spinner mLanguageCodeSpinner;

    @BindView(R.id.voice_type_spinner)
    public Spinner mVoiceTypeSpinner;

    @BindView(R.id.create_audio_btn)
    public Button mCreateAudioBtn;

    @BindView(R.id.pitch_seek_bar)
    public IndicatorSeekBar mPitchSeekBar;

    @BindView(R.id.speed_seek_bar)
    public IndicatorSeekBar mSpeedSeekBar;

    private MainMVP.CreateAudioPresenter mPresenter;

    private List<String> mListOfLanguageCodes = new ArrayList<>();
    private List<String> mListOfLanguages = new ArrayList<>();
    private List<String> mListOfVoiceTypes = new ArrayList<>();

    //Starts the Api Service to be called when needed
    ApiInterface mApiService = ApiClient.getClient().create(ApiInterface.class);

    ArrayAdapter<String> mLanguageSpinnerAdapter;
    ArrayAdapter<String> mVoiceSpinnerAdapter;

    Intent intent;

    //API key used for all API calls for authentication and tracking
    private final static String API_KEY = "AIzaSyCNvZKnuFSzUffWpiKWrYLKCg4KeEjaNJM";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_audio);
        setupActionBar();
        ButterKnife.bind(this);
        setupMVP();
        intent = getIntent();

        mImageText.setText(intent.getStringExtra(FIREBASE_TEXT));

        new GetLanguageCodeAsyncTask().execute();
        mLanguageCodeSpinner.setOnItemSelectedListener(this);
        mVoiceTypeSpinner.setEnabled(false);
        mLanguageCodeSpinner.setEnabled(false);
    }

    /**
     * Sets up the MVP Architecture
     */
    private void setupMVP() {
        //Initialising the Presenter
        mPresenter = new CreateAudioPresenter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu.
        getMenuInflater().inflate(R.menu.menu_create_audio, menu);
        return true;
    }

    /**
     * handles the Create Audio button click
     */
    @OnClick(R.id.create_audio_btn)
    public void onCreateAudioBtnClick() {


        if (mAudioNameEditTxt.getText().toString().equals("")) {
            //If the Audio name edit text is empty, then create an error message for the user
            mAudioNameEditTxt.requestFocus();
            mAudioNameTextInput.setError(getResources().getString(R.string.audio_name_error_string));


        } else if (!mVoiceTypeSpinner.isEnabled() && !mLanguageCodeSpinner.isEnabled()) {
            //Else if both Spinners are disabled, then show a Toast asking user to wait for data to download
            //These spinners are initially disabled, but enabled once the download is complete
            Toast.makeText(CreateAudioActivity.this, getResources().getString(R.string.download_error_string), Toast.LENGTH_LONG).show();

            //else if everything is fine, then continue with synthesizing text
        } else {

            //create a new Input object and store text
            Input input = new Input();
            input.setText(intent.getStringExtra(FIREBASE_TEXT));

            //create a new Voice object and stores language and voice type
            Voice voice = new Voice();
            voice.setLanguageCode(mListOfLanguageCodes.get(mLanguageCodeSpinner.getSelectedItemPosition()));
            voice.setName(mVoiceTypeSpinner.getSelectedItem().toString());

            //create a new AudioConfig object and store pitch, speed, and audio encoding
            AudioConfig audioConfig = new AudioConfig();
            audioConfig.setAudioEncoding(ENCODING_MP3);
            audioConfig.setPitch(mPitchSeekBar.getProgress());
            audioConfig.setSpeakingRate(mSpeedSeekBar.getProgress());

            //using these objects, call the asynctask which handles the POST request
            new SynthesizeTextAsyncTask(input, voice, audioConfig).execute();
        }
    }

    /**
     * Adds the Home button in the activity to allow users to go 'back' to previous activity
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            //if the refresh icon is pressed
            case R.id.action_refresh:
                //then download the languages again, in case there is an error
                new GetLanguageCodeAsyncTask().execute();

                //Set the Text to the extracted text again
                mImageText.setText(intent.getStringExtra(FIREBASE_TEXT));

                //Set the Audio name edit text, and seek bars, to default states
                mAudioNameEditTxt.setText(null);
                mPitchSeekBar.setProgress(PITCH_SEEKBAR_DEFAULT);
                mSpeedSeekBar.setProgress(SPEED_SEEKBAR_DEFAULT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

        //when an item is selected in the Language Spinner, then start new asynctask which gets the voice type based on the language
        String languageCode = mListOfLanguageCodes.get(i);
        new GetVoiceTypeAsyncTask(languageCode).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * GetLanguageCodeAsyncTask AsyncTask which handles getting the language code from API call
     */
    private class GetLanguageCodeAsyncTask extends AsyncTask<Void, Void, Void> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground(Void... voids) {
            //get the language code from the presenter
            mPresenter.getLanguageCodeDoInBackground(mApiService, API_KEY, mListOfLanguageCodes, mListOfLanguages,
                    mLanguageSpinnerAdapter, mLanguageCodeSpinner, CreateAudioActivity.this);
            return null;
        }
    }

    /**
     * GetVoiceTypeAsyncTask AsyncTask which handles getting the voice type according to the retrieved language code, from API call
     */
    private class GetVoiceTypeAsyncTask extends AsyncTask<Void, Void, Void> {

        String languageCode;

        /**
         * Async Constructor
         */
        public GetVoiceTypeAsyncTask(String s) {
            this.languageCode = s;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mVoiceTypeSpinner.setEnabled(true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground(Void... voids) {
            //get the voice type from the presenter using language code
            mPresenter.getVoiceTypeDoInBackground(mApiService, languageCode, API_KEY, mListOfVoiceTypes,
                    mVoiceSpinnerAdapter, mVoiceTypeSpinner, CreateAudioActivity.this);

            return null;
        }
    }

    /**
     * SynthesizeTextAsyncTask AsyncTask which handles posting all data to API, and retrieving base64 encoded audio string
     */
    private class SynthesizeTextAsyncTask extends AsyncTask<Void, Void, Void> {

        Input input;
        Voice voice;
        AudioConfig audioConfig;
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        String mAudioName;
        ProgressDialog dialog;

        /**
         * SynthesizeTextAsyncTask Constructor
         */
        public SynthesizeTextAsyncTask(Input input, Voice voice, AudioConfig audioConfig) {
            this.input = input;
            this.voice = voice;
            this.audioConfig = audioConfig;
            this.dialog = new ProgressDialog(CreateAudioActivity.this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getResources().getString(R.string.synthesize_string));
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

            //sends the input, voice, audio config to the api and retrieves a audio string
            //this string is then used to create a recyclerview item and added to database
            mAudioName = mAudioNameEditTxt.getText().toString();
            mPresenter.synthesizeTextDoInBackground(input, voice, audioConfig, mApiService, API_KEY,
                    CreateAudioActivity.this, mAudioName, db);
            return null;
        }
    }
}
