package com.khdamte.bitcode.khdamte_app.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.models.Helper;

import java.util.Locale;

/**
 * Created by Amado on 7/27/2017.
 */

public class AboutAppActivity extends Activity implements View.OnClickListener{

    private ImageView back_btn;
    private TextView email_val_tv, phone_val_tv, email_title_tv, phone_title_tv, contact_title, details_tv, title_toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_activity_layout);

        back_btn = (ImageView) findViewById(R.id.back_btn);

        email_val_tv = (TextView) findViewById(R.id.email_val_tv);
        phone_val_tv = (TextView) findViewById(R.id.phone_val_tv);
        email_title_tv = (TextView) findViewById(R.id.email_title_tv);
        phone_title_tv = (TextView) findViewById(R.id.phone_title_tv);
        contact_title = (TextView) findViewById(R.id.contact_title);
        details_tv = (TextView) findViewById(R.id.details_tv);
        title_toolbar = (TextView) findViewById(R.id.title_toolbar);


        email_val_tv.setTypeface(Helper.getTypeFace());
        phone_val_tv.setTypeface(Helper.getTypeFace(), Typeface.BOLD);
        email_title_tv.setTypeface(Helper.getTypeFace());
        phone_title_tv.setTypeface(Helper.getTypeFace());
        contact_title.setTypeface(Helper.getTypeFace(), Typeface.BOLD);
        details_tv.setTypeface(Helper.getTypeFace(), Typeface.BOLD);
        title_toolbar.setTypeface(Helper.getTypeFace(), Typeface.BOLD);

        Helper.setSrc4BackImg(back_btn);

        email_val_tv.setText("khdamte@gmail.com");
        phone_val_tv.setText("+96555588047");

        back_btn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }
}
