package com.example.rzahab.generator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PreviewActivity extends AppCompatActivity {

    Generator app;
    String UID;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Intent intent = getIntent();
        UID = intent.getStringExtra("UID");
        app = (Generator) this.getApplication();

        TextView uidTxt = (TextView) findViewById(R.id.uuid);
        uidTxt.setText(UID);

    }

    public void goToKobo(View v) {
        app.moveToKobo(this, UID + "");
    }

    public void goHome(View v) {
        startActivity(new Intent(this, SearchUserActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            startActivity(new Intent(this, SearchUserActivity.class));
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_search, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    //sign out method
    public void signOut(View v) {
        app.signOutAuth();
    }


    @Override
    public void onStart() {
        super.onStart();
        app.startAuth();
    }

    @Override
    public void onStop() {
        super.onStop();
        app.endAuth();
    }
}

