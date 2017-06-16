package com.example.rzahab.generator;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by Rzahab on 5/31/2017.
 */

public class AuthUser implements Parcelable {
    private String email, password;

    AuthUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static final Creator<AuthUser> CREATOR = new Creator<AuthUser>() {
        @Override
        public AuthUser createFromParcel(Parcel in) {
            return new AuthUser(in);
        }

        @Override
        public AuthUser[] newArray(int size) {
            return new AuthUser[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmailValid() {
        email = this.email.trim();
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPasswordValid() {
        password = this.password.trim();
        return !password.isEmpty() && (password.length() >= 6);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.password);
        //dest.writeString(this.);
    }

    public AuthUser(Parcel in) {
        this.email = in.readString();
        this.password = in.readString();
    }


}
