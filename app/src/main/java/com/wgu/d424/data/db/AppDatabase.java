package com.wgu.d424.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.wgu.d424.data.dao.NoteDao;
import com.wgu.d424.data.entities.Note;

@Database(entities = {Note.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;
    public abstract NoteDao noteDao();

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