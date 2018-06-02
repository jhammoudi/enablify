package com.hammoudij.enablify.activity;

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

import com.hammoudij.enablify.R;
import com.hammoudij.enablify.api.TextToSpeech.ApiClient;
import com.hammoudij.enablify.api.TextToSpeech.ApiInterface;
import com.hammoudij.enablify.db.AppDatabase;
import com.hammoudij.enablify.model.Audio;
import com.hammoudij.enablify.model.AudioConfig;
import com.hammoudij.enablify.model.Input;
import com.hammoudij.enablify.model.RetrofitModel;
import com.hammoudij.enablify.model.Voice;
import com.warkiz.widget.IndicatorSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hammoudij.enablify.activity.MainCameraActivity.FIREBASE_TEXT;

public class CreateAudioActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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

    private List<String> mListOfLanguageCodes = new ArrayList<>();
    private List<String> mListOfVoiceTypes = new ArrayList<>();

    ApiInterface apiService =
            ApiClient.getClient().create(ApiInterface.class);

    ArrayAdapter<String> mLanguageSpinnerAdapter;
    ArrayAdapter<String> mVoiceSpinnerAdapter;

    Intent intent;

    private static final String TAG = CreateAudioActivity.class.getSimpleName();
    private final static String API_KEY = "AIzaSyCNvZKnuFSzUffWpiKWrYLKCg4KeEjaNJM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_audio);
        setupActionBar();
        ButterKnife.bind(this);

        intent = getIntent();

        mImageText.setText(intent.getStringExtra(FIREBASE_TEXT));

        new GetLanguageCodeAsyncTask().execute();
        mLanguageCodeSpinner.setOnItemSelectedListener(this);
        mVoiceTypeSpinner.setEnabled(false);
        mLanguageCodeSpinner.setEnabled(false);
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
            mAudioNameTextInput.setError("Please enter Name");

        } else if (!mVoiceTypeSpinner.isEnabled() && !mLanguageCodeSpinner.isEnabled()) {
            Toast.makeText(CreateAudioActivity.this, "Please wait for Language Code and Voice Type to download", Toast.LENGTH_LONG).show();
        } else {

            Input input = new Input();
            input.setText(intent.getStringExtra(FIREBASE_TEXT));

            Voice voice = new Voice();
            voice.setLanguageCode(mLanguageCodeSpinner.getSelectedItem().toString());
            voice.setName(mVoiceTypeSpinner.getSelectedItem().toString());

            AudioConfig audioConfig = new AudioConfig();
            audioConfig.setAudioEncoding("MP3");
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
                mPitchSeekBar.setProgress(0);
                mSpeedSeekBar.setProgress(1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

        new GetVoiceTypeAsyncTask(parent.getItemAtPosition(i).toString()).execute();
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

            Call<RetrofitModel> call = apiService.getVoices(API_KEY);
            call.enqueue(new Callback<RetrofitModel>() {
                @Override
                public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {
                    List<Voice> voices = response.body().getVoices();
                    for (int i = 0; i < voices.size(); i++) {
                        if (!mListOfLanguageCodes.contains(voices.get(i).getLanguageCodes().get(0))) {
                            mListOfLanguageCodes.add(voices.get(i).getLanguageCodes().get(0));
                        }
                    }

                    mLanguageSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            mListOfLanguageCodes);

                    mLanguageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mLanguageCodeSpinner.setAdapter(mLanguageSpinnerAdapter); // this will set list of values to spinner
                    mLanguageCodeSpinner.setEnabled(true);
                }

                @Override
                public void onFailure(Call<RetrofitModel> call, Throwable t) {
                    // Log error here since request failed

                    Toast.makeText(CreateAudioActivity.this, t.getMessage() + "\n\n Please refresh", Toast.LENGTH_LONG).show();
                    Log.e(TAG, t.toString());
                }
            });

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

            Call<RetrofitModel> call = apiService.getVoiceType(languageCode, API_KEY);
            call.enqueue(new Callback<RetrofitModel>() {
                @Override
                public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {

                    mListOfVoiceTypes.clear();

                    List<Voice> voices = response.body().getVoices();
                    for (int i = 0; i < voices.size(); i++) {
                        if (!mListOfVoiceTypes.contains(voices.get(i).getName())) {
                            mListOfVoiceTypes.add(voices.get(i).getName());
                        }
                    }

                    mVoiceSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            mListOfVoiceTypes);

                    mVoiceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mVoiceTypeSpinner.setAdapter(mVoiceSpinnerAdapter); // this will set list of values to spinner
                }

                @Override
                public void onFailure(Call<RetrofitModel> call, Throwable t) {
                    // Log error here since request failed
                    Toast.makeText(CreateAudioActivity.this, t.getMessage() + "\n\n Please refresh", Toast.LENGTH_LONG).show();
                    Log.e(TAG, t.toString());
                }
            });

            return null;
        }
    }

    private class SynthesizeTextAsyncTask extends AsyncTask<Void, Void, Void> {

        Input input;
        Voice voice;
        AudioConfig audioConfig;
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        String mAudioName;

        public SynthesizeTextAsyncTask(Input input, Voice voice, AudioConfig audioConfig) {
            this.input = input;
            this.voice = voice;
            this.audioConfig = audioConfig;
        }

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

            RetrofitModel retrofitModel = new RetrofitModel();
            retrofitModel.setInput(input);
            retrofitModel.setVoice(voice);
            retrofitModel.setAudioConfig(audioConfig);

            Call<RetrofitModel> call = apiService.synthesizeText(retrofitModel, API_KEY);
            call.enqueue(new Callback<RetrofitModel>() {
                @Override
                public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {

                    //Base64-Encoded Audio Content
                    String audioContent = response.body().getAudioContent();
                    Log.d("RESULT", audioContent);


                    //use audiocontent and create audio file and save to local storage, save filepath
                    byte[] audioAsBytes = Base64.decode(audioContent, 0);
                    Log.d("RESULT-DECODE", audioAsBytes.toString());


                    File root = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    //File root = Environment.getExternalStorageDirectory();
                    File folder = new File(root.getAbsoluteFile(),"Enablify");

                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                    String fileName = folder.getAbsolutePath()  + "/"+
                            String.valueOf(System.currentTimeMillis() + ".mp3");


                    try (OutputStream out = new FileOutputStream(fileName)) {
                        out.write(audioAsBytes);
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("RESULT-FILEPATH", fileName);

                    //String filePath = "http://techslides.com/demos/samples/sample.mp3";

                    mAudioName = mAudioNameEditTxt.getText().toString();

                    String dateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                    String durationStr = "00:00";

                    Audio audio = new Audio(mAudioName, durationStr, dateTime, fileName);

                    db.audioDao().insertAll(audio);
                    Intent i = new Intent(getApplicationContext(), AudioListActivity.class);
                    i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    finish();
                    startActivity(i);
                }

                @Override
                public void onFailure(Call<RetrofitModel> call, Throwable t) {
                    // Log error here since request failed
                    Toast.makeText(CreateAudioActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

                    Log.e(TAG, t.toString());
                }
            });

            return null;
        }
    }
}
