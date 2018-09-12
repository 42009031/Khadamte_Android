package com.khdamte.bitcode.khdamte_app.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;

import com.khdamte.bitcode.khdamte_app.adapter.FetchPath;
import com.khdamte.bitcode.khdamte_app.adapter.SpinnerAdapter;

import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.models.MaidsDataModel;
import com.khdamte.bitcode.khdamte_app.ui.CircleTransform;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

/**
 * Created by Amado on 21-Apr-18.
 */

public class RegisterMaidsDataActivity extends AppCompatActivity {

    private ImageView back_btn, captureImg;

    private TextView title_toolbar, captureImgTxt, maidsNameTv, maidsNationalityTv, maidsAgeTv, maidsReligionTv, maidsPriceTv, maidsDescTv, contactWayTv, maidsCountryTv, maidsCityTv;

    private EditText maidsName_et, description_et, maidsAge_et, maidsReligion_et, price_et;

    private Spinner cityId_spinner, nationality_spinner, country_spinner, contactWaySpinner, maidsSpinner;

    private Button addBtn, deleteBtn, updateBtn;

    private View deleteView, updateView;

    private LinearLayout maidsSpinnerLayout;

    private HashMap<String, String> country_hashMap = new HashMap<>();
    private HashMap<String, String> cities_hashMap = new HashMap<>();
    private HashMap<String, String> nationalities_hashMap = new HashMap<>();
    private ArrayList<MaidsDataModel> maidsArray = new ArrayList<>();

