package com.example.rzahab.generator;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Rzahab on 6/2/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
