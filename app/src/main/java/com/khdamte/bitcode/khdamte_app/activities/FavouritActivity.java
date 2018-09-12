package com.khdamte.bitcode.khdamte_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.FavouritAdapter;
import com.khdamte.bitcode.khdamte_app.models.DBHelper;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.models.Office_Model;

import java.util.ArrayList;
import java.util.Locale;

public class FavouritActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView favourit_lv ;
    private FavouritAdapter adapter ;
    private ArrayList<Office_Model> office_models;
    private DBHelper dbHelper ;
    private ImageView back_btn;
    private TextView title_tv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourit);

        back_btn = (ImageView) findViewById(R.id.back_btn) ;
        title_tv = (TextView) findViewById(R.id.title_toolbar) ;

        dbHelper = new DBHelper(this);
        office_models = new ArrayList<>();
        office_models.addAll(dbHelper.getAllOffices());

        if(office_models.size() == 0){
            Toast.makeText(this, getResources().getString(R.string.toast_on_fav_off), Toast.LENGTH_LONG).show();
        }

        favourit_lv = (ListView) findViewById(R.id.favourit_lv);
        adapter = new FavouritAdapter(this, office_models);
        favourit_lv.setAdapter(adapter);
        favourit_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String office_id = office_models.get(position).getOffice_id();
                startActivity(new Intent(FavouritActivity.this, DetailsActivity.class).putExtra("office_id", office_id));
            }
        });

        back_btn.setOnClickListener(this);
        title_tv.setTypeface(MainActivity.lightFace);

        Helper.setSrc4BackImg(back_btn, Locale.getDefault().getDisplayLanguage());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back_btn :
                onBackPressed();
                break;
        }
    }
}
