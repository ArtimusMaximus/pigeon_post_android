package com.wgu.d424.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wgu.d424.data.entities.Profile;

@Dao
public interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveProfile(Profile profile);

    @Update
    void updateProfile(Profile profile);

    @Query("SELECT * FROM profile WHERE id = 1 LIMIT 1")
    Profile getProfile();

    @Query("SELECT COUNT(*) FROM profile WHERE id = 1")
    int profileExists();
}