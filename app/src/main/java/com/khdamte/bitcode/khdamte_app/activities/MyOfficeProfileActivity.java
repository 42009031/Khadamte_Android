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
import com.khdamte.bitcode.khdamte_app.fragments.AddOfficeFragment;
import com.khdamte.bitcode.khdamte_app.fragments.EditOfficeFragment;

/**
 * Created by Amado on 4/6/2017.
 */

public class MyOfficeProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FrameLayout update_frame, add_frame ;
    private Button update_btn, add_btn ;
    private ImageView update_imgView, add_imgView ;
    private FragmentManager fragmentManager;
    private Typeface lightFace ;
    private ImageView back_btn;
    public static TextView title_tv ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myoffice_settings_activity);

        lightFace = MainActivity.lightFace;

        back_btn = (ImageView) findViewById(R.id.back_btn) ;
        title_tv = (TextView) findViewById(R.id.title_toolbar) ;

        back_btn.setOnClickListener(this);
        title_tv.setTypeface(MainActivity.lightFace);
        setTitle(getResources().getString(R.string.add_office));

        update_frame = (FrameLayout) findViewById(R.id.update_frame);
        update_btn = (Button) findViewById(R.id.update_btn);
        update_imgView = (ImageView) findViewById(R.id.update_img);

        add_frame = (FrameLayout) findViewById(R.id.add_frame);
        add_btn = (Button) findViewById(R.id.add_btn);
        add_imgView = (ImageView) findViewById(R.id.add_img);

        update_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);

        update_btn.setTypeface(lightFace);
        add_btn.setTypeface(lightFace);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.myoffice_frame, new AddOfficeFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.update_btn:

                update_frame.setBackgroundColor(Color.parseColor("#ffffff"));
                update_btn.setTextColor(Color.parseColor("#000000"));
                update_imgView.setImageResource(R.drawable.edit_black);

                add_frame.setBackgroundColor(Color.parseColor("#4d4d4d"));
                add_btn.setTextColor(Color.parseColor("#ffffff"));
                add_imgView.setImageResource(R.drawable.add_white);

                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.myoffice_frame, new EditOfficeFragment()).addToBackStack(null).commit();
                break;
            case R.id.add_btn:

                update_frame.setBackgroundColor(Color.parseColor("#4d4d4d"));
                update_btn.setTextColor(Color.parseColor("#ffffff"));
                update_imgView.setImageResource(R.drawable.edit_white);

                add_frame.setBackgroundColor(Color.parseColor("#ffffff"));
                add_btn.setTextColor(Color.parseColor("#000000"));
                add_imgView.setImageResource(R.drawable.add_black);

                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.myoffice_frame, new AddOfficeFragment()).addToBackStack(null).commit();
            break;

            case R.id.back_btn :
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static void setTitle(String title){
        title_tv.setText(title);
    }
}
