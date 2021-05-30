package com.example.erunning;

public class AnalyticsItem {
    String name;
    String profile;
    String rank;
    int at_step;

    public AnalyticsItem(String name, String profile, String rank, int at_step) {
        this.name = name;
        this.profile = profile;
        this.rank = rank;
        this.at_step = at_step;
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile;
    }

    public String getRank() {
        return rank;
    }

    public int getAt_step() {
        return at_step;
    }
}
