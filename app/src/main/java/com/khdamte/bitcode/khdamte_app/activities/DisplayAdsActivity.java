package com.khdamte.bitcode.khdamte_app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.ui.TouchImageView;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

/**
 * Created by Amado on 4/5/2017.
 */

public class DisplayAdsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView close_imgView, call_iv, ads_details_btn;
    private TouchImageView ads_imgView;
    private TextView view_tv, close_tv;
    private String phone1 = "", phone2 = "";
    private String office_id;
    private AlertDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.display_ads_fullscreen_layout);

        close_imgView = (ImageView) findViewById(R.id.close_btn);
        close_tv = (TextView) findViewById(R.id.close_tv);
        ads_imgView = (TouchImageView) findViewById(R.id.ads_imgview);

        ads_details_btn = (ImageView) findViewById(R.id.ads_details_btn);
        call_iv = (ImageView) findViewById(R.id.call_iv);
        view_tv = (TextView) findViewById(R.id.view_tv);

        progressDialog = new SpotsDialog(this, R.style.Custom);

        ads_details_btn.setOnClickListener(this);
        close_imgView.setOnClickListener(this);
        call_iv.setOnClickListener(this);

        view_tv.setTypeface(MainActivity.lightFace);
        close_tv.setTypeface(MainActivity.lightFace);

        String ads_img = getIntent().getExtras().getString("ads_img");
        office_id = getIntent().getExtras().getString("office_id");

        if (isNetworkAvailable()) {
            GetOfficeInfo(office_id);
            postView(office_id);
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
        }
        Picasso.with(this).load(ads_img).into(ads_imgView);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void dismissDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showDialog() {
        progressDialog.show();
    }

    private void GetOfficeInfo(String office_id) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getAdsInfo(office_id);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissDialog();
                String result = "";
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject adsObj = new JSONObject(result);
                        JSONObject Obj = (JSONObject) adsObj.get("Response");
                        String views = Obj.getString("views");
                        phone2 = Obj.getString("mob2");
                        phone1 = Obj.getString("mob1");
                        view_tv.setText(views);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DisplayAdsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DisplayAdsActivity.this, getResources().getString(R.string.toast_no_ads_info), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DisplayAdsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void postView(String office_id) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.postView(office_id);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissDialog();
                String result = "";
                result = response.body().toString();
                if (!result.equals("")) {
                } else {
                    Toast.makeText(DisplayAdsActivity.this, getResources().getString(R.string.toast_no_ads_info), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DisplayAdsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void postOfficeCall(String office_id) {
        showDialog();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("officeId", office_id);

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.postCall(jsonObj);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        boolean success = mainJsonObj.getBoolean("Success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DisplayAdsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DisplayAdsActivity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DisplayAdsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (!phone1.equals("") && !phone2.equals("")) {
                        onCall(phone1);
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.toast_call_permission), Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    public void onCall(String phone_number) {
        int permissionCheck = ContextCompat.checkSelfPermission(DisplayAdsActivity.this, Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DisplayAdsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + phone_number)));
            postOfficeCall(office_id);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ads_details_btn:
                if (isNetworkAvailable()) {
                    startActivity(new Intent(DisplayAdsActivity.this, DetailsActivity.class).putExtra("office_id", office_id));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.call_iv:
                if (!phone1.equals("") && !phone2.equals("")) {
                    new AlertDialog.Builder(DisplayAdsActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.call_dialog_title))
                            .setMessage(getString(R.string.call_dialog_body)+" ["+phone1+"] ? ")
                            .setPositiveButton("Call", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    Handler mHandler = new Handler(getMainLooper());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            onCall(phone1);
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();

                }
                break;
            case R.id.close_btn:
                finish();
                break;
        }
    }
}
