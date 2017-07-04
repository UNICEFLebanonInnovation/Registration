package com.example.rzahab.generator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class PreviewActivity extends AppCompatActivity {

    Generator app;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Intent intent = getIntent();
        UID = intent.getStringExtra("UID");
        app = (Generator) this.getApplication();

        TextView uidTxt = (TextView) findViewById(R.id.uuid);
        uidTxt.setText("" + UID);

    }

    public void goToKobo(View v) {
        app.moveToKobo(this, UID + "");
    }
}

