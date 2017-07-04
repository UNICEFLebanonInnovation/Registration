package com.example.rzahab.generator;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    Locale current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            current = getResources().getConfiguration().getLocales().get(0);
        } else {
            current = getResources().getConfiguration().locale;
        }
        Log.d("LANG", "Lang: " + current.toLanguageTag());
        RadioButton selectedLang;
        selectedLang = (current.toLanguageTag().equals("ar")) ?
                (RadioButton) findViewById(R.id.arabic) : (RadioButton) findViewById(R.id.english);
        selectedLang.setChecked(true);
    }

    public void switchLang(View v) {
        RadioGroup gender_group = (RadioGroup) findViewById(R.id.radio_language);
        int selectedGender = gender_group.getCheckedRadioButtonId();

        String lang = (selectedGender == R.id.arabic) ? "ar" : "en";

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }

}