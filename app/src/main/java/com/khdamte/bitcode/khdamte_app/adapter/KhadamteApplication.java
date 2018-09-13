package com.khdamte.bitcode.khdamte_app.adapter;

import android.app.Application;


/**
 * Created by Ahmed on 21/01/2018.
 */

public class KhadamteApplication extends Application {

    public static KhadamteApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        KhadamteApplication.context = (KhadamteApplication) getApplicationContext();
    }
}
