package com.khdamte.bitcode.khdamte_app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.Flags_Adapter;
import com.khdamte.bitcode.khdamte_app.adapter.SpinnerAdapter;
import com.khdamte.bitcode.khdamte_app.models.Flags_Model;
import com.khdamte.bitcode.khdamte_app.models.UserRegistrationModel;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class OwnerOfficeRegistrationActivity extends AppCompatActivity{

    private ImageView back_btn;
    private TextView title_toolbar, officeNameHint, descriptionHint, phone1Hint, phone2Hint, phone3Hint, nationalityHint, otherServicesHint, otherServicesTv;
    private EditText officeNameEt, descriptionEt, phone1Et, phone2Et, phone3Et;
    private Spinner nationalitySpinner, otherServicesSpinner;
    private RecyclerView flagsRecycleview;
    private Flags_Adapter myAdapter;
    private AlertDialog progressDialog;

    private HashMap<String, ArrayList<Flags_Model>> nat_name_hashMap;
    private HashMap<String, ArrayList<Flags_Model>> nat_id_hashMap;
    private ArrayList<Flags_Model> flags_models;
    private ArrayList<Flags_Model> nationality_list;
    private Set<String> nat_selected_hashSet;
    private String postOtherSer, selected_national, langToLoad ;

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

        progressDialog = new SpotsDialog(this, R.style.Custom);

        title_toolbar.setTypeface(MainActivity.lightFace);
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

        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        langToLoad = languagepref.getString("languageToLoad", null);
        title_toolbar.setText(getResources().getString(R.string.add_office));


        ArrayList<String> nation_arrayList = new ArrayList<String>();
        nation_arrayList.add(getResources().getString(R.string.nationality_maids));
        final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, nation_arrayList);
        nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        nationalitySpinner.setAdapter(nation_ArrayAdapter);

        ArrayList<String> servicesArrayList = new ArrayList<String>();
        servicesArrayList.add(getResources().getString(R.string.chooseOtherSer));
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
    
    // API
    private void GetAllNationalities() {
        showDialog();
        nat_name_hashMap = new HashMap<String, ArrayList<Flags_Model>>();
        nat_id_hashMap = new HashMap<String, ArrayList<Flags_Model>>();
        final ArrayList<String> flags_arrayList = new ArrayList<>();
        flags_arrayList.add(getResources().getString(R.string.nationality_maids));

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
                                        for(String keys: other_services_Set){
                                            otherSerName.append(keys+", ");
                                            otherSerId.append(nameOfServicesMap.get(keys)+",");
                                            otherServicesTv.setText(otherSerName.substring(0, otherSerName.length()-2));
                                        }
                                        postOtherSer = String.valueOf(otherSerId.substring(0, otherSerId.length()-1));
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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) OwnerOfficeRegistrationActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
