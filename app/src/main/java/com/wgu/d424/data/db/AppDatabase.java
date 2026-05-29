package com.wgu.d424.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.wgu.d424.data.dao.NoteDao;
import com.wgu.d424.data.dao.ProfileDao;
import com.wgu.d424.data.entities.Note;
import com.wgu.d424.data.entities.Profile;

@Database(entities = {Note.class, Profile.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;
    public abstract NoteDao noteDao();
    public abstract ProfileDao profileDao();

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "homing_pigeon_database"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return instance;
    }
}