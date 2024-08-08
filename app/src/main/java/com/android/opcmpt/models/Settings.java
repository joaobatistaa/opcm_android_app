package com.android.opcmpt.models;

public class Settings {

    private String privacy_policy;
    private String about_us;
    private String our_mission;

    public String getPrivacy_policy() {
        return privacy_policy;
    }
    public String getAboutUs() {
        return about_us;
    }
    public String getOurMission() {
        return our_mission;
    }

    public void setPrivacy_policy(String privacy_policy) {
        this.privacy_policy = privacy_policy;
    }
    public void setAbout_us(String about_us) {
        this.about_us = about_us;
    }
    public void setOur_mission(String our_mission) {
        this.our_mission = our_mission;
    }

}
