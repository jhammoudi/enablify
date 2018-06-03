package com.hammoudij.enablify.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.activity.AudioAdapter;
import com.hammoudij.enablify.db.AppDatabase;
import com.hammoudij.enablify.model.Audio;

import java.io.File;
import java.util.List;

/**
 * The Audio Presenter which handles the processing corresponding to the AudioAdapter and AudioListActivity
 */

public class AudioPresenter implements MainMVP.AudioPresenter {

    public static final String AUDIO_MP3 = "audio/mp3";
    public static final String FILEPROVIDER = ".fileprovider";

    /**
     * Sets up the AudioListActivity with the RecyclerView and Adapter
     */
    public void setUpActivity(RecyclerView recyclerView, Activity activity) {

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        AppDatabase db = AppDatabase.getDatabase(activity);

        List<Audio> audioList = db.audioDao().getAllAudio();

        //Initialises the Audio Adapter, and sets the adapter to the parameter recyclerView.
        AudioAdapter audioAdapter = new AudioAdapter(audioList, db);
        recyclerView.setAdapter(audioAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Removes the Audio from recyclerView
     */
    public void removeItem(int position, List<Audio> audioList, AppDatabase db, AudioAdapter audioAdapter) {

        //Removes the audio object at position parameter, from the audio list
        Audio audio = audioList.get(position);
        audioList.remove(position);

        //deletes the audio object from the database
        db.audioDao().delete(audio);

        //notify that the data has changed, to update the recyclerView
        audioAdapter.notifyItemRemoved(position);
    }

    /**
     * Shares the Audio from recyclerView
     */
    public void shareItem(View v, int position, List<Audio> audioList) {

        //Gets the Audio at position parameter and creates an audio file based on its file path
        Audio audio = audioList.get(position);
        String filePath = audio.getFilePath();
        File file = new File(filePath);

        //create an intent to share this audio file
        Uri audioUri = FileProvider.getUriForFile(v.getContext(), v.getContext().getApplicationInfo().packageName + FILEPROVIDER, file);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, audioUri);
        share.setType(AUDIO_MP3);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //starts the share intent, which allows sharing to messenging apps, mail, and cloud storages
        v.getContext().startActivity(Intent.createChooser(share, v.getContext().getResources().getString(R.string.share_audio_string)));
    }

    /**
     * Plays the Audio from the recyclerView
     */
    public void clickItem(View v, int position, List<Audio> audioList) {

        //Gets the Audio at position parameter and creates an audio file based on its file path
        Audio audio = audioList.get(position);
        String filePath = audio.getFilePath();
        File audioFile = new File(filePath);

        //create an intent to play this audio file
        Uri audioUri = FileProvider.getUriForFile(v.getContext(), v.getContext().getApplicationInfo().packageName + FILEPROVIDER, audioFile);
        Intent play = new Intent(Intent.ACTION_VIEW);
        play.setDataAndType(audioUri, AUDIO_MP3);
        play.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //starts the play intent, which allows users to play the audio using any pre-installed media players
        v.getContext().startActivity(play);
    }
}
