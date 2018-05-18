package com.hammoudij.enablify.activity;

import android.arch.persistence.room.Room;
import android.speech.tts.Voice;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.hammoudij.enablify.AppDatabase;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.model.Audio;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioListActivity extends AppCompatActivity {

    //private ArrayList<Audio> mAudioList = new ArrayList<>();

    @BindView(R.id.audio_recycler_view)
    public RecyclerView mRecyclerView;

    private AudioAdapter mAudioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_list);
        ButterKnife.bind(this);
        setupActionBar();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,"production")
                .allowMainThreadQueries()
                .build();

        //db.audioDao().insertAll(new Audio("Audio 1", "Normal"));

        List<Audio> audioList = db.audioDao().getAllAudio();

        mAudioAdapter = new AudioAdapter(audioList);
        mRecyclerView.setAdapter(mAudioAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());






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
}
