package com.khdamte.bitcode.khdamte_app.models;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.KhadamteApplication;
import com.khdamte.bitcode.khdamte_app.adapter.SharedPreferencesManager;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Helper {

    public final static String LOCALE = "locale";
    public final static String AR = "ar";
    public final static String EN = "en";


    public static void setSrc4BackImg(ImageView view) {
        if (SharedPreferencesManager.getStringValue(Helper.LOCALE).equals(Helper.AR)) {
            view.setImageResource(R.drawable.ic_back);
        } else {
            view.setImageResource(R.drawable.ic_forward);
        }
    }

    public static Typeface getTypeFace(){
        Typeface lightFace;
        if (SharedPreferencesManager.getStringValue(Helper.LOCALE).equals(Helper.AR)) {
            lightFace = Typeface.createFromAsset(KhadamteApplication.context.getAssets(), "fonts/arabic_font.ttf");
        } else {
            lightFace = Typeface.createFromAsset(KhadamteApplication.context.getAssets(), "fonts/comic.ttf");
        }
        return lightFace;
    }
}
