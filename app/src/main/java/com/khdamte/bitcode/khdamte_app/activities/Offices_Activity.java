package com.khdamte.bitcode.khdamte_app.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.SpinnerAdapter;
import com.khdamte.bitcode.khdamte_app.fragments.IndividualMaidsFragment;
import com.khdamte.bitcode.khdamte_app.fragments.Office_Fragment;
import com.khdamte.bitcode.khdamte_app.fragments.ServicesFragment;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.khdamte.bitcode.khdamte_app.activities.MainActivity.lightFace;
import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class Offices_Activity extends AppCompatActivity implements View.OnClickListener {

    private Button offices_btn, services_btn, maids_btn;
    private String city_id = "";
    private FragmentManager fragmentManager;
    private ImageView backImgView;
    public static TextView mainTitle;
    private AlertDialog progressDialog;
    private Spinner nation_sp;
    private String selected_national = "";
    private FloatingActionButton search_fab;
    private HashMap<String, String> nat_name_hashMap;
    private ImageView filter_btn;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offices_layout);

        MobileAds.initialize(this, getResources().getString(R.string.app_ad_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));
        AdRequest request = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(request);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                showInterstitial();
            }

            @Override
            public void onAdClosed() {

            }
        });

        progressDialog = new SpotsDialog(this, R.style.Custom);
        offices_btn = (Button) findViewById(R.id.offices_btn);
        services_btn = (Button) findViewById(R.id.services_btn);
        maids_btn  = (Button) findViewById(R.id.maids_btn);
        search_fab = (FloatingActionButton) findViewById(R.id.search_fab);
        filter_btn = (ImageView) findViewById(R.id.filter_btn);

        backImgView = (ImageView) findViewById(R.id.back_btn);
        mainTitle = (TextView) findViewById(R.id.title_toolbar);

        Helper.setSrc4BackImg(backImgView, Locale.getDefault().getDisplayLanguage());

        offices_btn.setOnClickListener(this);
        services_btn.setOnClickListener(this);
        maids_btn.setOnClickListener(this);
        backImgView.setOnClickListener(this);
        search_fab.setOnClickListener(this);
        filter_btn.setOnClickListener(this);


        offices_btn.setTypeface(lightFace);
        services_btn.setTypeface(lightFace);
        mainTitle.setTypeface(lightFace);

        changeTitle(getResources().getString(R.string.maid_office));

        city_id = getIntent().getExtras().getString("city_id");

        maidsOfficeBtn("no", "no", false, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.services_btn:
                ServiceOfficeBtn("no", false);
                break;

            case R.id.offices_btn:
                maidsOfficeBtn("no", "no", false, false);
                break;

            case R.id.maids_btn:
                onMaidsBtnClick("no", "no", false, false);
                break;

            case R.id.back_btn:
                onBackPressed();
                break;

            case R.id.search_fab:
                showSearchDilaog();
                break;

            case R.id.filter_btn:
                showFilterDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static void changeTitle(String title) {
        mainTitle.setText(title);
    }

    private void dismissDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showDialog() {
        progressDialog.show();
    }

    private void GetAllNationalities() {
        showDialog();
        nat_name_hashMap = new HashMap<String, String>();
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
                                flags_arrayList.add(nat_name);
                                nat_name_hashMap.put(nat_name, nat_id);
                            }
                            final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(Offices_Activity.this, R.layout.spinner_item, flags_arrayList);
                            nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            nation_sp.setAdapter(nation_ArrayAdapter);
                            nation_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (position > 0) {
                                        selected_national = parent.getItemAtPosition(position).toString();
                                    }else{
                                        selected_national= "" ;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            Toast.makeText(Offices_Activity.this, getResources().getString(R.string.toast_no_nationality), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Offices_Activity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Offices_Activity.this, getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(Offices_Activity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void showSearchDilaog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.search_activity_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (isNetworkAvailable()) {
            GetAllNationalities();
        } else {
            Toast.makeText(Offices_Activity.this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
        }
        final TextView or_tv = (TextView) dialog.findViewById(R.id.or_tv);
        final TextView toolbar_title = (TextView) dialog.findViewById(R.id.toolbar_title);
        final EditText office_name_et = (EditText) dialog.findViewById(R.id.office_name_et);

        nation_sp = (Spinner) dialog.findViewById(R.id.nation_sp);
        ArrayList<String> nation_arrayList = new ArrayList<String>();
        nation_arrayList.add(getResources().getString(R.string.nationality_maids));

        final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item, nation_arrayList);
        nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        nation_sp.setAdapter(nation_ArrayAdapter);

        final Button cancel_btn = (Button) dialog.findViewById(R.id.cancel_btn);
        final Button search_btn = (Button) dialog.findViewById(R.id.search_btn);

        toolbar_title.setTypeface(lightFace);
        office_name_et.setTypeface(lightFace);
        search_btn.setTypeface(lightFace);
        cancel_btn.setTypeface(lightFace);
        or_tv.setTypeface(lightFace);

        final Fragment f = getSupportFragmentManager().findFragmentById(R.id.offices_frameLayout);
        if (f instanceof ServicesFragment) {
            or_tv.setVisibility(View.GONE);
            nation_sp.setVisibility(View.GONE);
        } else if (f instanceof Office_Fragment) {
            or_tv.setVisibility(View.VISIBLE);
            nation_sp.setVisibility(View.VISIBLE);
        }

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String offic_name = office_name_et.getText().toString();
                String nation_id = "";
                if (!selected_national.equals("")) {
                    nation_id = nat_name_hashMap.get(selected_national);
                    selected_national = "" ;
                }

                if (offic_name.equals("") && nation_id.equals("")) {
                    Toast.makeText(Offices_Activity.this, getString(R.string.toast_type_office_name_or_nation_id), Toast.LENGTH_SHORT).show();
                } else {
                    if (f instanceof ServicesFragment) {
                        ServiceOfficeBtn(offic_name, false);
                        dialog.cancel();
                    } else if (f instanceof Office_Fragment) {
                        if(!offic_name.equals("") && nation_id.equals("")){
                            maidsOfficeBtn(offic_name, "no", false, false);
                            dialog.cancel();
                        }else if(offic_name.equals("") && !nation_id.equals("")){
                            maidsOfficeBtn("no", nation_id, false, false);
                            dialog.cancel();
                        }else{
                            Toast.makeText(Offices_Activity.this, getString(R.string.willSearchByOfficeName), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        dialog.show();
    }

    private void showFilterDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.filter_dilaog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView or_tv = (TextView) dialog.findViewById(R.id.or_tv);
        final TextView toolbar_title = (TextView) dialog.findViewById(R.id.toolbar_title);
        final Button alphab_btn = (Button) dialog.findViewById(R.id.alphab_btn);
        final Button review_btn = (Button) dialog.findViewById(R.id.review_btn);

        toolbar_title.setTypeface(lightFace);
        review_btn.setTypeface(lightFace);
        alphab_btn.setTypeface(lightFace);
        or_tv.setTypeface(lightFace);

        final Fragment f = getSupportFragmentManager().findFragmentById(R.id.offices_frameLayout);
        if (f instanceof ServicesFragment) {
            or_tv.setVisibility(View.GONE);
            review_btn.setVisibility(View.GONE);
        } else if (f instanceof Office_Fragment) {
            or_tv.setVisibility(View.VISIBLE);
            review_btn.setVisibility(View.VISIBLE);
        }

        alphab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alphab_btn.setBackgroundColor(Color.parseColor("#ff9900"));
                alphab_btn.setTextColor(Color.parseColor("#660033"));

                review_btn.setBackgroundColor(Color.parseColor("#ffffff"));
                review_btn.setTextColor(Color.parseColor("#cc0000"));

                if (f instanceof ServicesFragment) {
                    ServiceOfficeBtn("no", true);
                } else if (f instanceof Office_Fragment) {
                    maidsOfficeBtn("no", "no", false, true);
                }
                dialog.cancel();
            }
        });

        review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alphab_btn.setBackgroundColor(Color.parseColor("#ffffff"));
                alphab_btn.setTextColor(Color.parseColor("#cc0000"));

                review_btn.setBackgroundColor(Color.parseColor("#ff9900"));
                review_btn.setTextColor(Color.parseColor("#660033"));

                if (f instanceof ServicesFragment) {
                    Toast.makeText(Offices_Activity.this, getString(R.string.toast_noReview_4Services_offices), Toast.LENGTH_SHORT).show();
                } else if (f instanceof Office_Fragment) {
                    maidsOfficeBtn("no", "no", true, false);
                }

                dialog.cancel();
            }
        });
        dialog.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ServiceOfficeBtn(String officeName, boolean sort_alpha) {
        services_btn.setBackgroundColor(Color.parseColor("#ffffff"));
        services_btn.setTextColor(Color.parseColor("#000000"));

        offices_btn.setBackgroundColor(Color.parseColor("#000000"));
        offices_btn.setTextColor(Color.parseColor("#ffffff"));

        maids_btn.setBackgroundColor(Color.parseColor("#000000"));
        maids_btn.setTextColor(Color.parseColor("#ffffff"));

        fragmentManager = getSupportFragmentManager();
        Fragment fragment = new ServicesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("city_id", city_id);
        bundle.putString("office_name", officeName);
        bundle.putBoolean("sort_alpha", sort_alpha);
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.offices_frameLayout, fragment).addToBackStack(null).commit();
    }

    private void maidsOfficeBtn(String officeName, String maidsNation, boolean sort_review, boolean sort_alpha) {
        offices_btn.setBackgroundColor(Color.parseColor("#ffffff"));
        offices_btn.setTextColor(Color.parseColor("#000000"));

        services_btn.setBackgroundColor(Color.parseColor("#000000"));
        services_btn.setTextColor(Color.parseColor("#ffffff"));

        maids_btn.setBackgroundColor(Color.parseColor("#000000"));
        maids_btn.setTextColor(Color.parseColor("#ffffff"));

        fragmentManager = getSupportFragmentManager();
        Fragment ser_fragment = new Office_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("city_id", city_id);
        bundle.putString("office_name", officeName);
        bundle.putString("maids_nation", maidsNation);
        bundle.putBoolean("sort_review", sort_review);
        bundle.putBoolean("sort_alpha", sort_alpha);
        ser_fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.offices_frameLayout, ser_fragment).addToBackStack(null).commit();
    }

    private void onMaidsBtnClick(String maidsName, String maidsNation, boolean sort_review, boolean sort_alpha) {
        maids_btn.setBackgroundColor(Color.parseColor("#ffffff"));
        maids_btn.setTextColor(Color.parseColor("#000000"));

        services_btn.setBackgroundColor(Color.parseColor("#000000"));
        services_btn.setTextColor(Color.parseColor("#ffffff"));

        offices_btn.setBackgroundColor(Color.parseColor("#000000"));
        offices_btn.setTextColor(Color.parseColor("#ffffff"));

        fragmentManager = getSupportFragmentManager();
        Fragment ser_fragment = new IndividualMaidsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("city_id", city_id);
        bundle.putString("maidsName", maidsName);
        bundle.putString("maids_nation", maidsNation);
        bundle.putBoolean("sort_review", sort_review);
        bundle.putBoolean("sort_alpha", sort_alpha);
        ser_fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.offices_frameLayout, ser_fragment).addToBackStack(null).commit();
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}
