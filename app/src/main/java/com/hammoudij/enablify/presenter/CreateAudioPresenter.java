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

/**
 * The Create Audio Presenter which handles the processing corresponding to the synthesizing of text to audio
 */

public class CreateAudioPresenter implements MainMVP.CreateAudioPresenter {

    private static final String ENABLIFY_PACKAGE = "Enablify";
    private static final String MP3 = ".mp3";


    /**
     * Handles the get language code API call
     */
    public void getLanguageCodeDoInBackground(ApiInterface apiService, String API_KEY, final List<String> listOfLanguageCodes,
                                              final List<String> listOfLanguages, final Spinner languageCodeSpinner, final Activity activity) {

        //creates a new call
        Call<RetrofitModel> call = apiService.getVoices(API_KEY);
        call.enqueue(new Callback<RetrofitModel>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {

                //if call is successful, store the list of voices into a list
                ArrayAdapter<String> languageSpinnerAdapter;
                List<Voice> voices = response.body().getVoices();

                //loop over each voice
                for (int i = 0; i < voices.size(); i++) {

                    //get the language code from each voice, eg en-US, en-AU
                    String languageCode = voices.get(i).getLanguageCodes().get(0);

                    //get the first two characters of this code, eg en, it, ja
                    String lng = languageCode.substring(0, 2);

                    //create a locale object from this country code
                    Locale loc = new Locale(lng);

                    //get the display language and capitalise first character. eg English, Espanyol, Turkce, Italiano
                    String name = loc.getDisplayLanguage(loc);
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);

                    //add each display language to a list of languages
                    // also add the language codes to list of language codes, which is needed to make API call later
                    if (!listOfLanguages.contains(name)) {
                        //store the language in a list if its not found already, to remove duplicates
                        listOfLanguages.add(name);
                        listOfLanguageCodes.add(lng);
                    }
                }

                //setup the spinner
                languageSpinnerAdapter = new ArrayAdapter<String>(activity.getApplicationContext(),
                        android.R.layout.simple_spinner_item,
                        listOfLanguages);

                languageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                languageCodeSpinner.setAdapter(languageSpinnerAdapter); // this will set list of values to spinner
                languageCodeSpinner.setEnabled(true);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onFailure(Call<RetrofitModel> call, Throwable t) {
                // Log error here since request failed
                Log.d(activity.getLocalClassName(), t.getMessage());
                // if failed, ask user to refresh, which will start the API call again
                Toast.makeText(activity, t.getMessage() + activity.getString(R.string.please_refresh_string), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Handles the get voice type API call
     */
    public void getVoiceTypeDoInBackground(ApiInterface apiService, String languageCode, String API_KEY, final List<String> listOfVoiceTypes,
                                           final Spinner voiceTypeSpinner, final Activity activity) {
        //creates a new call
        Call<RetrofitModel> call = apiService.getVoiceType(languageCode, API_KEY);
        call.enqueue(new Callback<RetrofitModel>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {

                //if call is successful
                ArrayAdapter<String> voiceSpinnerAdapter;

                //clear the existing list, as a new set of voice types will be added
                //each language has a list of compatible voice types, so voice types depends on the language
                listOfVoiceTypes.clear();

                //store list of voices into list
                List<Voice> voices = response.body().getVoices();

                //loop over each voice
                for (int i = 0; i < voices.size(); i++) {
                    if (!listOfVoiceTypes.contains(voices.get(i).getName())) {
                        //store the voice in a list if its not found already, to remove duplicates
                        listOfVoiceTypes.add(voices.get(i).getName());
                    }
                }

                //setup the spinner
                voiceSpinnerAdapter = new ArrayAdapter<String>(activity.getApplicationContext(),
                        android.R.layout.simple_spinner_item,
                        listOfVoiceTypes);

                voiceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                voiceTypeSpinner.setAdapter(voiceSpinnerAdapter); // this will set list of values to spinner
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onFailure(Call<RetrofitModel> call, Throwable t) {
                // Log error here since request failed
                Log.d(activity.getLocalClassName(), t.getMessage());
                // if failed, ask user to refresh, which will start the API call again
                Toast.makeText(activity, t.getMessage() + activity.getString(R.string.please_refresh_string), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Returns a fileName for the new Audio to be inserted to the database
     */
    private String createFileName(Activity activity) {
        //building up the directory
        File root = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File folder = new File(root.getAbsoluteFile(), ENABLIFY_PACKAGE);

        //make the folder if it doesn't exist
        if (!folder.exists()) {
            folder.mkdirs();
        }

        //set the file name as the current time in milliseconds to remove risk of duplicates
        //return the full file path with file name
        return folder.getAbsolutePath() + "/" +
                String.valueOf(System.currentTimeMillis() + MP3);
    }

    /**
     * Calculates the duration of the audio file with the filename parameter
     */
    private String createDurationString(String fileName, Activity activity) {
        //creates a new mediaplayer with the audio file
        MediaPlayer mp = MediaPlayer.create(activity, Uri.parse(fileName));
        //gets the duration in milliseconds
        int duration = mp.getDuration();
        mp.release();

        //converts the milliseconds into minutes and seconds
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MILLISECONDS.toMinutes(duration) * 60);

        //returns a string to be displayed in the recyclerview in the AudioListActivity class
        return String.valueOf(minutes) + activity.getString(R.string.minute_string) + String.valueOf(seconds) + activity.getString(R.string.seconds_string);
    }

    /**
     * Handles the get language code API call
     */
    public void synthesizeTextDoInBackground(Input input, Voice voice, AudioConfig audioConfig, ApiInterface apiService, String API_KEY,
                                             final Activity activity, final String audioName, final AppDatabase db) {
        //creates a retrofitModel with parameters
        RetrofitModel retrofitModel = new RetrofitModel();
        retrofitModel.setInput(input);
        retrofitModel.setVoice(voice);
        retrofitModel.setAudioConfig(audioConfig);

        //make a POST api class, with this retrofitModel
        Call<RetrofitModel> call = apiService.synthesizeText(retrofitModel, API_KEY);
        call.enqueue(new Callback<RetrofitModel>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {
                //if call is successful

                //gets the audio content and store into string
                //audio content is Base64-Encoded Audio
                String audioContent = response.body().getAudioContent();
                Log.d(activity.getLocalClassName(), audioContent);

                //decode the Base64-Encoded Audio as bytes
                byte[] audioAsBytes = Base64.decode(audioContent, 0);

                //get a fileName
                String fileName = createFileName(activity);

                //store the decoded bytes into a file with fileName, into local storage
                try (OutputStream out = new FileOutputStream(fileName)) {
                    out.write(audioAsBytes);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //get the current date and time as string
                String dateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                //get the duration of the audio as string
                String time = createDurationString(fileName, activity);

                //create a new audio according to audio name, time, datetime, and filename
                Audio audio = new Audio(audioName, time, dateTime, fileName);

                //insert this audio object to the database, which is presented in the recyclerview in the AudioListActivity
                db.audioDao().insertAll(audio);

                //finish the current activity, and open the AudioListActivity to view the newly added Audio file
                Intent i = new Intent(activity.getApplicationContext(), AudioListActivity.class);
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                activity.finish();
                activity.startActivity(i);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onFailure(Call<RetrofitModel> call, Throwable t) {
                // Log error here since request failed
                Log.d(activity.getLocalClassName(), t.getMessage());
                // if API call failed, show the message to the user
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
