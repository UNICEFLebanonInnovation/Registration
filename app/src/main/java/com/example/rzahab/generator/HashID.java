package com.example.rzahab.generator;

/**
 * Created by Rzahab on 6/20/2017.
 */

public class HashID {
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    private String hash;

    HashID(String hash) {
        this.hash = hash;
    }


}
