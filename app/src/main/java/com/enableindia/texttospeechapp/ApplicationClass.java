package com.enableindia.texttospeechapp;

import android.app.Application;

/**
 * Created by archds on 10/25/2015.
 */
public class ApplicationClass extends Application {

    private String pitch;
    private String speechRate;

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getSpeechRate() {
        return speechRate;
    }

    public void setSpeechRate(String speechRate) {
        this.speechRate = speechRate;
    }
}