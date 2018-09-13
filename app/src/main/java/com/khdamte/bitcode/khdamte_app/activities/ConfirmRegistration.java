package com.khdamte.bitcode.khdamte_app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.models.Helper;

/**
 * Created by Ahmed Dawoud on 1/31/2017.
 */

public class ConfirmRegistration extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 5000;
    private ImageView right_img;
    private TextView confirm_msg_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_registration_layout);

        right_img = (ImageView) findViewById(R.id.right_img);
        confirm_msg_tv = (TextView) findViewById(R.id.confirm_msg_tv);
        confirm_msg_tv.setTypeface(Helper.getTypeFace());

        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);

        right_img.startAnimation(animation);
        confirm_msg_tv.startAnimation(animation1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedpreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("id", getIntent().getExtras().getString("useId"));
                editor.putString("officeId", getIntent().getExtras().getString("officeId"));
                editor.putString("userRole", "owner");
                editor.apply();

//                Toast.makeText(ConfirmRegistration.this, "Welcome " + getIntent().getExtras().getString("fName") + " " + getIntent().getExtras().getString("lName"), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ConfirmRegistration.this, MainActivity.class));

                Intent mainIntent = new Intent(ConfirmRegistration.this, MainActivity.class);
                startActivity(mainIntent);

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
