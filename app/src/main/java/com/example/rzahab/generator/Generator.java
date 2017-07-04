package com.example.rzahab.generator;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rzahab on 6/13/2017.
 */

public class Generator extends Application {
    public String TAG;
    private ArrayList<SuggestedUser> suggestedUsers;
    private HashMap<String, String> userData;
    private double rateThreshold;
    private String generatorURL;
    public FirebaseAuth.AuthStateListener authListener;
    public FirebaseAuth auth;

    public String getGeneratorURL() {
        return generatorURL;
    }

    public void setGeneratorURL(String generatorURL) {
        this.generatorURL = generatorURL;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.setRateThreshold(50);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        TAG = "MAIN";
        generatorURL = "https://id-gen.herokuapp.com/generate";

    }

    public void authenticateUser(final Activity currentActivity) {
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        final TextView helloUserText = (TextView) currentActivity.findViewById(R.id.hello_label);
        final ProgressBar progressBar = (ProgressBar) currentActivity.findViewById(R.id.progressBar);
        authListener = new FirebaseAuth.AuthStateListener() {
            public FirebaseUser AuthCurrentUser;

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                AuthCurrentUser = firebaseAuth.getCurrentUser();
                if (AuthCurrentUser == null) {
                    // if user is null launch login activity
                    backToLogin(currentActivity);
                } else {
                    helloUserText.setText(getString(R.string.hello) + AuthCurrentUser.getEmail() + "");
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
    }

    public void startAuth() {
        auth.addAuthStateListener(authListener);
    }

    public void signOutAuth() {
        auth.signOut();
    }

    public void endAuth() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public void backToLogin(Activity currentActivity) {
        startActivity(new Intent(currentActivity, LoginActivity.class));
        currentActivity.finish();
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

    public void moveToKobo(Activity currentActivity, String UID) {
        ClipboardManager clipboard = (ClipboardManager) currentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ID", UID);
        Toast.makeText(currentActivity, this.getResources().getString(R.string.id_copied) + UID, Toast.LENGTH_LONG).show();
        clipboard.setPrimaryClip(clip);

        startNewActivity(currentActivity, "org.koboc.collect.android");
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
