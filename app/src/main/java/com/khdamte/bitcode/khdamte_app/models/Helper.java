package com.khdamte.bitcode.khdamte_app.models;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import com.khdamte.bitcode.khdamte_app.R;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Helper {

    public static void setSrc4BackImg(ImageView view) {
        SharedPreferences languagepref = view.getContext().getSharedPreferences("language", MODE_PRIVATE);
        String langToLoad = languagepref.getString("languageToLoad", null);
        String language = Locale.getDefault().getDisplayLanguage();
        if (language.equals("العربية")) {
            view.setImageResource(R.drawable.ic_back);
        } else {
            view.setImageResource(R.drawable.ic_forward);
        }
    }
}
