package com.khdamte.bitcode.khdamte_app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.activities.Offices_Activity;
import com.khdamte.bitcode.khdamte_app.adapter.SpinnerAdapter;
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

import static android.content.Context.MODE_PRIVATE;
import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;


public class OfficePlacesFragment extends Fragment implements View.OnClickListener {

    //    private Button kuwait_button, hawalli_button, ahmadi_button, farwaniyah_button, jahra_button;

    private ImageView lang_btn;
    private Spinner countr_sp, city_sp;
    private Button show_btn;
    private SharedPreferences languagepref;
    private SharedPreferences.Editor editor;
    private String langToLoad;
    private HashMap<String, String> counries_hashMap;
    private HashMap<String, String> cities_hashMap;
    private AlertDialog progressDialog;
    private String city, country;

    private Animation animation ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.region_layout, container, false);

        lang_btn = (ImageView) rootView.findViewById(R.id.lang_btn);
        countr_sp = (Spinner) rootView.findViewById(R.id.countr_sp);
        city_sp = (Spinner) rootView.findViewById(R.id.city_sp);
        show_btn = (Button) rootView.findViewById(R.id.show_btn);
        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);

        show_btn.setOnClickListener(this);
        lang_btn.setOnClickListener(this);
        show_btn.setTypeface(MainActivity.lightFace);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);


        String language = Locale.getDefault().getDisplayLanguage();
        language = language.toLowerCase();

        languagepref = getActivity().getSharedPreferences("language", MODE_PRIVATE);
        editor = languagepref.edit();
        editor.putString("languageToLoad", language);
        editor.apply();

        ArrayList<String> countries_arrayList = new ArrayList<String>();
        countries_arrayList.add(getResources().getString(R.string.choose_country));
        final ArrayAdapter<String> country_spinnerArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, countries_arrayList);
        country_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        countr_sp.setAdapter(country_spinnerArrayAdapter);

        ArrayList<String> cities_arrayList = new ArrayList<String>();
        cities_arrayList.add(getResources().getString(R.string.choose_city));
        final ArrayAdapter<String> city_spinnerArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, cities_arrayList);
        city_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        city_sp.setAdapter(city_spinnerArrayAdapter);

        langToLoad = languagepref.getString("languageToLoad", null);
        if (langToLoad.contains("العربية")) {
            lang_btn.setImageResource(R.drawable.eng_btn);
        } else {
            lang_btn.setImageResource(R.drawable.ar);
        }

        if (isNetworkAvailable()) {
            GetAllCountries();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.lang_btn:
                lang_btn.startAnimation(animation);
                if (langToLoad.equals("العربية")) {
                    String languageToLoad = "eng"; // your language
                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());

                    startActivity(new Intent(getActivity(), MainActivity.class));
                    editor.putString("languageToLoad", "العربية");
                    editor.apply();
                    lang_btn.setImageResource(R.drawable.ar);
                    getActivity().finish();
                } else {
                    String languageToLoad = "ar"; // your language
                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    editor.putString("languageToLoad", "english");
                    editor.apply();
                    lang_btn.setImageResource(R.drawable.eng_btn);
                    getActivity().finish();
                }

                break;

            case R.id.show_btn:
                show_btn.startAnimation(animation);
                int city_position = city_sp.getSelectedItemPosition();
                int country_position = countr_sp.getSelectedItemPosition();
                if ((city_position > 0) && (country_position > 0)) {
                    city = city_sp.getSelectedItem().toString();
                    country = countr_sp.getSelectedItem().toString();
                    if (!city.equals("") && !country.equals("")) {
                        startActivity(new Intent(getActivity(), Offices_Activity.class).putExtra("city_id", cities_hashMap.get(city_sp.getSelectedItem().toString())));
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_empty_coun_city), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_empty_coun_city), Toast.LENGTH_LONG).show();
                }
                break;
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

    private void GetAllCountries() {

        showDialog();
        counries_hashMap = new HashMap<String, String>();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getCountries();
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                if(!response.body().isJsonNull()){
                    result = response.body().toString();
                    if (!result.equals("")) {
                        try {
                            JSONObject mainJsonObj = new JSONObject(result);
                            JSONArray adsJsonArray = mainJsonObj.getJSONArray("Response");
                            for (int i = 0; i < adsJsonArray.length(); i++) {
                                JSONObject adsObj = adsJsonArray.getJSONObject(i);
                                String country_id = adsObj.getString("id");
                                String country_name = adsObj.getString("name");
                                if(country_name.contains(",")){
                                    String [] country = country_name.split(",");
                                    String ar = country[1];
                                    String eng = country[0];
                                    if (langToLoad.equals("العربية")) {
                                        counries_hashMap.put(ar.trim(), country_id);
                                    }else{
                                        counries_hashMap.put(eng.trim(), country_id);
                                    }
                                }else {
                                    counries_hashMap.put(country_name.trim(), country_id);
                                }
                            }
                            ArrayList<String> countries_arrayList = new ArrayList<String>();
                            countries_arrayList.add(getResources().getString(R.string.choose_country));
                            countries_arrayList.addAll(counries_hashMap.keySet());
                            final ArrayAdapter<String> country_spinnerArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, countries_arrayList);
                            country_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            countr_sp.setAdapter(country_spinnerArrayAdapter);
                            countr_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedItemText = (String) parent.getItemAtPosition(position);
                                    if (position > 0) {
                                        GetAllCities(counries_hashMap.get(selectedItemText));
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            FirebaseCrash.report(e);
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
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
                            String [] city = city_name.split(",");
                            String ar = city[1];
                            String eng = city[0];
                            if (langToLoad.equals("العربية")) {
                                cities_hashMap.put(ar.trim(), city_id);
                            }else{
                                cities_hashMap.put(eng.trim(), city_id);
                            }
                        }
                        ArrayList<String> cities_arrayList = new ArrayList<String>();
                        cities_arrayList.add(getResources().getString(R.string.choose_city));
                        for (String keys : cities_hashMap.keySet()) {
                            cities_arrayList.add(keys);
                        }
                        final ArrayAdapter<String> city_spinnerArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, cities_arrayList);
                        city_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                        city_sp.setAdapter(city_spinnerArrayAdapter);
                        city_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } catch (JSONException e) {
                        FirebaseCrash.report(e);
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
