package com.hammoudij.enablify;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.hammoudij.enablify.model.Audio;

@Database(entities = {Audio.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AudioDao audioDao();
}