    private AlertDialog progressDialog;
    private String langToLoad, postContactWayId = "";
    private SharedPreferences languagepref;
    private String captureImgUri;
    private String user_id;
    private MaidsDataModel maidDataModel = new MaidsDataModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_maids_data_layout);

        init();
        btnsListner();

        GetAllMaids(user_id);
        GetAllCountries();
        GetAllNationalities();
        GetAllContactWays();
    }

    private void btnsListner() {

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = maidsName_et.getText().toString();
                String desc = description_et.getText().toString();
                String age = maidsAge_et.getText().toString();
                String price = price_et.getText().toString();
                String religion = maidsReligion_et.getText().toString();
                String currentMonth = "1";
                String stateId = cities_hashMap.get(cityId_spinner.getSelectedItem().toString());
                String nationId = nationalities_hashMap.get(nationality_spinner.getSelectedItem().toString());

                if (!TextUtils.isEmpty(user_id) &&
                        !TextUtils.isEmpty(name) &&
                        !TextUtils.isEmpty(desc) &&
                        !TextUtils.isEmpty(age) &&
                        !TextUtils.isEmpty(price) &&
                        !TextUtils.isEmpty(religion) &&
                        !TextUtils.isEmpty(currentMonth) &&
                        !TextUtils.isEmpty(stateId) &&
                        !TextUtils.isEmpty(postContactWayId) &&
                        !TextUtils.isEmpty(captureImgUri) &&
                        !TextUtils.isEmpty(nationId)) {

                    postMaid(user_id, name, desc, age, nationId, stateId, religion, captureImgUri, price, currentMonth);
                } else {
                    Toast.makeText(RegisterMaidsDataActivity.this, getString(R.string.toast_type_alldata), Toast.LENGTH_SHORT).show();
                }

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = maidsName_et.getText().toString();
                String desc = description_et.getText().toString();
                String age = maidsAge_et.getText().toString();
                String price = price_et.getText().toString();
                String religion = maidsReligion_et.getText().toString();
                String currentMonth = "1";
                String stateId = cities_hashMap.get(cityId_spinner.getSelectedItem().toString());
                String nationId = nationalities_hashMap.get(nationality_spinner.getSelectedItem().toString());

                if (!TextUtils.isEmpty(user_id) &&
                        !TextUtils.isEmpty(name) &&
                        !TextUtils.isEmpty(desc) &&
                        !TextUtils.isEmpty(age) &&
                        !TextUtils.isEmpty(price) &&
                        !TextUtils.isEmpty(religion) &&
                        !TextUtils.isEmpty(currentMonth) &&
                        !TextUtils.isEmpty(stateId) &&
                        !TextUtils.isEmpty(postContactWayId) &&
                        !TextUtils.isEmpty(nationId)) {

                    if (!TextUtils.isEmpty(maidDataModel.getMaidId())) {
                        if(TextUtils.isEmpty(captureImgUri)){
                            editMaidWithoutImage(maidDataModel.getMaidId(), user_id, name, desc, age, nationId, stateId, religion, price, currentMonth);
                        }else{
                            editMaid(maidDataModel.getMaidId(), user_id, name, desc, age, nationId, stateId, religion, captureImgUri, price, currentMonth);
                        }

                    } else {
                        Toast.makeText(RegisterMaidsDataActivity.this, "please select maid", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RegisterMaidsDataActivity.this, getString(R.string.toast_type_alldata), Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(maidDataModel.getMaidId())) {
                    deleteMaid(maidDataModel.getMaidId());
                } else {
                    Toast.makeText(RegisterMaidsDataActivity.this, "please select maid", Toast.LENGTH_SHORT).show();
                }


            }
        });

        captureImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPermission()) {
                    Intent localIntent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(localIntent, 5);
                } else {
                    if (checkPermission()) {
                        requestPermissionAndContinue();
                    } else {
                        Intent localIntent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(localIntent, 5);
                    }
                }

            }
        });

    }


    private void init() {
        back_btn = (ImageView) findViewById(R.id.back_btn);
        progressDialog = new SpotsDialog(this, R.style.Custom);
        title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        maidsSpinnerLayout = (LinearLayout) findViewById(R.id.maidsSpinnerLayout);
        maidsSpinner = (Spinner) findViewById(R.id.maidsSpinner);
        captureImg = (ImageView) findViewById(R.id.captureImg);
        captureImgTxt = (TextView) findViewById(R.id.captureImgTxt);
        maidsNameTv = (TextView) findViewById(R.id.maidsNameTv);
        maidsName_et = (EditText) findViewById(R.id.maidsName_et);
        maidsDescTv = (TextView) findViewById(R.id.maidsDescTv);
        description_et = (EditText) findViewById(R.id.description_et);
        maidsAgeTv = (TextView) findViewById(R.id.maidsAgeTv);
        maidsAge_et = (EditText) findViewById(R.id.maidsAge_et);
        maidsReligionTv = (TextView) findViewById(R.id.maidsReligionTv);
        maidsReligion_et = (EditText) findViewById(R.id.maidsReligion_et);
        maidsPriceTv = (TextView) findViewById(R.id.maidsPriceTv);
        price_et = (EditText) findViewById(R.id.price_et);
        maidsCountryTv = (TextView) findViewById(R.id.maidsCountryTv);
        country_spinner = (Spinner) findViewById(R.id.country_spinner);

        maidsCityTv = (TextView) findViewById(R.id.maidsCityTv);
        cityId_spinner = (Spinner) findViewById(R.id.cityId_spinner);
        maidsNationalityTv = (TextView) findViewById(R.id.maidsNationalityTv);
        nationality_spinner = (Spinner) findViewById(R.id.nationality_spinner);
        contactWayTv = (TextView) findViewById(R.id.contactWayTv);
        contactWaySpinner = (Spinner) findViewById(R.id.contactWaySpinner);
        addBtn = (Button) findViewById(R.id.addBtn);
        updateBtn = (Button) findViewById(R.id.updateBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        deleteView = findViewById(R.id.deleteView);
        updateView = findViewById(R.id.updateView);

        title_toolbar.setTypeface(MainActivity.lightFace, Typeface.BOLD);
        captureImgTxt.setTypeface(MainActivity.lightFace);
        maidsNameTv.setTypeface(MainActivity.lightFace);
        maidsName_et.setTypeface(MainActivity.lightFace);
        maidsDescTv.setTypeface(MainActivity.lightFace);
        description_et.setTypeface(MainActivity.lightFace);
        maidsAgeTv.setTypeface(MainActivity.lightFace);
        maidsAge_et.setTypeface(MainActivity.lightFace);
        maidsReligionTv.setTypeface(MainActivity.lightFace);
        maidsReligion_et.setTypeface(MainActivity.lightFace);
        maidsPriceTv.setTypeface(MainActivity.lightFace);
        price_et.setTypeface(MainActivity.lightFace);
        maidsNationalityTv.setTypeface(MainActivity.lightFace);
        contactWayTv.setTypeface(MainActivity.lightFace);
        maidsCountryTv.setTypeface(MainActivity.lightFace);
        maidsCityTv.setTypeface(MainActivity.lightFace);
        addBtn.setTypeface(MainActivity.lightFace);
        updateBtn.setTypeface(MainActivity.lightFace);
        deleteBtn.setTypeface(MainActivity.lightFace);

        Helper.setSrc4BackImg(back_btn);

        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        langToLoad = languagepref.getString("languageToLoad", null);

        final SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        user_id = prefs.getString("id", null);

        ArrayList<String> countries_arrayList = new ArrayList<String>();
        countries_arrayList.add(getResources().getString(R.string.choose_country));
        final ArrayAdapter<String> country_spinnerArrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, countries_arrayList);
        country_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        country_spinner.setAdapter(country_spinnerArrayAdapter);

        ArrayList<String> cities_arrayList = new ArrayList<String>();
        cities_arrayList.add(getResources().getString(R.string.choose_city));
        final ArrayAdapter<String> city_spinnerArrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, cities_arrayList);
        city_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        cityId_spinner.setAdapter(city_spinnerArrayAdapter);


        ArrayList<String> nation_arrayList = new ArrayList<String>();
        nation_arrayList.add(getResources().getString(R.string.nationality_maids));
        final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, nation_arrayList);
        nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        nationality_spinner.setAdapter(nation_ArrayAdapter);

        ArrayList<String> contactWays_arrayList = new ArrayList<String>();
        contactWays_arrayList.add(getResources().getString(R.string.addContactWays));
        final ArrayAdapter<String> contactWays_arrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, contactWays_arrayList);
        contactWays_arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        contactWaySpinner.setAdapter(contactWays_arrayAdapter);
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Runtime Permission");
                alertBuilder.setMessage("Allow KHADAMTE to access photos and media on your device ?");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(RegisterMaidsDataActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(RegisterMaidsDataActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
        } else {
            Intent localIntent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(localIntent, 5);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 123) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    Intent localIntent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(localIntent, 5);
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        if ((paramInt1 == 5) && (paramInt2 == -1) && (paramIntent != null)) {
            Uri photoUri = paramIntent.getData();
            if (photoUri != null) {
                captureImgUri = FetchPath.getPath(this, photoUri);
            }
            Picasso.with(this).load(String.valueOf(paramIntent.getData())).transform(new CircleTransform()).into(this.captureImg);
        }
    }

    private void dismissDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showDialog() {
        progressDialog.show();
    }


    // APIs

    private void GetAllMaids(String userId) {
        showDialog();
        nationalities_hashMap = new HashMap<>();
        final ArrayList<String> maidsNameArray = new ArrayList<>();
        maidsNameArray.add(getString(R.string.chooseMaid));

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getUserMaid(userId);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        JSONArray adsJsonArray = mainJsonObj.getJSONArray("Response");
                        if (adsJsonArray.length() != 0) {
                            maidsSpinnerLayout.setVisibility(View.VISIBLE);
                            for (int i = 0; i < adsJsonArray.length(); i++) {
                                JSONObject adsObj = adsJsonArray.getJSONObject(i);

                                String id = adsObj.getString("id");
                                String name = adsObj.getString("name");
                                String religion = adsObj.getString("religion");
                                String age = adsObj.getString("age");
                                String price = adsObj.getString("price");
                                String image = adsObj.getString("image");
                                String descrip = adsObj.getString("descrip");
                                String userId = adsObj.getString("userId");
                                String user = adsObj.getString("user");
                                String phone1 = adsObj.getString("phone1");
                                String phone2 = adsObj.getString("phone2");
                                String nationality = adsObj.getString("nationality");

                                maidsArray.add(new MaidsDataModel(id,
                                        userId,
                                        name,
                                        religion,
                                        age,
                                        price,
                                        image,
                                        descrip,
                                        user,
                                        phone1,
                                        phone2,
                                        nationality));

                                maidsNameArray.add(name);
                            }


                            final ArrayAdapter<String> maidsArrayAdapter = new SpinnerAdapter(RegisterMaidsDataActivity.this, R.layout.spinner_item, maidsNameArray);
                            maidsArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            maidsSpinner.setAdapter(maidsArrayAdapter);
                            maidsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                    if (position > 0) {
                                        maidDataModel = maidsArray.get(position - 1);

                                        Picasso.with(RegisterMaidsDataActivity.this)
                                                .load(maidDataModel.getImage())
                                                .transform(new CircleTransform())
                                                .into(captureImg);

                                        maidsName_et.setText(maidDataModel.getName());
                                        int nationIndex = 0;
                                        for (String natioName : nationalities_hashMap.keySet()) {
                                            if (natioName.equals(maidDataModel.getNationality())) {
                                                nationality_spinner.setSelection(nationIndex, true);
                                            }
                                            nationIndex++;
                                        }
                                        maidsAge_et.setText(maidDataModel.getAge());
                                        maidsReligion_et.setText(maidDataModel.getReligion());
                                        price_et.setText(maidDataModel.getPrice());
                                        description_et.setText(maidDataModel.getDescrip());

//                                        int countryIndex = 0;
//                                        for(String natioName : country_hashMap.keySet()){
//                                            if(natioName.equals(model.getNationality())){
//                                                country_spinner.setSelection(countryIndex, true);
//                                            }
//                                            countryIndex++;
//                                        }

//                                        int cityIndex = 0;
//                                        for(String cityName : cities_hashMap.keySet()){
//                                            if(cityName.equals(model.getNationality())){
//                                                cityId_spinner.setSelection(cityIndex, true);
//                                            }
//                                            cityIndex++;
//                                        }
                                        deleteView.setVisibility(View.VISIBLE);
                                        updateBtn.setVisibility(View.VISIBLE);
                                        deleteBtn.setVisibility(View.VISIBLE);
                                        addBtn.setVisibility(View.GONE);
                                    } else {
                                        deleteView.setVisibility(View.GONE);
                                        updateBtn.setVisibility(View.GONE);
                                        deleteBtn.setVisibility(View.GONE);
                                        addBtn.setVisibility(View.VISIBLE);

                                        captureImg.setImageResource(R.drawable.licenses);
                                        maidsName_et.setText("");
                                        nationality_spinner.setSelection(0, true);
                                        maidsAge_et.setText("");
                                        maidsReligion_et.setText("");
                                        price_et.setText("");
                                        description_et.setText("");
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });

                        } else {
                            Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_no_nationality), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterMaidsDataActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void GetAllCountries() {
        showDialog();
        country_hashMap = new HashMap<String, String>();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getCountries();
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        JSONArray adsJsonArray = mainJsonObj.getJSONArray("Response");
                        if (adsJsonArray.length() != 0) {
                            for (int i = 0; i < adsJsonArray.length(); i++) {
                                JSONObject adsObj = adsJsonArray.getJSONObject(i);
                                String country_id = adsObj.getString("id");
                                String country_name = adsObj.getString("name");
                                if (country_name.contains(",")) {
                                    String[] country = country_name.split(",");
                                    String ar = country[1];
                                    String eng = country[0];
                                    if (langToLoad.equals("العربية")) {
                                        country_hashMap.put(ar.trim(), country_id);
                                    } else {
                                        country_hashMap.put(eng.trim(), country_id);
                                    }
                                } else {
                                    country_hashMap.put(country_name.trim(), country_id);
                                }


                            }
                            ArrayList<String> countries_arrayList = new ArrayList<String>();
                            countries_arrayList.add(getResources().getString(R.string.choose_country));
                            countries_arrayList.addAll(country_hashMap.keySet());
                            final ArrayAdapter<String> country_spinnerArrayAdapter = new SpinnerAdapter(RegisterMaidsDataActivity.this, R.layout.spinner_item, countries_arrayList);
                            country_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            country_spinner.setAdapter(country_spinnerArrayAdapter);
                            country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedItemText = (String) parent.getItemAtPosition(position);
                                    if (position > 0) {
                                        GetAllCities(country_hashMap.get(selectedItemText));
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_no_countries), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterMaidsDataActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void GetAllCities(String country_id) {
        showDialog();
        cities_hashMap = new HashMap<>();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getCities(country_id);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        JSONArray adsJsonArray = mainJsonObj.getJSONArray("Response");
                        for (int i = 0; i < adsJsonArray.length(); i++) {
                            JSONObject adsObj = adsJsonArray.getJSONObject(i);
                            String city_id = adsObj.getString("id");
                            String city_name = adsObj.getString("name");
                            if (city_name.contains(",")) {
                                String[] city = city_name.split(",");
                                String ar = city[1];
                                String eng = city[0];
                                if (langToLoad.equals("العربية")) {
                                    cities_hashMap.put(ar.trim(), city_id);
                                } else {
                                    cities_hashMap.put(eng.trim(), city_id);
                                }
                            } else {
                                cities_hashMap.put(city_name.trim(), city_id);
                            }


                        }
                        ArrayList<String> cities_arrayList = new ArrayList<String>();
                        cities_arrayList.add(getResources().getString(R.string.choose_city));
                        cities_arrayList.addAll(cities_hashMap.keySet());
                        final ArrayAdapter<String> city_spinnerArrayAdapter = new SpinnerAdapter(RegisterMaidsDataActivity.this, R.layout.spinner_item, cities_arrayList);
                        city_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                        cityId_spinner.setAdapter(city_spinnerArrayAdapter);


                    } catch (JSONException e) {
                        FirebaseCrash.report(e);
                        e.printStackTrace();
                        Toast.makeText(RegisterMaidsDataActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void GetAllNationalities() {
        showDialog();
        nationalities_hashMap = new HashMap<>();

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getNationalities();
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        JSONArray adsJsonArray = mainJsonObj.getJSONArray("Response");
                        if (adsJsonArray.length() != 0) {
                            for (int i = 0; i < adsJsonArray.length(); i++) {
                                JSONObject adsObj = adsJsonArray.getJSONObject(i);
                                String nat_id = adsObj.getString("id");
                                String nat_name = adsObj.getString("name");
                                if (nat_name.contains(",")) {
                                    String[] nationality = nat_name.split(",");
                                    String ar = nationality[1];
                                    String eng = nationality[0];
                                    if (langToLoad.equals("العربية")) {
                                        nationalities_hashMap.put(ar.trim(), nat_id);
                                    } else {
                                        nationalities_hashMap.put(eng.trim(), nat_id);
                                    }
                                } else {
                                    nationalities_hashMap.put(nat_name, nat_id);
                                }
                            }
                            ArrayList<String> nationlities_arrayList = new ArrayList<String>();
                            nationlities_arrayList.add(getResources().getString(R.string.nationality_maids));
                            nationlities_arrayList.addAll(nationalities_hashMap.keySet());

                            final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(RegisterMaidsDataActivity.this, R.layout.spinner_item, nationlities_arrayList);
                            nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            nationality_spinner.setAdapter(nation_ArrayAdapter);

                        } else {
                            Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_no_nationality), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterMaidsDataActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void GetAllContactWays() {
        showDialog();
        final ArrayList<String> contactWaysArray = new ArrayList<>();
        contactWaysArray.add(getString(R.string.addContactWays));
        final Map<String, String> contacyWaysMap = new HashMap<>();

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getContactsWay();
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONArray serJsonArray = obj.getJSONArray("data");
                        if (serJsonArray.length() != 0) {
                            for (int i = 0; i < serJsonArray.length(); i++) {
                                JSONObject serObj = serJsonArray.getJSONObject(i);
                                String ser_id = serObj.getString("id");
                                String ser_name = serObj.getString("Name");
                                if (ser_name.contains(",")) {
                                    String[] nameArray = ser_name.split(",");
                                    String ar = nameArray[1];
                                    String eng = nameArray[0];
                                    if (langToLoad.equals("العربية")) {
                                        ser_name = ar.trim();
                                    } else {
                                        ser_name = eng.trim();
                                    }
                                }
                                contacyWaysMap.put(ser_name, ser_id);
                                contactWaysArray.add(ser_name);
                            }

                            final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(RegisterMaidsDataActivity.this, R.layout.spinner_item, contactWaysArray);
                            nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            contactWaySpinner.setAdapter(nation_ArrayAdapter);

                            contactWaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (position > 0) {
                                        postContactWayId = contacyWaysMap.get(contactWaysArray.get(position));
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterMaidsDataActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> RegisterMaidsDataActivity, Throwable t) {
                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void postMaid(String user_id, String name, String descrip, String age, String nationalityId, String stateId,
                          String religion, String imageUri, String price, String currentMonth) {

        showDialog();

        File file = new File(imageUri);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);

        RequestBody name_RB = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        RequestBody description_RB = RequestBody.create(MediaType.parse("multipart/form-data"), descrip);
        RequestBody age_RB = RequestBody.create(MediaType.parse("multipart/form-data"), age);
        RequestBody nationalityId_RB = RequestBody.create(MediaType.parse("multipart/form-data"), nationalityId);
        RequestBody stateId_RB = RequestBody.create(MediaType.parse("multipart/form-data"), stateId);
        RequestBody religion_RB = RequestBody.create(MediaType.parse("multipart/form-data"), religion);
        RequestBody price_RB = RequestBody.create(MediaType.parse("multipart/form-data"), price);
        RequestBody contactWay_RB = RequestBody.create(MediaType.parse("multipart/form-data"), postContactWayId);
        RequestBody currentMonth_RB = RequestBody.create(MediaType.parse("multipart/form-data"), currentMonth);


        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<ResponseBody> connection = KHADAMTY_API.postIndividualMaid(user_id,
                name_RB,
                description_RB,
                age_RB,
                nationalityId_RB,
                stateId_RB,
                religion_RB,
                body,
                price_RB,
                contactWay_RB,
                currentMonth_RB);

        connection.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissDialog();
                String result = "";

                if (response.body() != null) {
                    result = response.body().toString();
                    if (!result.equals("")) {
                        try {
                            JSONObject adsObj = new JSONObject(response.body().string());
                            String Status = adsObj.getString("Status");
                            if (Status.equals("true")) {
                                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.registerMaidSuccess), Toast.LENGTH_LONG).show();
                                onBackPressed();
                            } else
                                Toast.makeText(RegisterMaidsDataActivity.this, Status, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterMaidsDataActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                    }
                } else if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String errorMsg = jObjError.getString("Message");
                        if (errorMsg.equals("You can't save advertise this month")) {
                            Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.registerMaidPermission), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterMaidsDataActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void editMaid(String maidId, String user_id, String name, String descrip, String age, String nationalityId, String stateId,
                          String religion, String imageUri, String price, String currentMonth) {

        showDialog();

        File file = new File(imageUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);

        RequestBody name_RB = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        RequestBody description_RB = RequestBody.create(MediaType.parse("multipart/form-data"), descrip);
        RequestBody age_RB = RequestBody.create(MediaType.parse("multipart/form-data"), age);
        RequestBody nationalityId_RB = RequestBody.create(MediaType.parse("multipart/form-data"), nationalityId);
        RequestBody stateId_RB = RequestBody.create(MediaType.parse("multipart/form-data"), stateId);
        RequestBody religion_RB = RequestBody.create(MediaType.parse("multipart/form-data"), religion);
        RequestBody price_RB = RequestBody.create(MediaType.parse("multipart/form-data"), price);
        RequestBody contactWay_RB = RequestBody.create(MediaType.parse("multipart/form-data"), postContactWayId);
        RequestBody currentMonth_RB = RequestBody.create(MediaType.parse("multipart/form-data"), currentMonth);

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<ResponseBody> connection = KHADAMTY_API.editMaid(maidId,
                name_RB,
                description_RB,
                age_RB,
                nationalityId_RB,
                stateId_RB,
                religion_RB,
                body,
                price_RB,
                contactWay_RB,
                currentMonth_RB);

        connection.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissDialog();
                String result = "";

                if (response.body() != null) {
                    result = response.body().toString();
                    if (!result.equals("")) {
                        try {
                            JSONObject adsObj = new JSONObject(response.body().string());
                            String Status = adsObj.getString("Response");
                            if (Status.equals("Edited Success")) {
                                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.updatedSuccess), Toast.LENGTH_LONG).show();
                                onBackPressed();
                            } else
                                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.updatedMaidFail), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterMaidsDataActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                    }
                } else if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String errorMsg = jObjError.getString("Message");
                        if (errorMsg.equals("You can't save advertise this month")) {
                            Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.registerMaidPermission), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterMaidsDataActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void editMaidWithoutImage(String maidId, String user_id, String name, String descrip, String age, String nationalityId, String stateId,
                          String religion, String price, String currentMonth) {

        showDialog();

        RequestBody name_RB = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        RequestBody description_RB = RequestBody.create(MediaType.parse("multipart/form-data"), descrip);
        RequestBody age_RB = RequestBody.create(MediaType.parse("multipart/form-data"), age);
        RequestBody nationalityId_RB = RequestBody.create(MediaType.parse("multipart/form-data"), nationalityId);
        RequestBody stateId_RB = RequestBody.create(MediaType.parse("multipart/form-data"), stateId);
        RequestBody religion_RB = RequestBody.create(MediaType.parse("multipart/form-data"), religion);
        RequestBody price_RB = RequestBody.create(MediaType.parse("multipart/form-data"), price);
        RequestBody contactWay_RB = RequestBody.create(MediaType.parse("multipart/form-data"), postContactWayId);
        RequestBody currentMonth_RB = RequestBody.create(MediaType.parse("multipart/form-data"), currentMonth);

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<ResponseBody> connection = KHADAMTY_API.editMaidWithoutImage(maidId,
                name_RB,
                description_RB,
                age_RB,
                nationalityId_RB,
                stateId_RB,
                religion_RB,
                price_RB,
                contactWay_RB,
                currentMonth_RB);

        connection.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissDialog();
                String result = "";

                if (response.body() != null) {
                    result = response.body().toString();
                    if (!result.equals("")) {
                        try {
                            JSONObject adsObj = new JSONObject(response.body().string());
                            String Status = adsObj.getString("Response");
                            if (Status.equals("Edited Success")) {
                                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.updatedSuccess), Toast.LENGTH_LONG).show();
                                onBackPressed();
                            } else
                                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.updatedMaidFail), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterMaidsDataActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                    }
                } else if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String errorMsg = jObjError.getString("Message");
                        if (errorMsg.equals("You can't save advertise this month")) {
                            Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.registerMaidPermission), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterMaidsDataActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void deleteMaid(String maid_id) {

        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<ResponseBody> connection = KHADAMTY_API.deleteMade(maid_id);

        connection.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissDialog();
                String result = "";

                if (response.body() != null) {
                    result = response.body().toString();
                    if (!result.equals("")) {
                        try {
                            JSONObject adsObj = new JSONObject(response.body().string());
                            String Status = adsObj.getString("Response");
                            if (Status.equals("Deleted")) {
                                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.deleteSuccess), Toast.LENGTH_LONG).show();
                                onBackPressed();
                            } else
                                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.deleteMaidFail), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterMaidsDataActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                    }
                } else if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String errorMsg = jObjError.getString("Message");
                        Toast.makeText(RegisterMaidsDataActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RegisterMaidsDataActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });


    }


}
