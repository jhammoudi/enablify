package com.hammoudij.enablify.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "audio_table")
public class Audio {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "length")
    private String mLength;

    @ColumnInfo(name = "time")
    private String mTime;

    @ColumnInfo(name = "file_path")
    private String mFilePath;

    public Audio(String name, String length, String time, String filePath) {
        this.mName = name;
        this.mLength = length;
        this.mTime = time;
        this.mFilePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getLength() {
        return mLength;
    }

    public void setLength(String length) {
        this.mLength = length;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }
}
