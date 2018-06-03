package com.hammoudij.enablify.presenter;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.activity.AudioListActivity;
import com.hammoudij.enablify.api.TextToSpeech.ApiInterface;
import com.hammoudij.enablify.db.AppDatabase;
import com.hammoudij.enablify.model.Audio;
import com.hammoudij.enablify.model.AudioConfig;
import com.hammoudij.enablify.model.Input;
import com.hammoudij.enablify.model.RetrofitModel;
import com.hammoudij.enablify.model.Voice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAudioPresenter implements MainMVP.CreateAudioPresenter {

    public static final String ENABLIFY_PACKAGE = "Enablify";
    public static final String MP3 = ".mp3";

    public void getLanguageCodeDoInBackground(ApiInterface apiService,
                                              String API_KEY, final List<String> listOfLanguageCodes,
                                              final List<String> listOfLanguages,
                                              final ArrayAdapter<String> langSpinnerAdapter,
                                              final Spinner languageCodeSpinner,
                                              final Activity activity){

        Call<RetrofitModel> call = apiService.getVoices(API_KEY);
        call.enqueue(new Callback<RetrofitModel>() {
            @Override
            public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {

                ArrayAdapter<String> languageSpinnerAdapter = langSpinnerAdapter;
                List<Voice> voices = response.body().getVoices();
                for (int i = 0; i < voices.size(); i++) {
                    String languageCode = voices.get(i).getLanguageCodes().get(0);

                    String lng = languageCode.substring(0,2);
                    Locale loc = new Locale(lng);
                    String name = loc.getDisplayLanguage(loc);
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);

                    if (!listOfLanguages.contains(name)) {
                        listOfLanguages.add(name);
                        listOfLanguageCodes.add(lng);
                    }
                }

                languageSpinnerAdapter = new ArrayAdapter<String>(activity.getApplicationContext(),
                        android.R.layout.simple_spinner_item,
                        listOfLanguages);

                languageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                languageCodeSpinner.setAdapter(languageSpinnerAdapter); // this will set list of values to spinner
                languageCodeSpinner.setEnabled(true);
            }
            @Override
            public void onFailure(Call<RetrofitModel> call, Throwable t) {
                // Log error here since request failed
                Log.d(activity.getLocalClassName(), t.getMessage());
                Toast.makeText(activity, t.getMessage() + activity.getString(R.string.please_refresh_string), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getVoiceTypeDoInBackground(ApiInterface apiService,
                                           String languageCode,
                                           String API_KEY,
                                           final List<String> listOfVoiceTypes,
                                           final ArrayAdapter<String> voiceSpinnerAdapt,
                                           final Spinner voiceTypeSpinner,
                                           final Activity activity){
        Call<RetrofitModel> call = apiService.getVoiceType(languageCode, API_KEY);
        call.enqueue(new Callback<RetrofitModel>() {
            @Override
            public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {

                ArrayAdapter<String> voiceSpinnerAdapter = voiceSpinnerAdapt;
                listOfVoiceTypes.clear();

                List<Voice> voices = response.body().getVoices();
                for (int i = 0; i < voices.size(); i++) {
                    if (!listOfVoiceTypes.contains(voices.get(i).getName())) {
                        listOfVoiceTypes.add(voices.get(i).getName());
                    }
                }

                voiceSpinnerAdapter = new ArrayAdapter<String>(activity.getApplicationContext(),
                        android.R.layout.simple_spinner_item,
                        listOfVoiceTypes);

                voiceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                voiceTypeSpinner.setAdapter(voiceSpinnerAdapter); // this will set list of values to spinner
            }

            @Override
            public void onFailure(Call<RetrofitModel> call, Throwable t) {
                // Log error here since request failed
                Log.d(activity.getLocalClassName(), t.getMessage());
                Toast.makeText(activity, t.getMessage() + activity.getString(R.string.please_refresh_string), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String createFileName(Activity activity) {
        File root = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        //File root = Environment.getExternalStorageDirectory();
        File folder = new File(root.getAbsoluteFile(), ENABLIFY_PACKAGE);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder.getAbsolutePath()  + "/"+
                String.valueOf(System.currentTimeMillis() + MP3);
    }

    private String createDurationString(String fileName, Activity activity) {
        MediaPlayer mp = MediaPlayer.create(activity, Uri.parse(fileName));
        int duration = mp.getDuration();
        mp.release();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MILLISECONDS.toMinutes(duration)* 60) ;

        return String.valueOf(minutes) + " min " + String.valueOf(seconds)+ " sec";
    }

    public void synthesizeTextDoInBackground(Input input,
                                             Voice voice,
                                             AudioConfig audioConfig,
                                             ApiInterface apiService,
                                             String API_KEY,
                                             final Activity activity,
                                             final String audioName,
                                             final AppDatabase db){
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
                Log.d(activity.getLocalClassName(), audioContent);

                //use audiocontent and create audio file and save to local storage, save filepath
                byte[] audioAsBytes = Base64.decode(audioContent, 0);

                String fileName = createFileName(activity);


                try (OutputStream out = new FileOutputStream(fileName)) {
                    out.write(audioAsBytes);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String dateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                String time = createDurationString(fileName, activity);
                Audio audio = new Audio(audioName, time, dateTime, fileName);

                db.audioDao().insertAll(audio);
                Intent i = new Intent(activity.getApplicationContext(), AudioListActivity.class);
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                activity.finish();
                activity.startActivity(i);
            }

            @Override
            public void onFailure(Call<RetrofitModel> call, Throwable t) {
                // Log error here since request failed
                Log.d(activity.getLocalClassName(), t.getMessage());
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
