package com.khdamte.bitcode.khdamte_app.models;

import android.view.View;
import android.widget.ImageView;

import com.khdamte.bitcode.khdamte_app.R;

public class Helper {

    public static void setSrc4BackImg(ImageView view, String Lang) {
        if (Lang.equals("ar")) {
            view.setImageResource(R.drawable.ic_back);
        } else {
            view.setImageResource(R.drawable.ic_forward);
        }
    }
}
