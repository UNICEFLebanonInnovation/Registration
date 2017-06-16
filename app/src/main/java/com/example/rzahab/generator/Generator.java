package com.example.rzahab.generator;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rzahab on 6/13/2017.
 */

public class Generator extends Application {
    private ArrayList<SuggestedUser> suggestedUsers;
    private HashMap<String, String> userData;
    private double rateThreshold;

    @Override
    public void onCreate() {
        super.onCreate();
        this.setRateThreshold(50);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public HashMap<String, String> getUserData() {
        return userData;
    }

    public void setUserData(HashMap<String, String> userData) {
        this.userData = userData;
    }

    public double getRateThreshold() {
        return rateThreshold;
    }

    private void setRateThreshold(double rateThreshold) {
        this.rateThreshold = rateThreshold;
    }

    public ArrayList<SuggestedUser> getSuggestedUsers() {
        return this.suggestedUsers;
    }

    public void setSuggestedUsers(ArrayList<SuggestedUser> users) {
        this.suggestedUsers = users;
    }
    public void moveToKobo(Activity currentActivity, String UID)
    {
        ClipboardManager clipboard = (ClipboardManager) currentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ID", UID);
        Toast.makeText(currentActivity, "Value copied to ClipBoard: "+UID, Toast.LENGTH_LONG).show();
        clipboard.setPrimaryClip(clip);

        startNewActivity(currentActivity,"org.koboc.collect.android");
    }

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
