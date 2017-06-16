package com.example.rzahab.generator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputLayout signupInputLayoutEmail;
    private TextInputLayout signupInputLayoutPassword;
    ProgressBar progressBar;
    private EditText signupInputEmail;
    private EditText signupInputPassword;
    private String TAG;
    private AuthUser CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = this.getClass().getSimpleName();
        auth = FirebaseAuth.getInstance();

        //SuggestionLibrary ab = new SuggestionLibrary();

        //Log.d(TAG, "أ"+ " is : "+ ab.transliterateLetter("أ".charAt(0)));
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, UserActivity.class));
            finish();
        }

        signupInputLayoutEmail = (TextInputLayout) findViewById(R.id.signup_input_layout_email);
        signupInputLayoutPassword = (TextInputLayout) findViewById(R.id.signup_input_layout_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        signupInputEmail = (EditText) findViewById(R.id.signup_input_email);
        signupInputPassword = (EditText) findViewById(R.id.signup_input_password);

    }

    public void signUp(View v) {
        String email = signupInputEmail.getText().toString();
        String password = signupInputPassword.getText().toString();

        CurrentUser = new AuthUser(email, password);
        if (!checkEmail()) {
            return;
        }
        if (!checkPassword()) {
            return;
        }
        signupInputLayoutEmail.setErrorEnabled(false);
        signupInputLayoutPassword.setErrorEnabled(false);
        progressBar.setVisibility(View.VISIBLE);


        auth.createUserWithEmailAndPassword(signupInputEmail.getText().toString(), signupInputPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Authentication failed." + task.getException());
                        } else {
                            startActivity(new Intent(MainActivity.this, UserActivity.class));
                            finish();
                        }
                    }
                });
    }

    private boolean checkEmail() {
        String email = signupInputEmail.getText().toString();
        if (!CurrentUser.isEmailValid()) {

            signupInputLayoutEmail.setErrorEnabled(true);
            signupInputLayoutEmail.setError(getString(R.string.err_msg_email));
            signupInputEmail.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputEmail);
            return false;
        }
        signupInputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {

        String password = signupInputPassword.getText().toString();
        if (!CurrentUser.isPasswordValid()) {

            signupInputLayoutPassword.setError(getString(R.string.err_msg_password));
            signupInputPassword.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputPassword);
            return false;
        }
        signupInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void logIn(View v) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
