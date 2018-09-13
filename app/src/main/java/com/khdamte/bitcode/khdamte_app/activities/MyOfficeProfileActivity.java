package com.khdamte.bitcode.khdamte_app.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.fragments.EditOfficeFragment;
import com.khdamte.bitcode.khdamte_app.models.Helper;

import java.util.Locale;

/**
 * Created by Amado on 4/6/2017.
 */

public class MyOfficeProfileActivity extends AppCompatActivity{


    private FragmentManager fragmentManager;
    private ImageView back_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myoffice_settings_activity);

        back_btn = (ImageView) findViewById(R.id.back_btn) ;
        TextView title_tv = (TextView) findViewById(R.id.title_toolbar) ;

        title_tv.setTypeface(Helper.getTypeFace());

        Helper.setSrc4BackImg(back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.myoffice_frame, new EditOfficeFragment()).addToBackStack(null).commit();
    }


    @Override
    public void onBackPressed() {
        finish();
    }


}
