package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class BarterApp extends Application {

    private static Context mContext;
    private static SharedPreferences mSharedPreferences;

    public void onCreate() {
        super.onCreate();
        BarterApp.mContext = getApplicationContext();
        BarterApp.mSharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME,MODE_PRIVATE);
    }

    public static Context getAppContext() {
        return BarterApp.mContext;
    }

    public static SharedPreferences getSharedPrefs() {
        return BarterApp.mSharedPreferences;
    }

}
