package com.wgu.d424.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile")
public class Profile {

    @PrimaryKey
    private int id = 1;

    private String email;
    private String pinHash;

    public Profile(String email, String pinHash) {
        this.email = email;
        this.pinHash = pinHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }
}