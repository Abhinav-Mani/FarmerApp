package com.androlord.farmerapp.Utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class SetPersistence extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}