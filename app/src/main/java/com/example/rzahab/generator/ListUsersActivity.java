package com.example.rzahab.generator;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ListUsersActivity extends AppCompatActivity implements Serializable {

    Map<String, String> filled_fields;
    Generator app;
    int day, month, year;
    Button date_picker;
    String gender_filter, dob_filter;
    private String TAG;
    private boolean doubleBackToExitPressedOnce = false;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        TAG = this.getLocalClassName();
        app = ((Generator) this.getApplication());
        app.setUserData(null);
        app.authenticateUser(this);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        date_picker = (Button) findViewById(R.id.date_picker);

    }

    private void showDate(int year, int month, int day) {
        dob_filter = String.format("%02d", day) + "" + String.format("%02d", month) + "" + year;

        date_picker.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    public void clearFilter(View v) {
        calendar.clear();
        date_picker.setText(R.string.pick_date);
        RadioButton female = (RadioButton) findViewById(R.id.female);
        RadioButton male = (RadioButton) findViewById(R.id.male);
        RadioButton any = (RadioButton) findViewById(R.id.any);

        female.setChecked(false);
        male.setChecked(false);
        any.setChecked(true);

        gender_filter = null;
        dob_filter = null;
    }

    public void search(View v) {
        RadioGroup gender_group = (RadioGroup) findViewById(R.id.gender);
        int selectedGender = gender_group.getCheckedRadioButtonId();

        gender_filter = (selectedGender == R.id.male) ? "male" : (selectedGender == R.id.female) ? "female" : null;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        if (gender_filter != null) {
            myRef = myRef.child(gender_filter);
            //if (dob_filter != null)
            //    myRef = myRef.child(dob_filter);
        }


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gender_tmp, dob_tmp, users_tmp;
                ArrayList<SuggestedUser> users_suggested = new ArrayList<>();
                for (DataSnapshot genderSnapshot : dataSnapshot.getChildren()) {
                    if (gender_filter == null) {
                        gender_tmp = genderSnapshot.getKey();//gender
                        Log.d(TAG, "Gender1: " + gender_tmp);
                        for (DataSnapshot dobSnapshot : genderSnapshot.getChildren()) {
                            dob_tmp = dobSnapshot.getKey();//dob
                            Log.d(TAG, "Dob1: " + dob_tmp);
                            if ((dob_filter != null && dob_filter.equals(dob_tmp)) || dob_filter == null)
                                for (DataSnapshot usersSnapshot : dobSnapshot.getChildren()) {
                                    SuggestedUser user = fetchUsers(usersSnapshot);
                                    users_suggested.add(user);
                                }
                        }
                    } else {
                        gender_tmp = genderSnapshot.getKey();
                        Log.d(TAG, "Gender2: " + gender_tmp);
                        dob_tmp = genderSnapshot.getKey();//dob
                        Log.d(TAG, "Dob: " + dob_tmp);
                        if ((dob_filter != null && dob_filter.equals(dob_tmp)) || dob_filter == null)
                            for (DataSnapshot usersSnapshot : genderSnapshot.getChildren()) {
                                SuggestedUser user = fetchUsers(usersSnapshot);
                                users_suggested.add(user);
                            }
                    }

                    Log.d(TAG, "Number found: " + users_suggested.size());
                }
                if (users_suggested.size() > 0) {
                    app.setSuggestedUsers(users_suggested);

                    Intent i = new Intent(ListUsersActivity.this, ListSuggestedActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(ListUsersActivity.this, R.string.non_found, Toast.LENGTH_LONG).show();
                    //addNew();
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    public SuggestedUser fetchUsers(DataSnapshot dataSnapshot) {
        HashMap<String, String> currentUser = new HashMap<>();
        String ID = dataSnapshot.getKey();

        Log.d(TAG, "==== ID: " + ID + "====");
        currentUser.put("ID", ID);
        for (DataSnapshot userAttribute : dataSnapshot.getChildren()) {
            Log.d(TAG, "=======" + userAttribute.getKey() + " : " + userAttribute.getValue());
            currentUser.put(userAttribute.getKey() + "", userAttribute.getValue() + "");
        }

        return new SuggestedUser(currentUser);
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.exit_back, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public boolean validateInput() {
        filled_fields = new HashMap<>();
        EditText first_name_txt, last_name_txt, father_name_txt, mother_name_txt;
        first_name_txt = (EditText) findViewById(R.id.first_name);
        last_name_txt = (EditText) findViewById(R.id.last_name);
        father_name_txt = (EditText) findViewById(R.id.father_name);
        mother_name_txt = (EditText) findViewById(R.id.mother_name);

        int required = 0;

        if (first_name_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("first_name", first_name_txt.getText().toString());
            required++;
        }
        if (last_name_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("last_name", last_name_txt.getText().toString());
            required++;
        }
        if (father_name_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("father_name", father_name_txt.getText().toString());
            required++;
        }
        if (mother_name_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("mother_name", mother_name_txt.getText().toString());
            required++;
        }

        return (required >= 3);
    }

    public HashMap<String, String> getUserData() {

        DatePicker dob_dialog = (DatePicker) findViewById(R.id.dob);
        String month = String.format("%02d", dob_dialog.getMonth() + 1);

        String dob = String.format("%02d", dob_dialog.getDayOfMonth()) + "" + month + "" + dob_dialog.getYear();

        RadioGroup gender_group = (RadioGroup) findViewById(R.id.gender);
        int selectedGender = gender_group.getCheckedRadioButtonId();

        String gender = (selectedGender == R.id.male) ? "male" : "female";

        HashMap<String, String> userData = new HashMap<>();

        userData.putAll(filled_fields);
        userData.put("dob", dob);
        userData.put("gender", gender);

        app.setUserData(userData);

        return userData;
    }


    @Override
    protected void onResume() {
        super.onResume();
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

    public void addNew() {
        startActivity(new Intent(this, NewUserActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            /*case R.id.action_delete_all:
                //deleteAllListItems();
                break;
                */
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.action_home:
                startActivity(new Intent(this, ListUsersActivity.class));
                finish();
                break;
            case R.id.action_language:
                startActivity(new Intent(this, LanguageActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
