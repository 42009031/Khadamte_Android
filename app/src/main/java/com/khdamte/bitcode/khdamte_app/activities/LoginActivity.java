package com.khdamte.bitcode.khdamte_app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class LoginActivity extends AppCompatActivity {

    private EditText username_et, password_et;
    private Button login_btn, register_new_user, forget_pass_btn;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username_et = (EditText) findViewById(R.id.username_editText);
        password_et = (EditText) findViewById(R.id.password_editText);

        login_btn = (Button) findViewById(R.id.login_btn);
        forget_pass_btn = (Button) findViewById(R.id.forget_pass_btn);
        register_new_user = (Button) findViewById(R.id.register_now_btn);
        progressDialog = new SpotsDialog(this, R.style.Custom);

        login_btn.setTypeface(Helper.getTypeFace(), Typeface.BOLD);
        forget_pass_btn.setTypeface(Helper.getTypeFace());
        register_new_user.setTypeface(Helper.getTypeFace());
        username_et.setTypeface(Helper.getTypeFace());
        password_et.setTypeface(Helper.getTypeFace());

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_et.getText().toString();
                String password = password_et.getText().toString();
                if (isNetworkAvailable()) {
                    if ((!username.equals("")) && (!password.equals(""))) {
                        PostLogin(username, password);
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_type_email_pass), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
                }


            }
        });
        forget_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_et.getText().toString();
                if (isNetworkAvailable()) {
                    if (!username.equals("")) {
                        forgetPassword(username);
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_type_email), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
                }
            }
        });
        register_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, UserRegistationActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void PostLogin(final String emailAddress, String password) {

        JsonObject userObj = new JsonObject();
        userObj.addProperty("email", emailAddress);
        userObj.addProperty("pwd", password);

        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.postLogin(userObj);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissDialog();
                String result = "";
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        if (result.contains("Response")) {
                            JSONObject mainObj = new JSONObject(result);
                            JSONObject loginObj = mainObj.getJSONObject("Response");
                            String id = loginObj.getString("id");
                            String userRole = loginObj.getString("userRole");
                            SharedPreferences sharedpreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("id", id);
                            editor.putString("userRole", userRole);
                            editor.apply();
                            Toast.makeText(LoginActivity.this, "Welcome " + emailAddress, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else if (result.contains("Success")) {
                            JSONObject adsObj = new JSONObject(result);
                            String success = adsObj.getString("Success");
                            if (success.equals("Your data is ok but you wait to be approved")) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_pending_account), Toast.LENGTH_LONG).show();
                            } else if (success.equals("false")) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_invalid_email_pass), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });

    }

    private void forgetPassword(final String emailAddress) {

        JsonObject userObj = new JsonObject();
        userObj.addProperty("email", emailAddress);

        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonArray> connection = KHADAMTY_API.postForgotPassword(userObj);
        connection.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                dismissDialog();
                String result = "";
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONArray mainObj = new JSONArray(result);
                        JSONObject obj = mainObj.getJSONObject(0);
                        String message = obj.getString("Message");
                        if (message.equals("Please Check you Spam on Mail")) {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_check_eail), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_invalid_email), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
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
