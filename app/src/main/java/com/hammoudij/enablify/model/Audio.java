package com.hammoudij.enablify.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "audio_table")
public class Audio {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "audio_name")
    private String mAudioName;

    @ColumnInfo(name = "category")
    private String mCategory;


    public Audio( String audioName, String category) {
        this.mAudioName = audioName;
        this.mCategory = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAudioName() {
        return mAudioName;
    }

    public void setAudioName(String audioName) {
        this.mAudioName = audioName;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }
}
