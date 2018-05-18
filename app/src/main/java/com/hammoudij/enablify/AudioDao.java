package com.hammoudij.enablify;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hammoudij.enablify.model.Audio;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AudioDao {

    @Query("SELECT * FROM audio_table")
    List<Audio> getAllAudio();

    @Insert
    void insertAll(Audio... audios);

}
