package com.khdamte.bitcode.khdamte_app.activities;

import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.SpinnerAdapter;
import com.khdamte.bitcode.khdamte_app.models.UserRegistrationModel;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class UserRegistationActivity extends AppCompatActivity {

    private EditText firstname, lastname, password, confirm_password, email, phone1, address;
    private TextView title_toolbar, userRoleTitle, firstNameHint, lastNameHint, emailHint, passwordHint, confirmPasswordHint,
            phoneHint, genderTitle, countryHint, cityHint, addressHint;
    private RadioGroup userRoleRadioGroup, genderRadioGroup;
    private View emailView, genderView;
    private LinearLayout genderLayout, emailLayout;
    private RadioButton personRadioBtn, officeRadioBtn, maleRadioBtn, femaleRadioBtn;
    private Spinner countrySpinner, citySpinner;

    private ImageView back_btn;
    private Button registration_btn;
    private AlertDialog progressDialog;

    private HashMap<String, String> country_hashMap;
    private HashMap<String, String> cities_hashMap;
    private String langToLoad;
    private SharedPreferences languagepref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registation);

        init();

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String email_str = email.getText().toString();
                if (hasFocus) {
                } else {
                    if (email_str.contains("@") && email_str.contains(".")) {
                    } else {
                        Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_email_correct), Toast.LENGTH_LONG).show();
                        email.setText("");
                    }
                }
            }
        });
        registration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userRole = personRadioBtn.isChecked() ? "person" : "office";
                String userGender = maleRadioBtn.isChecked() ? "male" : "female";
                String firstname_str = firstname.getText().toString();
                String lastname_str = lastname.getText().toString();
                String password_str = password.getText().toString();
                String confirm_password_str = confirm_password.getText().toString();
                String email_str = email.getText().toString();
                String phone1_str = phone1.getText().toString();

                String selectedStateName = citySpinner.getSelectedItem().toString();
                String stateId = "";
                if(!TextUtils.isEmpty(selectedStateName)){
                    stateId = cities_hashMap.get(selectedStateName);
                }
                String address_str = address.getText().toString();

                if (userRole.equals("person")) { // person
                    if (!TextUtils.isEmpty(firstname_str) &&
                            !TextUtils.isEmpty(lastname_str) &&
                            !TextUtils.isEmpty(password_str) &&
                            !TextUtils.isEmpty(confirm_password_str) &&
                            !TextUtils.isEmpty(phone1_str) &&
                            !TextUtils.isEmpty(userGender) &&
                            !TextUtils.isEmpty(stateId) &&
                            !TextUtils.isEmpty(address_str)) {
                        if (password_str.equals(confirm_password_str)) {


                        } else {
                            Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_con_pass), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_type_alldata), Toast.LENGTH_LONG).show();
                    }
                } else { // office
                    if (!TextUtils.isEmpty(firstname_str) &&
                            !TextUtils.isEmpty(lastname_str) &&
                            !TextUtils.isEmpty(password_str) &&
                            !TextUtils.isEmpty(confirm_password_str) &&
                            !TextUtils.isEmpty(email_str) &&
                            !TextUtils.isEmpty(phone1_str) &&
                            !TextUtils.isEmpty(stateId) &&
                            !TextUtils.isEmpty(address_str)) {
                        if (password_str.equals(confirm_password_str)) {

                            Intent ownerIntent = new Intent(UserRegistationActivity.this, OwnerOfficeRegistrationActivity.class);
                            Bundle b = new Bundle();
                            b.putParcelable("UserData", new UserRegistrationModel(userRole, firstname_str, lastname_str, email_str, password_str, phone1_str, userGender, stateId, address_str));
                            ownerIntent.putExtras(b);
                            startActivity(ownerIntent);
                        } else {
                            Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_con_pass), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_type_alldata), Toast.LENGTH_LONG).show();
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

        personRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registration_btn.setText(getString(R.string.register_user));

                emailHint.setVisibility(View.GONE);
                emailLayout.setVisibility(View.GONE);
                emailView.setVisibility(View.GONE);

                genderView.setVisibility(View.VISIBLE);
                genderLayout.setVisibility(View.VISIBLE);
            }
        });

        officeRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registration_btn.setText(getString(R.string.nextReg));
                emailHint.setVisibility(View.VISIBLE);
                emailLayout.setVisibility(View.VISIBLE);
                emailView.setVisibility(View.VISIBLE);

                genderView.setVisibility(View.GONE);
                genderLayout.setVisibility(View.GONE);
            }
        });

        GetAllCountries();
    }

    private void init() {
        back_btn = (ImageView) findViewById(R.id.back_btn);
        title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        userRoleTitle = (TextView) findViewById(R.id.userRoleTitle);
        userRoleRadioGroup = (RadioGroup) findViewById(R.id.userRoleRadioGroup);
        personRadioBtn = (RadioButton) findViewById(R.id.personRadioBtn);
        officeRadioBtn = (RadioButton) findViewById(R.id.officeRadioBtn);
        firstNameHint = (TextView) findViewById(R.id.firstNameHint);
        firstname = (EditText) findViewById(R.id.first_name_et);
        lastNameHint = (TextView) findViewById(R.id.lastNameHint);
        lastname = (EditText) findViewById(R.id.lastname_et);
        passwordHint = (TextView) findViewById(R.id.passwordHint);
        password = (EditText) findViewById(R.id.password_et);
        confirmPasswordHint = (TextView) findViewById(R.id.confirmPasswordHint);
        confirm_password = (EditText) findViewById(R.id.confirm_pass_editText);
        emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        emailHint = (TextView) findViewById(R.id.emailHint);
        email = (EditText) findViewById(R.id.email_editText);
        emailView = findViewById(R.id.emailView);
        phoneHint = (TextView) findViewById(R.id.phoneHint);
        phone1 = (EditText) findViewById(R.id.phone1_editText);
        genderTitle = (TextView) findViewById(R.id.genderTitle);
        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        maleRadioBtn = (RadioButton) findViewById(R.id.maleRadioBtn);
        femaleRadioBtn = (RadioButton) findViewById(R.id.femaleRadioBtn);
        countryHint = (TextView) findViewById(R.id.countryHint);
        countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        cityHint = (TextView) findViewById(R.id.cityHint);
        citySpinner = (Spinner) findViewById(R.id.citySpinner);
        addressHint = (TextView) findViewById(R.id.addressHint);
        address = (EditText) findViewById(R.id.address_editText);
        registration_btn = (Button) findViewById(R.id.registration_btn);
        progressDialog = new SpotsDialog(this, R.style.Custom);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        langToLoad = languagepref.getString("languageToLoad", null);

        title_toolbar.setTypeface(MainActivity.lightFace);
        userRoleTitle.setTypeface(MainActivity.lightFace);
        personRadioBtn.setTypeface(MainActivity.lightFace);
        officeRadioBtn.setTypeface(MainActivity.lightFace);
        firstNameHint.setTypeface(MainActivity.lightFace);
        firstname.setTypeface(MainActivity.lightFace);
        lastNameHint.setTypeface(MainActivity.lightFace);
        lastname.setTypeface(MainActivity.lightFace);
        passwordHint.setTypeface(MainActivity.lightFace);
        password.setTypeface(MainActivity.lightFace);
        confirmPasswordHint.setTypeface(MainActivity.lightFace);
        confirm_password.setTypeface(MainActivity.lightFace);
        emailHint.setTypeface(MainActivity.lightFace);
        email.setTypeface(MainActivity.lightFace);
        phoneHint.setTypeface(MainActivity.lightFace);
        phone1.setTypeface(MainActivity.lightFace);
        genderView = findViewById(R.id.genderView);
        genderLayout = (LinearLayout) findViewById(R.id.genderLayout);
        maleRadioBtn.setTypeface(MainActivity.lightFace);
        femaleRadioBtn.setTypeface(MainActivity.lightFace);
        countryHint.setTypeface(MainActivity.lightFace);
        cityHint.setTypeface(MainActivity.lightFace);
        addressHint.setTypeface(MainActivity.lightFace);
        address.setTypeface(MainActivity.lightFace);
        registration_btn.setTypeface(MainActivity.lightFace);

        ArrayList<String> countries_arrayList = new ArrayList<String>();
        countries_arrayList.add(getResources().getString(R.string.choose_country));
        final ArrayAdapter<String> country_spinnerArrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, countries_arrayList);
        country_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        countrySpinner.setAdapter(country_spinnerArrayAdapter);

        ArrayList<String> cities_arrayList = new ArrayList<String>();
        cities_arrayList.add(getResources().getString(R.string.choose_city));
        final ArrayAdapter<String> city_spinnerArrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, cities_arrayList);
        city_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        citySpinner.setAdapter(city_spinnerArrayAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // onClick Listener


    // API
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
                            final ArrayAdapter<String> country_spinnerArrayAdapter = new SpinnerAdapter(UserRegistationActivity.this, R.layout.spinner_item, countries_arrayList);
                            country_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            countrySpinner.setAdapter(country_spinnerArrayAdapter);
                            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                            Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_no_countries), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UserRegistationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
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
                        final ArrayAdapter<String> city_spinnerArrayAdapter = new SpinnerAdapter(UserRegistationActivity.this, R.layout.spinner_item, cities_arrayList);
                        city_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                        citySpinner.setAdapter(city_spinnerArrayAdapter);


                    } catch (JSONException e) {
                        FirebaseCrash.report(e);
                        e.printStackTrace();
                        Toast.makeText(UserRegistationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    // Helper methods
    private void dismissDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showDialog() {
        progressDialog.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
