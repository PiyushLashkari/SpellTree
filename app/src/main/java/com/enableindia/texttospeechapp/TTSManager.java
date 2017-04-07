package com.enableindia.texttospeechapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;


public class TTSManager {

    private TextToSpeech mTts = null;
    private volatile boolean isLoaded = false;
    private String PREFS_NAME = "TTS_PREFS";

    public void init(Context context) {

        try {
            mTts = new TextToSpeech(context, onInitListener);
            mTts.setSpeechRate((float)0.7);
            mTts.setPitch((float) 0.7);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTts.setLanguage(Locale.US);
                isLoaded = true;

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "TTS Manager: This Language is not supported");
                }
            } else {
                Log.e("error", "TTS Manager: Initialization Failed!");
            }
        }
    };

    public void shutDown() {
        mTts.shutdown();
    }

    public void addQueue(String text) {
        if (isLoaded)
            mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            Log.e("error", "TTS Manager: Add Queue - TTS Not Initialized");
    }

    public void initQueue(String text) {
        if (isLoaded)
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        else
            Log.e("error", "TTS Manager: Init Queue - TTS Not Initialized");

//        int iter = 0;
//
//        try {
//            while (!isLoaded) {
//                ++iter;
//                Thread.sleep(500);
//
//                if (iter == 4) {
//                    // Break after 2 seconds. [in addition, preventing infinite loop]
//                    Log.e("error", "TTS Manager: Init Queue - TTS Not Initialized. [Even after 2 seconds]");
//                    break;
//                }
//            }
//        } catch (InterruptedException ex) {
//            // Do Nothing
//        }
//
//        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    public boolean isTtsSpeaking()
    {
        return mTts.isSpeaking();
    }

    public void setTtsPitch(float pitch)
    {
        mTts.setPitch(pitch);
    }
    public void setTtsRate(float rate)
    {
        mTts.setSpeechRate(rate);
    }


}