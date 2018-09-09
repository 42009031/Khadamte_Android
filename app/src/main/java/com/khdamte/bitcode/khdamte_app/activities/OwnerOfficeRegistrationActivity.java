package com.khdamte.bitcode.khdamte_app.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.FetchPath;
import com.khdamte.bitcode.khdamte_app.adapter.Flags_Adapter;
import com.khdamte.bitcode.khdamte_app.adapter.SpinnerAdapter;
import com.khdamte.bitcode.khdamte_app.models.Flags_Model;
import com.khdamte.bitcode.khdamte_app.models.UserRegistrationModel;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class OwnerOfficeRegistrationActivity extends AppCompatActivity {

    private ImageView back_btn;
    private TextView title_toolbar, officeNameHint, descriptionHint, phone1Hint, phone2Hint, phone3Hint, nationalityHint, otherServicesHint, otherServicesTv, officeLogoHint;
    private EditText officeNameEt, descriptionEt, phone1Et, phone2Et, phone3Et;
    private ImageView officeLogo;
    private Spinner nationalitySpinner, otherServicesSpinner;
    private RecyclerView flagsRecycleview;
    private Button registration_btn;
    private Flags_Adapter myAdapter;
    private AlertDialog progressDialog;

    private HashMap<String, ArrayList<Flags_Model>> nat_name_hashMap;
    private HashMap<String, ArrayList<Flags_Model>> nat_id_hashMap;
    private ArrayList<Flags_Model> flags_models;
    private ArrayList<Flags_Model> nationality_list;
    private Set<String> nat_selected_hashSet;
    private String postOtherSer = "", selected_national = "", langToLoad, captureImgUri = "";

    private SharedPreferences languagepref;
    private UserRegistrationModel userData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_office_layout);

        init();

        if (isNetworkAvailable()) {
            GetAllNationalities();
            GetOtherServices();
        } else {
            Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
        }

        registration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                captureImgUri
                String officeName = officeNameEt.getText().toString();
                String officeDesc = descriptionEt.getText().toString();
                String phone1 = phone1Et.getText().toString();
                String phone2 = phone2Et.getText().toString();
                String phone3 = phone3Et.getText().toString();
                StringBuffer nationBuffer = new StringBuffer();
                String maid_nationalities = "";
                if (nat_selected_hashSet.size() != 0) {
                    for (String ids : nat_selected_hashSet) {
                        nationBuffer.append(ids);
                        nationBuffer.append(",");
                    }
                    maid_nationalities = String.valueOf(nationBuffer);
                    maid_nationalities = maid_nationalities.substring(0, maid_nationalities.length() - 1);
                }

                if (!TextUtils.isEmpty(officeName) &&
                        !TextUtils.isEmpty(officeDesc) &&
                        !TextUtils.isEmpty(phone1) &&
                        !TextUtils.isEmpty(phone2) &&
                        !TextUtils.isEmpty(maid_nationalities)) {

                    if (isNetworkAvailable()) {
                        postOffice(officeName,
                                phone1,
                                phone2,
                                phone3,
                                userData.getState(),
                                userData.getAddress(),
                                officeDesc,
                                userData.getEmail(),
                                userData.getCountryId(),
                                maid_nationalities,
                                postOtherSer);
                    } else {
                        Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_type_alldata), Toast.LENGTH_LONG).show();
                }
            }
        });

        officeLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {

        userData = getIntent().getParcelableExtra("UserData");

        back_btn = (ImageView) findViewById(R.id.back_btn);
        title_toolbar = (TextView) findViewById(R.id.title_toolbar);

        officeLogo = (ImageView) findViewById(R.id.officeLogo);
        officeLogoHint = (TextView) findViewById(R.id.officeLogoHint);

        officeNameHint = (TextView) findViewById(R.id.officeNameHint);
        officeNameEt = (EditText) findViewById(R.id.officeNameEt);

        descriptionHint = (TextView) findViewById(R.id.descriptionHint);
        descriptionEt = (EditText) findViewById(R.id.descriptionEt);

        phone1Hint = (TextView) findViewById(R.id.phone1Hint);
        phone1Et = (EditText) findViewById(R.id.phone1Et);

        phone2Hint = (TextView) findViewById(R.id.phone2Hint);
        phone2Et = (EditText) findViewById(R.id.phone2Et);

        phone3Hint = (TextView) findViewById(R.id.phone3Hint);
        phone3Et = (EditText) findViewById(R.id.phone3Et);

        nationalityHint = (TextView) findViewById(R.id.nationalityHint);
        otherServicesHint = (TextView) findViewById(R.id.otherServicesHint);

        nationalitySpinner = (Spinner) findViewById(R.id.nationalitySpinner);
        flagsRecycleview = (RecyclerView) findViewById(R.id.flagsRecycleview);

        otherServicesSpinner = (Spinner) findViewById(R.id.otherServicesSpinner);
        otherServicesTv = (TextView) findViewById(R.id.otherServicesTv);

        registration_btn = (Button) findViewById(R.id.registration_btn);

        progressDialog = new SpotsDialog(this, R.style.Custom);

        title_toolbar.setTypeface(MainActivity.lightFace);
        officeLogoHint.setTypeface(MainActivity.lightFace);
        officeNameHint.setTypeface(MainActivity.lightFace);
        officeNameEt.setTypeface(MainActivity.lightFace);
        descriptionHint.setTypeface(MainActivity.lightFace);
        descriptionEt.setTypeface(MainActivity.lightFace);
        phone1Hint.setTypeface(MainActivity.lightFace);
        phone1Et.setTypeface(MainActivity.lightFace);
        phone2Hint.setTypeface(MainActivity.lightFace);
        phone2Et.setTypeface(MainActivity.lightFace);
        phone3Hint.setTypeface(MainActivity.lightFace);
        phone3Et.setTypeface(MainActivity.lightFace);
        nationalityHint.setTypeface(MainActivity.lightFace);
        otherServicesHint.setTypeface(MainActivity.lightFace);
        otherServicesTv.setTypeface(MainActivity.lightFace);
        registration_btn.setTypeface(MainActivity.lightFace);

        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        langToLoad = languagepref.getString("languageToLoad", null);
        title_toolbar.setText(getResources().getString(R.string.add_office));


        ArrayList<String> nation_arrayList = new ArrayList<String>();
        nation_arrayList.add(getResources().getString(R.string.maidsNation));
        final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, nation_arrayList);
        nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        nationalitySpinner.setAdapter(nation_ArrayAdapter);

        ArrayList<String> servicesArrayList = new ArrayList<String>();
        servicesArrayList.add(getResources().getString(R.string.otherServices));
        final ArrayAdapter<String> otherServicesArrayAdapter = new SpinnerAdapter(OwnerOfficeRegistrationActivity.this, R.layout.spinner_item, servicesArrayList);
        otherServicesArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        otherServicesSpinner.setAdapter(otherServicesArrayAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // helper methods 
    private void dismissDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showDialog() {
        progressDialog.show();
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
                        ActivityCompat.requestPermissions(OwnerOfficeRegistrationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(OwnerOfficeRegistrationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
        } else {
            Intent localIntent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(localIntent, 5);
        }
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
            Picasso.with(this).load(String.valueOf(paramIntent.getData())).transform(new CircleTransform()).into(this.officeLogo);
        }
    }

    // API
    private void GetAllNationalities() {
        showDialog();
        nat_name_hashMap = new HashMap<String, ArrayList<Flags_Model>>();
        nat_id_hashMap = new HashMap<String, ArrayList<Flags_Model>>();
        final ArrayList<String> flags_arrayList = new ArrayList<>();
        flags_arrayList.add(getResources().getString(R.string.maidsNation));

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
                                    String[] nameArray = nat_name.split(",");
                                    String ar = nameArray[1];
                                    String eng = nameArray[0];
                                    if (langToLoad.equals("العربية")) {
                                        nat_name = ar.trim();
                                    } else {
                                        nat_name = eng.trim();
                                    }
                                }
                                String nat_img = adsObj.getString("pic");
                                nat_img = "http://www.khadamte.com" + nat_img;
                                flags_models = new ArrayList<>();
                                flags_models.add(new Flags_Model(nat_name, nat_id, nat_img));
                                flags_arrayList.add(nat_name);
                                nat_name_hashMap.put(nat_name, flags_models);
                                nat_id_hashMap.put(nat_id, flags_models);
                            }
                            nationality_list = new ArrayList<Flags_Model>();
                            nat_selected_hashSet = new HashSet<String>();
                            final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(OwnerOfficeRegistrationActivity.this, R.layout.spinner_item, flags_arrayList);
                            nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            nationalitySpinner.setAdapter(nation_ArrayAdapter);
                            nationalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (position > 0) {
                                        selected_national = parent.getItemAtPosition(position).toString();
                                        nationality_list.addAll(nat_name_hashMap.get(selected_national));

                                        for (int i = 0; i < nationality_list.size(); i++) {
                                            String nat_name = nationality_list.get(i).getFlag_name();
                                            String nat_id = nationality_list.get(i).getFlag_id();
                                            if (nat_name.equals(selected_national)) {
                                                nat_selected_hashSet.add(nat_id);
                                            }
                                        }
                                        nationality_list = new ArrayList<Flags_Model>();
                                        for (String nat_ids : nat_selected_hashSet) {
                                            nationality_list.addAll(nat_id_hashMap.get(nat_ids));
                                        }
                                        myAdapter = new Flags_Adapter(OwnerOfficeRegistrationActivity.this, nationality_list);
                                        flagsRecycleview.setAdapter(myAdapter);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_no_nationality), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(OwnerOfficeRegistrationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void GetOtherServices() {
        showDialog();
        final ArrayList<String> nameOfOthSerArray = new ArrayList<>();
        nameOfOthSerArray.add(getString(R.string.add_other_ser));
        final Map<String, String> nameOfServicesMap = new HashMap<>();

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getOtherServices();
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONArray serJsonArray = obj.getJSONArray("Response");
                        if (serJsonArray.length() != 0) {
                            for (int i = 0; i < serJsonArray.length(); i++) {
                                JSONObject serObj = serJsonArray.getJSONObject(i);
                                String ser_id = serObj.getString("id");
                                String ser_name = serObj.getString("name");
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

                                nameOfServicesMap.put(ser_name, ser_id);
                                nameOfOthSerArray.add(ser_name);
                            }

                            final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(OwnerOfficeRegistrationActivity.this, R.layout.spinner_item, nameOfOthSerArray);
                            nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            otherServicesSpinner.setAdapter(nation_ArrayAdapter);

                            final Set<String> other_services_Set = new HashSet<String>();

                            otherServicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (position > 0) {
                                        final StringBuilder otherSerName = new StringBuilder();
                                        final StringBuilder otherSerId = new StringBuilder();
                                        String selectedItemText = (String) parent.getItemAtPosition(position);
                                        other_services_Set.add(selectedItemText);
                                        for (String keys : other_services_Set) {
                                            otherSerName.append(keys + ", ");
                                            otherSerId.append(nameOfServicesMap.get(keys) + ",");
                                            otherServicesTv.setText(otherSerName.substring(0, otherSerName.length() - 2));
                                        }
                                        postOtherSer = String.valueOf(otherSerId.substring(0, otherSerId.length() - 1));
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
//                            Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_no_nationality), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(OwnerOfficeRegistrationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) OwnerOfficeRegistrationActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void postOffice(String officeName, String officeMob1, String officeMob2, String officeMob3, String city_id,
                            String address, String officeDesc, String userEmail, String country_id, String nationalities, String otherServices) {
        showDialog();

        JsonObject userObj = new JsonObject();
        userObj.addProperty("fname", userData.getfName());
        userObj.addProperty("lname", userData.getlName());
        userObj.addProperty("pwd", userData.getPwd());
        userObj.addProperty("phone1", userData.getPhone());
        userObj.addProperty("email", userEmail);
        userObj.addProperty("address", userData.getAddress());
        userObj.addProperty("userRole", userData.getRole());
        userObj.addProperty("StateMasterId", userData.getState());

        JsonObject officeObj = new JsonObject();
        officeObj.addProperty("name", officeName);
        officeObj.addProperty("mob1", officeMob1);
        officeObj.addProperty("mob2", officeMob2);
        officeObj.addProperty("mob3", officeMob3);
        officeObj.addProperty("cityid", city_id);
        officeObj.addProperty("address", address);
        officeObj.addProperty("description", officeDesc);
        officeObj.addProperty("email", userEmail);
        officeObj.addProperty("countryid", country_id);

        JsonArray nationalitiesArray = new JsonArray();
        if (nationalities.contains(",")) {
            String[] natIds = nationalities.split(",");
            for (String nationId : natIds) {
                JsonObject nationObj = new JsonObject();
                nationObj.addProperty("id", nationId);
                nationalitiesArray.add(nationObj);
            }
        } else {
            if (!TextUtils.isEmpty(nationalities)) {
                JsonObject nationObj = new JsonObject();
                nationObj.addProperty("id", nationalities);
                nationalitiesArray.add(nationObj);
            }
        }


        JsonArray otherServArray = new JsonArray();
        if (otherServices.contains(",")) {
            String[] otherServIds = otherServices.split(",");
            for (String otherServId : otherServIds) {
                JsonObject otherServObj = new JsonObject();
                otherServObj.addProperty("id", otherServId);
                otherServArray.add(otherServObj);
            }
        } else {
            if (!TextUtils.isEmpty(otherServices)) {
                JsonObject otherServObj = new JsonObject();
                otherServObj.addProperty("id", otherServices);
                otherServArray.add(otherServObj);
            }
        }

        officeObj.add("nationalities", nationalitiesArray);
        officeObj.add("OtherServices", otherServArray);
        JsonArray officeArray = new JsonArray();
        officeArray.add(officeObj);

        userObj.add("Offices", officeArray);

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.postRegistration(userObj);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissDialog();
                String result = "";
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject adsObj = new JSONObject(result);
                        if (adsObj.toString().contains("Success")) {
                            boolean success = adsObj.getBoolean("Success");
                            String officeId = adsObj.getString("OfficeId");
                            String userId = adsObj.getString("id");
                            if (success) {
                                if(TextUtils.isEmpty(captureImgUri)){
                                    Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_off_reg), Toast.LENGTH_LONG).show();

                                    Bundle b = new Bundle();
                                    b.putString("fName", userData.getfName());
                                    b.putString("lName", userData.getlName());
                                    b.putString("useId", userId);
                                    b.putString("officeId", officeId);
                                    startActivity(new Intent(OwnerOfficeRegistrationActivity.this, ConfirmRegistration.class).putExtras(b));
                                }else{
                                    uploadOfficeImage(userId, officeId, captureImgUri);
                                }


                            } else {
                                Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_off_not_reg), Toast.LENGTH_LONG).show();
                            }
                        } else if (adsObj.toString().contains("This user has been saved before")) {
                            Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_user_exist), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(OwnerOfficeRegistrationActivity.this, adsObj.toString(), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(OwnerOfficeRegistrationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }


    private void uploadOfficeImage(final String userId, final String officeId, String imageUri) {
        showDialog();
        File file = new File(imageUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);

        RequestBody name_RB = RequestBody.create(MediaType.parse("multipart/form-data"), officeNameEt.getText().toString());
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<ResponseBody> connection = KHADAMTY_API.AddOfficePhoto(officeId, body);

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
                            if (Status.equals("Success")) {
                                Toast.makeText(OwnerOfficeRegistrationActivity.this, "Image upload success", Toast.LENGTH_LONG).show();
                                Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_off_reg), Toast.LENGTH_LONG).show();
                                Bundle b = new Bundle();
                                b.putString("fName", userData.getfName());
                                b.putString("lName", userData.getlName());
                                b.putString("useId", userId);
                                b.putString("officeId", officeId);
                                startActivity(new Intent(OwnerOfficeRegistrationActivity.this, ConfirmRegistration.class).putExtras(b));
                            } else
                                Toast.makeText(OwnerOfficeRegistrationActivity.this, Status, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(OwnerOfficeRegistrationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                    }
                } else if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String errorMsg = jObjError.getString("Message");
                        Toast.makeText(OwnerOfficeRegistrationActivity.this, errorMsg, Toast.LENGTH_LONG).show();
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
                Toast.makeText(OwnerOfficeRegistrationActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }
}
