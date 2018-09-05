package com.khdamte.bitcode.khdamte_app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.activities.MyOfficeProfileActivity;
import com.khdamte.bitcode.khdamte_app.adapter.Flags_Adapter;
import com.khdamte.bitcode.khdamte_app.adapter.SpinnerAdapter;
import com.khdamte.bitcode.khdamte_app.models.Flags_Model;
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

import static android.content.Context.MODE_PRIVATE;
import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class EditOfficeFragment extends Fragment {

    private EditText company_name_et, email_editText, phone_number1, phone_number2, phone_number3,
            address, desc_editText;
    private Button update_btn;
    private Spinner nationality_spinner, country_spinner, city_spinner, otherServices_spinner;
    private Typeface lightFace;
    private RecyclerView flag_RecyclerView;
    private Flags_Adapter myAdapter;
    private String officeId, selected_national;
    private ArrayList<Flags_Model> nationality_list;
    private HashMap<String, String> counries_hashMap;
    private HashMap<String, String> cities_hashMap;
    private AlertDialog progressDialog;
    private HashMap<String, ArrayList<Flags_Model>> nat_name_hashMap;
    private HashMap<String, ArrayList<Flags_Model>> nat_id_hashMap;
    private ArrayList<Flags_Model> flags_models;
    private Set<String> nat_selected_hashSet;
    private TextView other_services_tv ;
    private String postOtherSer ;
    private String langToLoad;
    private SharedPreferences languagepref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_office_layout, container, false);

        lightFace = MainActivity.lightFace;

        MyOfficeProfileActivity.setTitle(getResources().getString(R.string.update_office));
        languagepref = getActivity().getSharedPreferences("language", MODE_PRIVATE);
        langToLoad = languagepref.getString("languageToLoad", null);
        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);

        other_services_tv = (TextView) rootView.findViewById(R.id.other_services_tv);
        company_name_et = (EditText) rootView.findViewById(R.id.company_name_et);
        email_editText = (EditText) rootView.findViewById(R.id.email_editText);
        desc_editText = (EditText) rootView.findViewById(R.id.desc_editText);

        country_spinner = (Spinner) rootView.findViewById(R.id.country_spinner);
        city_spinner = (Spinner) rootView.findViewById(R.id.city_spinner);
        otherServices_spinner = (Spinner) rootView.findViewById(R.id.otherServices_spinner);

        address = (EditText) rootView.findViewById(R.id.address_editText);
        phone_number1 = (EditText) rootView.findViewById(R.id.phone1_editText);
        phone_number2 = (EditText) rootView.findViewById(R.id.phone2_editText);
        phone_number3 = (EditText) rootView.findViewById(R.id.phone3_editText);
        nationality_spinner = (Spinner) rootView.findViewById(R.id.nationality_spinner);

        update_btn = (Button) rootView.findViewById(R.id.update_btn);
        flag_RecyclerView = (RecyclerView) rootView.findViewById(R.id.flags_listview);

        company_name_et.setTypeface(lightFace);
        email_editText.setTypeface(lightFace);
        address.setTypeface(lightFace);
        phone_number1.setTypeface(lightFace);
        phone_number2.setTypeface(lightFace);
        phone_number3.setTypeface(lightFace);
        desc_editText.setTypeface(lightFace);
        update_btn.setTypeface(lightFace);
        other_services_tv.setTypeface(lightFace);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String office_name = company_name_et.getText().toString();
                String office_mob1 = phone_number1.getText().toString();
                String office_mob2 = phone_number2.getText().toString();
                String office_mob3 = phone_number3.getText().toString();

                int city_position = city_spinner.getSelectedItemPosition();
                int country_position = country_spinner.getSelectedItemPosition();
                String city_id = "", country_id = "" ;
                if((city_position > 0) && (country_position > 0)){
                    city_id = cities_hashMap.get(city_spinner.getSelectedItem().toString());
                    country_id = counries_hashMap.get(country_spinner.getSelectedItem().toString());
                }
                String office_address = address.getText().toString();
                String office_desc = desc_editText.getText().toString();
                String office_email = email_editText.getText().toString();
                StringBuffer nationBuffer = new StringBuffer();
                String maid_nationalities = "" ;
                if(nat_selected_hashSet.size() != 0){
                    for (String ids : nat_selected_hashSet) {
                        nationBuffer.append(ids);
                        nationBuffer.append(",");
                    }
                    maid_nationalities = String.valueOf(nationBuffer);
                    maid_nationalities = maid_nationalities.substring(0, maid_nationalities.length() - 1);
                }

                if (!office_name.equals("") && !office_mob1.equals("") && !office_mob2.equals("") && !office_mob3.equals("") && !city_id.equals("")
                        && !country_id.equals("") && !office_address.equals("") && !office_desc.equals("") && !office_email.equals("") && !maid_nationalities.equals("")
                        || postOtherSer.equals("")) {
                    if (isNetworkAvailable()) {
                        SharedPreferences prefs = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
                        final String user_id = prefs.getString("id", "");
                        updateOffice(user_id, office_name, office_mob1, office_mob2, office_mob3, city_id,
                                office_address, office_desc, office_email, country_id, maid_nationalities, officeId, postOtherSer);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_type_alldata), Toast.LENGTH_LONG).show();
                }
            }
        });

        SharedPreferences prefs = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        final String user_id = prefs.getString("id", null);

        if (isNetworkAvailable()) {
            GetAllCountries();
            GetAllNationalities();
            GetOtherServices();
            if (user_id != null) {
                GetMyOfficeByUserID(user_id);
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
        }


        ArrayList<String> nation_arrayList = new ArrayList<String>();
        nation_arrayList.add(getResources().getString(R.string.nationality_maids));

        final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, nation_arrayList);
        nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        nationality_spinner.setAdapter(nation_ArrayAdapter);

        return rootView;
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
                                String [] country = country_name.split(",");
                                String ar = country[1];
                                String eng = country[0];
                                if (langToLoad.equals("العربية")) {
                                    counries_hashMap.put(ar.trim(), country_id);
                                }else{
                                    counries_hashMap.put(eng.trim(), country_id);
                                }
                            }
                            ArrayList<String> countries_arrayList = new ArrayList<String>();
                            countries_arrayList.add(getResources().getString(R.string.choose_country));
                            for (String keys : counries_hashMap.keySet()) {
                                countries_arrayList.add(keys);
                            }
                            final ArrayAdapter<String> country_spinnerArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, countries_arrayList);
                            country_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            country_spinner.setAdapter(country_spinnerArrayAdapter);
                            country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_countries), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
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
                        if (adsJsonArray.length() != 0) {
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
                            city_spinner.setAdapter(city_spinnerArrayAdapter);
                            city_spinnerArrayAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_city), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

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
                                String nat_img = adsObj.getString("pic");
                                nat_img = "http://www.khdamte.co" + nat_img;
                                flags_models = new ArrayList<>();
                                flags_models.add(new Flags_Model(nat_name, nat_id, nat_img));
                                flags_arrayList.add(nat_name);
                                nat_name_hashMap.put(nat_name, flags_models);
                                nat_id_hashMap.put(nat_id, flags_models);
                            }
                            nationality_list = new ArrayList<Flags_Model>();
                            nat_selected_hashSet = new HashSet<String>();
                            final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, flags_arrayList);
                            nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            nationality_spinner.setAdapter(nation_ArrayAdapter);
                            nationality_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                        myAdapter = new Flags_Adapter(getActivity(), nationality_list);
                                        flag_RecyclerView.setAdapter(myAdapter);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_nationality), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void GetMyOfficeByUserID(String user_id) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getOfficeByUser(user_id);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        String res = mainJsonObj.getString("Response");
                        if (res.contains("The Office Appended until admin approve")) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_append_office), Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray adsJsonArray = mainJsonObj.getJSONArray("Response");
                            for (int i = 0; i < adsJsonArray.length(); i++) {
                                JSONObject offObj = adsJsonArray.getJSONObject(i);
                                String office_id = offObj.getString("id");
                                GetMyOfficeByOfficeId(office_id);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void GetMyOfficeByOfficeId(String office_id) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getOffice(office_id);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        if (result.contains("The Office Appended until admin approve")) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_append_office), Toast.LENGTH_LONG).show();
                        } else {

                            JSONObject offObj = mainJsonObj.getJSONObject("Response");
                            String office_id = offObj.getString("id");
                            String office_name = offObj.getString("name");
                            String office_description = offObj.getString("description");
                            String office_mob1 = offObj.getString("mob1");
                            String office_mob2 = offObj.getString("mob2");
                            String office_mob3 = offObj.getString("mob3");
                            String office_address = offObj.getString("address");
                            String office_email = offObj.getString("email");

                            company_name_et.setText(office_name);
                            desc_editText.setText(office_description);
                            phone_number1.setText(office_mob1);
                            phone_number2.setText(office_mob2);
                            phone_number3.setText(office_mob3);
                            address.setText(office_address);
                            email_editText.setText(office_email);
                            officeId = office_id;

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
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
                                nameOfServicesMap.put(ser_name, ser_id);
                                nameOfOthSerArray.add(ser_name);
                            }
                            final ArrayAdapter<String> nation_ArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, nameOfOthSerArray);
                            nation_ArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            otherServices_spinner.setAdapter(nation_ArrayAdapter);

                            final Set<String> other_services_Set = new HashSet<String>();

                            otherServices_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                            other_services_tv.setText(otherSerName.substring(0, otherSerName.length()-2));
                                        }
                                        postOtherSer = String.valueOf(otherSerId.substring(0, otherSerId.length()-1));
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_nationality), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    private void updateOffice(String user_id, String name, String mob1, String mob2, String mob3, String city_id,
                              String address, String desc, String email, String country_id, String nationalities, String office_id, String otherServices) {
        showDialog();

        JsonObject officeObj = new JsonObject();
        officeObj.addProperty("userid", user_id);
        officeObj.addProperty("name", name);
        officeObj.addProperty("mob1", mob1);
        officeObj.addProperty("mob2", mob2);
        officeObj.addProperty("mob3", mob3);
        officeObj.addProperty("cityid", city_id);
        officeObj.addProperty("address", address);
        officeObj.addProperty("description", desc);
        officeObj.addProperty("email", email);
        officeObj.addProperty("countryid", country_id);
        officeObj.addProperty("nationalities", nationalities);
        officeObj.addProperty("Id", office_id);
        officeObj.addProperty("OtherServices", otherServices);

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.updateOffice(officeObj);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissDialog();
                String result = "";
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject adsObj = new JSONObject(result);
                        boolean success = adsObj.getBoolean("Success");
                        if (success) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_off_upd), Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_off_not_upd), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_res_msg), Toast.LENGTH_LONG).show();
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