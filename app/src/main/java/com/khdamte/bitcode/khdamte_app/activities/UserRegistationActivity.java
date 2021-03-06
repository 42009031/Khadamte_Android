package com.khdamte.bitcode.khdamte_app.activities;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.SharedPreferencesManager;
import com.khdamte.bitcode.khdamte_app.adapter.SpinnerAdapter;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.models.UserRegistrationModel;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    private Spinner countrySpinner, citySpinner, countryKeySpinner;

    private ImageView back_btn;
    private Button registration_btn, verifyPhoneBtn;
    private AlertDialog progressDialog;
    private HashMap<String, String> country_hashMap;
    private HashMap<String, String> cities_hashMap;
    private ArrayList<String> countrieKeys_arrayList;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth fbAuth;
    private boolean isAvalidPhoneNumber;

    private static final String TAG = "PhoneAuth";

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
                String countryKey = countrieKeys_arrayList.get(countryKeySpinner.getSelectedItemPosition());
                String phone_str = phone1.getText().toString();
                String country_id = country_hashMap.get(countrySpinner.getSelectedItem().toString());
                String selectedStateName = citySpinner.getSelectedItem().toString();
                String stateId = "";
                if (!TextUtils.isEmpty(selectedStateName) && !selectedStateName.equals(getResources().getString(R.string.choose_city))) {
                    stateId = cities_hashMap.get(selectedStateName);
                }
                String address_str = address.getText().toString();

                if (userRole.equals("person")) { // person
                    if (!TextUtils.isEmpty(firstname_str) &&
                            !TextUtils.isEmpty(lastname_str) &&
                            !TextUtils.isEmpty(password_str) &&
                            !TextUtils.isEmpty(confirm_password_str) &&
                            !TextUtils.isEmpty(phone_str) &&
                            !TextUtils.isEmpty(userGender) &&
                            !TextUtils.isEmpty(stateId) &&
                            !TextUtils.isEmpty(address_str)) {
                        if (password_str.equals(confirm_password_str)) {

                            if (isAvalidPhoneNumber) {
                                postUserPerson(userRole,
                                        firstname_str,
                                        lastname_str,
                                        password_str,
                                        phone_str,
                                        userGender,
                                        stateId,
                                        address_str);
                            } else {
                                Toast.makeText(UserRegistationActivity.this, getString(R.string.verificationCodeError), Toast.LENGTH_LONG).show();
                            }

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
                            !TextUtils.isEmpty(phone_str) &&
                            !TextUtils.isEmpty(stateId) &&
                            !TextUtils.isEmpty(address_str)) {
                        if (password_str.equals(confirm_password_str)) {

                            if (isAvalidPhoneNumber) {
                                Intent ownerIntent = new Intent(UserRegistationActivity.this, OwnerOfficeRegistrationActivity.class);
                                Bundle b = new Bundle();
                                b.putParcelable("UserData", new UserRegistrationModel(userRole = userRole.equalsIgnoreCase("person") ? "user" : "owner",
                                        firstname_str,
                                        lastname_str,
                                        email_str,
                                        password_str,
                                        phone_str,
                                        userGender,
                                        stateId,
                                        country_id,
                                        address_str));
                                ownerIntent.putExtras(b);
                                startActivity(ownerIntent);
                            } else {
                                Toast.makeText(UserRegistationActivity.this, getString(R.string.verificationCodeError), Toast.LENGTH_LONG).show();
                            }
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

        verifyPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(countrieKeys_arrayList.get(countryKeySpinner.getSelectedItemPosition())) &&
                        !TextUtils.isEmpty(phone1.getText().toString())) {

                    showDialog();
                    String phoneNumber = countrieKeys_arrayList.get(countryKeySpinner.getSelectedItemPosition()) + phone1.getText().toString();
                    setUpVerificatonCallbacks();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            UserRegistationActivity.this,               // Activity (for callback binding)
                            verificationCallbacks);
                } else {
                    Toast.makeText(UserRegistationActivity.this, "Please type correct phone number", Toast.LENGTH_SHORT).show();
                }

            }
        });

        GetAllCountries();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

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
                                    if (SharedPreferencesManager.getStringValue(Helper.LOCALE).equals(Helper.AR)) {
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
                                if (SharedPreferencesManager.getStringValue(Helper.LOCALE).equals(Helper.AR)) {
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

    private void postUserPerson(String role,
                                final String fname,
                                final String lname,
                                String pwd,
                                String phone,
                                String gender,
                                String stateId,
                                String address) {

        JsonObject userObj = new JsonObject();
        userObj.addProperty("fname", fname);
        userObj.addProperty("lname", lname);
        userObj.addProperty("pwd", pwd);
        userObj.addProperty("phone1", phone);
        userObj.addProperty("address", address);
        userObj.addProperty("isApproved", true);
        userObj.addProperty("userRole", role = role.equalsIgnoreCase("person") ? "user" : "owner");
        userObj.addProperty("StateMasterId", stateId);
        userObj.addProperty("GenderId", gender = gender.equalsIgnoreCase("male") ? "1" : "2");

        showDialog();
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
                            String userId = adsObj.getString("id");
                            if (success) {
                                SharedPreferences sharedpreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("id", userId);
                                editor.putString("userRole", "user");
                                editor.apply();

                                if (Build.VERSION.SDK_INT >= 23) {
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.userWarning), 10000)
                                            .setAction("OK", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Toast.makeText(UserRegistationActivity.this, "Welcome " + fname + " " + lname, Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(UserRegistationActivity.this, MainActivity.class));
                                                }
                                            });
                                    snackbar.show();
                                    new CountDownTimer(7000, 1000) {

                                        public void onTick(long millisUntilFinished) {
                                        }

                                        public void onFinish() {
                                            startActivity(new Intent(UserRegistationActivity.this, MainActivity.class));
                                        }
                                    }.start();

                                } else {
                                    Toast.makeText(UserRegistationActivity.this, getString(R.string.userWarning), Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(UserRegistationActivity.this, MainActivity.class));
                                }

                            } else {
                                Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_reg_failed), Toast.LENGTH_LONG).show();
                            }
                        } else if (adsObj.toString().contains("This user has been saved before")) {
                            Toast.makeText(UserRegistationActivity.this, getResources().getString(R.string.toast_user_exist), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(UserRegistationActivity.this, adsObj.toString(), Toast.LENGTH_LONG).show();
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


    // Helper methods
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
        countryKeySpinner = (Spinner) findViewById(R.id.countryKeySpinner);
        phone1 = (EditText) findViewById(R.id.phone1_editText);
        verifyPhoneBtn = (Button) findViewById(R.id.verifyPhoneBtn);
        genderView = findViewById(R.id.genderView);
        genderLayout = (LinearLayout) findViewById(R.id.genderLayout);
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

        fbAuth = FirebaseAuth.getInstance();

        title_toolbar.setTypeface(Helper.getTypeFace());
        userRoleTitle.setTypeface(Helper.getTypeFace());
        personRadioBtn.setTypeface(Helper.getTypeFace());
        officeRadioBtn.setTypeface(Helper.getTypeFace());
        firstNameHint.setTypeface(Helper.getTypeFace());
        firstname.setTypeface(Helper.getTypeFace());
        lastNameHint.setTypeface(Helper.getTypeFace());
        lastname.setTypeface(Helper.getTypeFace());
        passwordHint.setTypeface(Helper.getTypeFace());
        password.setTypeface(Helper.getTypeFace());
        confirmPasswordHint.setTypeface(Helper.getTypeFace());
        confirm_password.setTypeface(Helper.getTypeFace());
        emailHint.setTypeface(Helper.getTypeFace());
        email.setTypeface(Helper.getTypeFace());
        phoneHint.setTypeface(Helper.getTypeFace());
        phone1.setTypeface(Helper.getTypeFace());
        genderTitle.setTypeface(Helper.getTypeFace());
        maleRadioBtn.setTypeface(Helper.getTypeFace());
        femaleRadioBtn.setTypeface(Helper.getTypeFace());
        countryHint.setTypeface(Helper.getTypeFace());
        cityHint.setTypeface(Helper.getTypeFace());
        addressHint.setTypeface(Helper.getTypeFace());
        address.setTypeface(Helper.getTypeFace());
        registration_btn.setTypeface(Helper.getTypeFace());
        verifyPhoneBtn.setTypeface(Helper.getTypeFace());

        Helper.setSrc4BackImg(back_btn);

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


        countrieKeys_arrayList = new ArrayList<String>();
        countrieKeys_arrayList.add("+965");
        countrieKeys_arrayList.add("+971");
        countrieKeys_arrayList.add("+966");
        countrieKeys_arrayList.add("+2");
        final ArrayAdapter<String> countryKeys_spinnerArrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, countrieKeys_arrayList);
        countryKeys_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        countryKeySpinner.setAdapter(countryKeys_spinnerArrayAdapter);
    }

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

    private void setUpVerificatonCallbacks() {

        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                dismissDialog();
                DisplayDialog();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                isAvalidPhoneNumber = false;
                dismissDialog();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d(TAG, "Invalid credential: " + e.getLocalizedMessage());
                    Toast.makeText(UserRegistationActivity.this, getString(R.string.innvalidCredential), Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
                    Log.d(TAG, "SMS Quota exceeded.");
                    Toast.makeText(UserRegistationActivity.this, getString(R.string.smsQota), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UserRegistationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                phoneVerificationId = verificationId;
                resendToken = token;
            }
        };
    }

    private void DisplayDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.verificationCodeTitle));
        alertDialog.setMessage(getString(R.string.verificationCodeBody));

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint(getString(R.string.verificationCodeBody));
        input.setTypeface(Helper.getTypeFace());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setGravity(Gravity.CENTER);

        alertDialog.setView(input);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialog.setPositiveButton(getString(R.string.verify), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String code = input.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        });

        alertDialog.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    Toast.makeText(UserRegistationActivity.this, getString(R.string.validverificationCode), Toast.LENGTH_LONG).show();
                    phone1.setEnabled(false);
                    phone1.setClickable(false);
                    countryKeySpinner.setEnabled(false);
                    countryKeySpinner.setClickable(false);
                    isAvalidPhoneNumber = true;
                } else {
                    isAvalidPhoneNumber = false;
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(UserRegistationActivity.this, getString(R.string.invalidverificationCode), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void signOut() {
        fbAuth.signOut();
    }

}
