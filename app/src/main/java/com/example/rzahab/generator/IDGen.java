package com.example.rzahab.generator;

import android.util.Log;

import com.google.common.hash.Hashing;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * Created by Rzahab on 6/21/2017.
 */

public class IDGen {
    public User user;

    public IDGen(User user) {

        this.user = user;
    }

    public IDGen() {

    }

    public String generateID() {

        String first_name = (user.getFirst_name_equiv() != null) ?
                user.getFirst_name_equiv() : user.getFirst_name();

        String father_name = (user.getFather_name_equiv() != null) ?
                user.getFather_name_equiv() : user.getFather_name();

        String last_name = (user.getLast_name_equiv() != null) ?
                user.getLast_name_equiv() : user.getLast_name();

        String mother_name = (user.getMother_name_equiv() != null) ?
                user.getMother_name_equiv() : user.getMother_name();

        String full_name = first_name + father_name + last_name;

        String gender_char = user.getGender().substring(0,1);

        String full_name_hash = primeHash(full_name, 4);
        String mother_name_hash = primeHash(mother_name, 3);

        String birthday_hash = primeHash(user.getDob(), 3);

        String full_name_char_count = String.format("%02d", full_name.length());
        String mother_name_char_count = String.format("%02d", mother_name.length());

        String hash = full_name_char_count + mother_name_char_count + full_name_hash + mother_name_hash + birthday_hash + gender_char;

        Log.d("Hashing","ID: "+hash);
        return hash;
    }

    public String primeHash(String name, int padding) {
        BigInteger b_10000 = new BigInteger("" + (int) Math.pow(10, padding));

        String hashed = Hashing.sha1()
                .hashString(name, StandardCharsets.UTF_8)
                .toString();

        BigInteger hashedToInt = new BigInteger(hashed, 16);

        String name_hash = String.format("%0" + padding + "d", hashedToInt.mod(b_10000));

        return name_hash;
    }
}



