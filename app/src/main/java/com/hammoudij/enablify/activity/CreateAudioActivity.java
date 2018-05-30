package com.hammoudij.enablify.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.hammoudij.enablify.model.AudioConfig;
import com.hammoudij.enablify.model.Input;
import com.hammoudij.enablify.model.Voice;
import com.hammoudij.enablify.model.RetrofitModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hammoudij.enablify.activity.MainCameraActivity.FIREBASE_TEXT;

public class CreateAudioActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.audio_name)
    public EditText mAudioNameEditTxt;

    @BindView(R.id.text_from_image)
    public TextView mImageText;

    @BindView(R.id.language_code_spinner)
    public Spinner mLanguageCodeSpinner;

    @BindView(R.id.voice_type_spinner)
    public Spinner mVoiceTypeSpinner;

    @BindView(R.id.create_audio_btn )
    public Button mCreateAudioBtn;

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

        retrieveLanguageCodes();
        mLanguageCodeSpinner.setOnItemSelectedListener(this);
        mVoiceTypeSpinner.setEnabled(false);
        mLanguageCodeSpinner.setEnabled(false);
        mCreateAudioBtn.setEnabled(false);
    }

    private void retrieveLanguageCodes() {

        Call<RetrofitModel> call = apiService.getVoices(API_KEY);
        call.enqueue(new Callback<RetrofitModel>() {
            @Override
            public void onResponse(Call<RetrofitModel>call, Response<RetrofitModel> response) {
                List<Voice> voices = response.body().getVoices();
                for (int i = 0; i < voices.size(); i++) {
                    if(!mListOfLanguageCodes.contains(voices.get(i).getLanguageCodes().get(0))) {
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
            public void onFailure(Call<RetrofitModel>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

    }

    @OnClick(R.id.create_audio_btn)
    public void onCreateAudioBtnClick() {
//        mAudioName = mAudioNameEditTxt.getText().toString();
//
//        AppDatabase db = AppDatabase.getDatabase(this);
//
//        db.audioDao().insertAll(
//                new Audio(mAudioName, "00:14", "Jan 10, 2018 - 5:30 PM", ""));
//
//        Intent i = new Intent(this, AudioListActivity.class);
//        finish();
//        startActivity(i);

        Input input = new Input();
        input.setText(intent.getStringExtra(FIREBASE_TEXT));

        Voice voice = new Voice();
        voice.setLanguageCode(mLanguageCodeSpinner.getSelectedItem().toString());
        voice.setName(mVoiceTypeSpinner.getSelectedItem().toString());

        AudioConfig audioConfig = new AudioConfig();
        audioConfig.setAudioEncoding("MP3");

        new SynthesizeTextAsyncTask(input,voice,audioConfig).execute();

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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        //Toast.makeText(parent.getContext(), parent.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();

        new GetVoiceTypeAsyncTask(parent.getItemAtPosition(i).toString()).execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
            mCreateAudioBtn.setEnabled(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Call<RetrofitModel> call = apiService.getVoiceType(languageCode,API_KEY);
            call.enqueue(new Callback<RetrofitModel>() {
                @Override
                public void onResponse(Call<RetrofitModel>call, Response<RetrofitModel> response) {

                    mListOfVoiceTypes.clear();

                    List<Voice> voices = response.body().getVoices();
                    for (int i = 0; i < voices.size(); i++) {
                        if(!mListOfVoiceTypes.contains(voices.get(i).getName())) {
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
                public void onFailure(Call<RetrofitModel>call, Throwable t) {
                    // Log error here since request failed
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

            Call<RetrofitModel> call = apiService.synthesizeText(retrofitModel,API_KEY);
            call.enqueue(new Callback<RetrofitModel>() {
                @Override
                public void onResponse(Call<RetrofitModel>call, Response<RetrofitModel> response) {

                    //Base64-Encoded Audio Content
                    String audioContent = response.body().getAudioContent();
                    Log.d("RESULT", audioContent);

                    Toast.makeText(CreateAudioActivity.this, audioContent, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(Call<RetrofitModel>call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });

            return null;
        }
    }
}
