package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesManager {

    private static final String APP_SETTINGS = "APP_SETTINGS";

    private SharedPreferencesManager() {}

    private static SharedPreferences getSharedPreferences() {
        return KhadamteApplication.context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getStringValue(String KEY) {
        return getSharedPreferences().getString(KEY , "");
    }

    public static void setStringValue(String KEY, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(KEY , newValue);
        editor.apply();
    }

    public static void clearSharedPreference(){
        getSharedPreferences().edit().clear().apply();
    }
}