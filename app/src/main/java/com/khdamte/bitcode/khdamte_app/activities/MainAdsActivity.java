package com.khdamte.bitcode.khdamte_app.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.AdsAdapter;
import com.khdamte.bitcode.khdamte_app.models.AdsModel;
import com.khdamte.bitcode.khdamte_app.models.Helper;

import java.util.ArrayList;


public class MainAdsActivity extends AppCompatActivity {

    private Button next;
    private ListView ads_lv;
    private AdsAdapter adsAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ads);
        ads_lv = (ListView) findViewById(R.id.ads_lv);
        next = (Button) findViewById(R.id.next_btn);

        next.setTypeface(Helper.getTypeFace(), Typeface.BOLD);

        ArrayList<AdsModel> adsModels = (ArrayList<AdsModel>) getIntent().getExtras().getSerializable("ads_list");
        adsAdapter = new AdsAdapter(this, adsModels);
        ads_lv.setAdapter(adsAdapter);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainAdsActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
