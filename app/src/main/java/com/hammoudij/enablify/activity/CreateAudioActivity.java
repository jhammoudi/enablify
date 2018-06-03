package com.hammoudij.enablify.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
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
import com.hammoudij.enablify.model.Audio;
import com.hammoudij.enablify.model.AudioConfig;
import com.hammoudij.enablify.model.Input;
import com.hammoudij.enablify.model.RetrofitModel;
import com.hammoudij.enablify.model.Voice;
import com.hammoudij.enablify.presenter.CreateAudioPresenter;
import com.warkiz.widget.IndicatorSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hammoudij.enablify.activity.MainCameraActivity.FIREBASE_TEXT;

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

    ApiInterface mApiService = ApiClient.getClient().create(ApiInterface.class);

    ArrayAdapter<String> mLanguageSpinnerAdapter;
    ArrayAdapter<String> mVoiceSpinnerAdapter;

    Intent intent;

    private final static String API_KEY = "AIzaSyCNvZKnuFSzUffWpiKWrYLKCg4KeEjaNJM";

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

    private void setupMVP() {
        mPresenter = new CreateAudioPresenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu.
        getMenuInflater().inflate(R.menu.menu_create_audio, menu);
        return true;
    }

    @OnClick(R.id.create_audio_btn)
    public void onCreateAudioBtnClick() {

        if (mAudioNameEditTxt.getText().toString().equals("")) {

            mAudioNameEditTxt.requestFocus();
            mAudioNameTextInput.setError(getResources().getString(R.string.audio_name_error_string));

        } else if (!mVoiceTypeSpinner.isEnabled() && !mLanguageCodeSpinner.isEnabled()) {
            Toast.makeText(CreateAudioActivity.this, getResources().getString(R.string.download_error_string), Toast.LENGTH_LONG).show();
        } else {

            Input input = new Input();
            input.setText(intent.getStringExtra(FIREBASE_TEXT));

            Voice voice = new Voice();
            voice.setLanguageCode(mListOfLanguageCodes.get(mLanguageCodeSpinner.getSelectedItemPosition()));
            voice.setName(mVoiceTypeSpinner.getSelectedItem().toString());

            AudioConfig audioConfig = new AudioConfig();
            audioConfig.setAudioEncoding(ENCODING_MP3);
            audioConfig.setPitch(mPitchSeekBar.getProgress());
            audioConfig.setSpeakingRate(mSpeedSeekBar.getProgress());

            new SynthesizeTextAsyncTask(input, voice, audioConfig).execute();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_refresh:
                new GetLanguageCodeAsyncTask().execute();
                mImageText.setText(intent.getStringExtra(FIREBASE_TEXT));
                mAudioNameEditTxt.setText(null);
                mPitchSeekBar.setProgress(PITCH_SEEKBAR_DEFAULT);
                mSpeedSeekBar.setProgress(SPEED_SEEKBAR_DEFAULT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

        String languageCode = mListOfLanguageCodes.get(i);
        new GetVoiceTypeAsyncTask(languageCode).execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class GetLanguageCodeAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mPresenter.getLanguageCodeDoInBackground(mApiService,API_KEY,mListOfLanguageCodes,mListOfLanguages,
                    mLanguageSpinnerAdapter,mLanguageCodeSpinner,CreateAudioActivity.this);
            return null;
        }
    }

    private class GetVoiceTypeAsyncTask extends AsyncTask<Void, Void, Void> {

        String languageCode;

        public GetVoiceTypeAsyncTask(String s) {
            this.languageCode = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mVoiceTypeSpinner.setEnabled(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mPresenter.getVoiceTypeDoInBackground(mApiService,languageCode,API_KEY,mListOfVoiceTypes,
                    mVoiceSpinnerAdapter,mVoiceTypeSpinner,CreateAudioActivity.this);

            return null;
        }
    }

    private class SynthesizeTextAsyncTask extends AsyncTask<Void, Void, Void> {

        Input input;
        Voice voice;
        AudioConfig audioConfig;
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        String mAudioName;
        ProgressDialog dialog;

        public SynthesizeTextAsyncTask(Input input, Voice voice, AudioConfig audioConfig) {
            this.input = input;
            this.voice = voice;
            this.audioConfig = audioConfig;
            this.dialog = new ProgressDialog(CreateAudioActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getResources().getString(R.string.synthesize_string));
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mAudioName = mAudioNameEditTxt.getText().toString();
            mPresenter.synthesizeTextDoInBackground(input,voice,audioConfig,mApiService,API_KEY,
                    CreateAudioActivity.this,mAudioName,db);
            return null;
        }
    }
}
