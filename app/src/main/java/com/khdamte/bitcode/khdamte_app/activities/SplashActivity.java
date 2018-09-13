package com.khdamte.bitcode.khdamte_app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.SharedPreferencesManager;
import com.khdamte.bitcode.khdamte_app.models.AdsModel;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class SplashActivity extends AppCompatActivity {


    private ImageView logo_img, en_imgView, ar_imgView;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen_layout);

       init();

        if (!getLanguage().equals("")) {
            setLocal(getLanguage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkAvailable()) {
            GetAllAdsFromServer();
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    // helper methods
    private void init() {
        MobileAds.initialize(this, getResources().getString(R.string.app_ad_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        logo_img = (ImageView) findViewById(R.id.logo_imgview);
        en_imgView = (ImageView) findViewById(R.id.eng_logo_imgview);
        ar_imgView = (ImageView) findViewById(R.id.ar_logo_imgview);

        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        logo_img.startAnimation(animation1);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        en_imgView.startAnimation(animation);
        ar_imgView.startAnimation(animation);
    }

    public static String getLanguage() {
        return SharedPreferencesManager.getStringValue(Helper.LOCALE);
    }

    private void setLocal(String mComingLocal) {
        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        Locale local = Locale.getDefault();
        if (mComingLocal.contains(Helper.AR)) {
            local = new Locale(Helper.AR, "eg");
        } else {
            local = new Locale(Helper.EN, "us");
        }
        Locale.setDefault(local);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(local);
            conf.setLayoutDirection(local);
        } else {
            conf.locale = local;
        }
        res.updateConfiguration(conf, dm);

    }

    // APIs
    private void GetAllAdsFromServer() {
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getAds();
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                result = response.body().toString();
                ArrayList<AdsModel> adsModels = new ArrayList<AdsModel>();
                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        JSONArray adsJsonArray = mainJsonObj.getJSONArray("Response");
                        if (adsJsonArray.length() == 0) {
                            Toast.makeText(SplashActivity.this, getResources().getString(R.string.toast_no_ads_found), Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < adsJsonArray.length(); i++) {
                                JSONObject adsObj = adsJsonArray.getJSONObject(i);
                                String ads_img = adsObj.getString("advertise");
                                String office_name = adsObj.getString("name");
                                String office_id = adsObj.getString("officeid");
                                adsModels.add(new AdsModel("http://www.khadamte.com/" + ads_img, office_id, office_name));
                            }
                        }
                        Intent mainIntent = new Intent(SplashActivity.this, MainAdsActivity.class);
                        mainIntent.putExtra("ads_list", adsModels);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SplashActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SplashActivity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(SplashActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

