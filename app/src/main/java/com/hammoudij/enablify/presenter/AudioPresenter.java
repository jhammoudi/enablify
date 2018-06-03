package com.hammoudij.enablify.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.activity.AudioAdapter;
import com.hammoudij.enablify.activity.AudioListActivity;
import com.hammoudij.enablify.db.AppDatabase;
import com.hammoudij.enablify.model.Audio;

import java.io.File;
import java.util.List;

public class AudioPresenter implements MainMVP.AudioPresenter {

    public static final String AUDIO_MP3 = "audio/mp3";
    public static final String FILEPROVIDER = ".fileprovider";

    public void setUpActivity(RecyclerView recyclerView, Activity activity) {

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        AppDatabase db = AppDatabase.getDatabase(activity);

        List<Audio> audioList = db.audioDao().getAllAudio();
        AudioAdapter audioAdapter = new AudioAdapter(audioList, db);
        recyclerView.setAdapter(audioAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void removeItem(int position, List<Audio> audioList, AppDatabase db, AudioAdapter audioAdapter) {

        Audio audio = audioList.get(position);
        audioList.remove(position);
        db.audioDao().delete(audio);
        audioAdapter.notifyItemRemoved(position);
    }

    public void shareItem(View v, int position, List<Audio> audioList) {

        Audio audio = audioList.get(position);
        String filePath = audio.getFilePath();
        File file = new File(filePath);
        Uri audioUri = FileProvider.getUriForFile(v.getContext(),v.getContext().getApplicationInfo().packageName + FILEPROVIDER,file);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, audioUri);
        share.setType(AUDIO_MP3);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        v.getContext().startActivity(Intent.createChooser(share, v.getContext().getResources().getString(R.string.share_audio_string)));
    }

    public void clickItem(View v, int position, List<Audio> audioList) {
        Audio audio = audioList.get(position);
        String filePath = audio.getFilePath();

        File audioFile = new File(filePath);

        Uri audioUri = FileProvider.getUriForFile(v.getContext(),v.getContext().getApplicationInfo().packageName + FILEPROVIDER,audioFile);

        Intent play = new Intent(Intent.ACTION_VIEW);
        play.setDataAndType(audioUri,AUDIO_MP3);
        play.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        v.getContext().startActivity(play);
    }
}
