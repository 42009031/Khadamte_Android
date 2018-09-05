package com.khdamte.bitcode.khdamte_app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.IndividualMaidsAdapter;
import com.khdamte.bitcode.khdamte_app.adapter.PhoneAdapter;
import com.khdamte.bitcode.khdamte_app.models.IndividualMaidsModel;
import com.khdamte.bitcode.khdamte_app.models.PhoneModel;
import com.khdamte.bitcode.khdamte_app.ui.CircleTransform;
import com.khdamte.bitcode.khdamte_app.ui.RecyclerItemClickListener;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

/**
 * Created by Amado on 10-Jul-18.
 */

public class IndividualMaidsDetailsActivity extends AppCompatActivity{

    private ImageView back_btn, maids_img;
    private TextView maids_name_tv, desc_title_tv, desc_tv, price_tv, price_val_tv, age_val_tv, age_tv,nationality_tv,
            nationality_val_tv, religion_tv,religion_val_tv, owner_tv, owner_val_tv ;

    private ListView phones_lv;
    private ArrayList<PhoneModel> phonesArray;
    private PhoneAdapter phoneAdapter;
    private AlertDialog progressDialog;
    private int newPosition;
    private SharedPreferences languagepref;
    private String langToLoad;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maids_individuals_details_layout);
        progressDialog = new SpotsDialog(this, R.style.Custom);

        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        langToLoad = languagepref.getString("languageToLoad", null);

        back_btn = (ImageView) findViewById(R.id.back_btn);
        maids_name_tv = (TextView) findViewById(R.id.maids_name_tv);
        maids_img = (ImageView) findViewById(R.id.maids_img);



        desc_title_tv = (TextView) findViewById(R.id.desc_title_tv);
        desc_tv = (TextView) findViewById(R.id.desc_tv);
        price_tv = (TextView) findViewById(R.id.price_tv);
        price_val_tv = (TextView) findViewById(R.id.price_val_tv);
        age_tv = (TextView) findViewById(R.id.age_tv);
        age_val_tv = (TextView) findViewById(R.id.age_val_tv);
        nationality_tv = (TextView) findViewById(R.id.nationality_tv);
        nationality_val_tv = (TextView) findViewById(R.id.nationality_val_tv);
        religion_tv = (TextView) findViewById(R.id.religion_tv);
        religion_val_tv = (TextView) findViewById(R.id.religion_val_tv);
        owner_tv = (TextView) findViewById(R.id.owner_tv);
        owner_val_tv = (TextView) findViewById(R.id.owner_val_tv);
        phones_lv = (ListView) findViewById(R.id.phones_lv);

        desc_tv.setTypeface(MainActivity.lightFace);
        price_tv.setTypeface(MainActivity.lightFace);
        price_val_tv.setTypeface(MainActivity.lightFace);
        age_tv.setTypeface(MainActivity.lightFace);
        age_val_tv.setTypeface(MainActivity.lightFace);
        desc_title_tv.setTypeface(MainActivity.lightFace);
        nationality_tv.setTypeface(MainActivity.lightFace);
        nationality_val_tv.setTypeface(MainActivity.lightFace);
        religion_tv.setTypeface(MainActivity.lightFace);
        religion_val_tv.setTypeface(MainActivity.lightFace);
        owner_tv.setTypeface(MainActivity.lightFace);
        owner_val_tv.setTypeface(MainActivity.lightFace);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        GetMaidData(getIntent().getExtras().getString("maidId"));

    }

    public void onCall(int position) {
        int permissionCheck = ContextCompat.checkSelfPermission(IndividualMaidsDetailsActivity.this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(IndividualMaidsDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
        } else {
            String phone_number = phonesArray.get(position).getPhone_number();
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + phone_number)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onCall(getNewPosition());
                } else {
                    Toast.makeText(IndividualMaidsDetailsActivity.this, getResources().getString(R.string.toast_call_permission), Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    public int getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(int newPosition) {
        this.newPosition = newPosition;
    }

    @Override
    public void onBackPressed() {finish();
    }

    private void GetMaidData(String maidId) {
        showDialog();

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.GetIndividualsMaid((maidId));
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();

                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        JSONArray offices_JsonArray = mainJsonObj.getJSONArray("Response");
                        if (offices_JsonArray.length() != 0) {
                            for (int i = 0; i < offices_JsonArray.length(); i++) {
                                phonesArray = new ArrayList<>();
                                JSONObject office_Obj = offices_JsonArray.getJSONObject(i);
                                String id = office_Obj.getString("id");
                                String image = office_Obj.getString("image");
                                String name = office_Obj.getString("name");
                                String descrip = office_Obj.getString("descrip");
                                String age = office_Obj.getString("age");
                                String price = office_Obj.getString("price");
                                if(!price.contains("DK") || !price.contains("Dk") || !price.contains("dk")){
                                    price = price+" DK";
                                }
                                String religion = office_Obj.getString("religion");
                                String userId = office_Obj.getString("userId");
                                String user = office_Obj.getString("user");
                                String phone1 = office_Obj.getString("phone1");
                                String phone2 = office_Obj.getString("phone2");
                                String nationality = office_Obj.getString("nationality");

                                maids_name_tv.setText(name);
                                Picasso.with(IndividualMaidsDetailsActivity.this)
                                        .load(image)
                                        .placeholder(R.drawable.empty)
                                        .transform(new CircleTransform())
                                        .into(maids_img);
                                desc_tv.setText(descrip);
                                price_val_tv.setText(price);
                                age_val_tv.setText(age);
                                if(nationality.contains(",")){
                                    String [] country = nationality.split(",");
                                    String ar = country[1];
                                    String eng = country[0];
                                    if (langToLoad.equals("العربية")) {
                                        nationality_val_tv.setText(ar);
                                    }else{
                                        nationality_val_tv.setText(eng);
                                    }
                                }else {
                                    nationality_val_tv.setText(nationality);
                                }
                                religion_val_tv.setText(religion);
                                owner_val_tv.setText(user);

                                phonesArray.add(new PhoneModel(getString(R.string.phone1), phone1));
                                phonesArray.add(new PhoneModel(getString(R.string.phone2), phone2));
                            }
                            phoneAdapter = new PhoneAdapter(IndividualMaidsDetailsActivity.this, phonesArray);
                            phones_lv.setAdapter(phoneAdapter);
                            phones_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    setNewPosition(position);
                                    new AlertDialog.Builder(IndividualMaidsDetailsActivity.this)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle(getString(R.string.call_dialog_title))
                                            .setMessage(getString(R.string.call_dialog_body) + " [" + phonesArray.get(position).getPhone_number() + "] ?")
                                            .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Handler mHandler = new Handler(getMainLooper());
                                                    mHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            onCall(position);
                                                        }
                                                    });
                                                }

                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                }
                            });

                        } else {
                            Toast.makeText(IndividualMaidsDetailsActivity.this, getResources().getString(R.string.toast_no_off_incity), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(IndividualMaidsDetailsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(IndividualMaidsDetailsActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(IndividualMaidsDetailsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void dismissDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showDialog() {
        progressDialog.show();
    }
}
