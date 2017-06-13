package com.example.rzahab.generator;

import java.util.ArrayList;

/**
 * Created by Rzahab on 6/13/2017.
 */

public class Generator extends MyApplication {
    private ArrayList<SuggestedUser> suggestedUsers;

    public double getRateThreshold() {
        return rateThreshold;
    }

    private void setRateThreshold(double rateThreshold) {
        this.rateThreshold = 50;
    }

    private double rateThreshold;

    public ArrayList<SuggestedUser> getSuggestedUsers() {
        return this.suggestedUsers;
    }

    public void setSuggestedUsers(ArrayList<SuggestedUser> users) {
        this.suggestedUsers = users;
    }


}
