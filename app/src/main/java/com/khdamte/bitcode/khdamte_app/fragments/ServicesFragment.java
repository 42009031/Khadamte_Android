package com.khdamte.bitcode.khdamte_app.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.activities.Offices_Activity;
import com.khdamte.bitcode.khdamte_app.adapter.ServicesAdapter;
import com.khdamte.bitcode.khdamte_app.models.ServicesModel;
import com.khdamte.bitcode.khdamte_app.ui.RecyclerItemClickListener;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

/**
 * Created by Amado on 7/23/2017.
 */

public class ServicesFragment extends Fragment {

    private RecyclerView services_rv;
    private ArrayList<ServicesModel> serv_models;
    private String city_id, office_name;
    private AlertDialog progressDialog;
    private int newPosition;
    private String phn1, phn2, selected_phone;
    private ServicesAdapter adapter ;
private boolean sort_alpha ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_services_layout, container, false);

        Offices_Activity.changeTitle(getResources().getString(R.string.ser_office));

        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);
        serv_models = new ArrayList<ServicesModel>();
        services_rv = (RecyclerView) rootView.findViewById(R.id.services_rv);
        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == 1) {
                    return 1;
                } else if (position == 2) {
                    return 2;
                } else if (position == 3 || position == 4) {
                    return 1;
                } else if (((position % 5) == 0)) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        services_rv.setLayoutManager(glm);
        ServicesAdapter adapter = new ServicesAdapter(getActivity(), serv_models);
        services_rv.setAdapter(adapter);

        city_id = getArguments().getString("city_id");
        office_name = getArguments().getString("office_name");
        sort_alpha = getArguments().getBoolean("sort_alpha");

        if (isNetworkAvailable()) {
            if(office_name.equals("no") && !sort_alpha){
                GetAllServices(city_id, false);
            }else{
                if(sort_alpha){
                    GetAllServices(city_id, true);
                }
                if(!office_name.equals("no")){
                    GetServOfficeByName(office_name);
                }
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_error_connection), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void GetAllServices(String city_id, final boolean sort_alpha) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getAllServicesInCity((city_id));
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainJsonObj = new JSONObject(result);
                        JSONArray offices_JsonArray = mainJsonObj.getJSONArray("Response");
                        if (offices_JsonArray.length() != 0) {
                            for (int i = 0; i < offices_JsonArray.length(); i++) {
                                JSONObject office_Obj = offices_JsonArray.getJSONObject(i);

                                String serv_id = office_Obj.getString("id");
                                String serv_image = office_Obj.getString("image");
                                if (!serv_image.equals("null")) {
                                    serv_image = "http://www.khdamte.co" + serv_image;
                                } else {
                                    int default_logos[] = {R.drawable.logo1, R.drawable.logo2, R.drawable.logo3, R.drawable.logo4, R.drawable.logo5};
                                    serv_image = "" + default_logos[randInt(0, default_logos.length - 1)];
                                }
                                String serv_name = office_Obj.getString("name");
                                String serv_phone2 = office_Obj.getString("phone2");
                                String serv_phone1 = office_Obj.getString("phone1");

                                serv_models.add(new ServicesModel(serv_id, serv_name, serv_image, serv_phone1, serv_phone2));
                            }

                            ServicesModel servicesModel = new ServicesModel();
                            for (int i = 0; i < serv_models.size(); i++) {
                                if (i == 0 || i == 1 || i == 3 || i == 4) {
                                } else if (i == 2) {
                                    serv_models.add(2, servicesModel);
                                } else if (((i % 5) == 0)) {
                                    serv_models.add(i, servicesModel);
                                }
                            }


                            if (sort_alpha) {
                                ArrayList<ServicesModel> sortOffice_models = new ArrayList<ServicesModel>();
                                for (int i = 0; i < serv_models.size(); i++) {
                                    String serv_name = serv_models.get(i).getServ_name();
                                    String serv_id = serv_models.get(i).getServ_id();
                                    String serv_image = serv_models.get(i).getServ_img();
                                    String serv_phone1 = serv_models.get(i).getServ_phonr1();
                                    String serv_phone2 = serv_models.get(i).getServ_phone2();
                                    if (serv_name != null) {
                                        sortOffice_models.add(new ServicesModel(serv_id, serv_name, serv_image, serv_phone1, serv_phone2));
                                    }
                                }
                                Collections.sort(sortOffice_models, new Comparator<ServicesModel>() {
                                    public int compare(ServicesModel obj1, ServicesModel obj2) {
                                        String office_name1 = obj1.getServ_name().toString();
                                        String office_name2 = obj2.getServ_name().toString();
                                        return office_name1.compareTo(office_name2);
                                    }
                                });
                                ServicesModel office_model = new ServicesModel();
                                for (int i = 0; i < sortOffice_models.size(); i++) {
                                    if (i == 0 || i == 1 || i == 3 || i == 4) {
                                    } else if (i == 2) {
                                        sortOffice_models.add(2, office_model);
                                    } else if (((i % 5) == 0)) {
                                        sortOffice_models.add(i, office_model);
                                    }
                                }
                                adapter = new ServicesAdapter(getActivity(), sortOffice_models);
                                services_rv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter = new ServicesAdapter(getActivity(), serv_models);
                                services_rv.setAdapter(adapter);
                            }
                            services_rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), services_rv, new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            setPhn1(serv_models.get(position).getServ_phonr1());
                                            setPhn2(serv_models.get(position).getServ_phone2());
                                            if (position == 0 || position == 1) {
                                                showCallDialog(getPhn1(), getPhn2());
                                            } else if (position == 2) {
                                            } else if (position == 3 || position == 4) {
                                                showCallDialog(getPhn1(), getPhn2());
                                            } else if (((position % 5) == 0)) {
                                            } else {
                                                showCallDialog(getPhn1(), getPhn2());
                                            }

                                        }

                                        @Override
                                        public void onLongItemClick(View view, int position) {
                                        }
                                    })
                            );
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_off_incity), Toast.LENGTH_LONG).show();
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

    private void GetServOfficeByName(String office_name) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getServOfficeByName(city_id, office_name.trim());
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                try {
                    result = response.body().toString();
                    if (!result.equals("")) {
                        try {
                            JSONObject mainObj = new JSONObject(result);
                            JSONArray offices_JsonArray = mainObj.getJSONArray("Response");
                            if (offices_JsonArray.length() != 0) {
                                for (int i = 0; i < offices_JsonArray.length(); i++) {
                                    JSONObject office_Obj = offices_JsonArray.getJSONObject(i);

                                    String serv_id = office_Obj.getString("id");
                                    String serv_image = office_Obj.getString("image");
                                    if (!serv_image.equals("null")) {
                                        serv_image = "http://www.khdamte.co" + serv_image;
                                    } else {
                                        int default_logos[] = {R.drawable.logo1, R.drawable.logo2, R.drawable.logo3, R.drawable.logo4, R.drawable.logo5};
                                        serv_image = "" + default_logos[randInt(0, default_logos.length - 1)];
                                    }
                                    String serv_name = office_Obj.getString("name");
                                    String serv_phone2 = office_Obj.getString("phone2");
                                    String serv_phone1 = office_Obj.getString("phone1");

                                    serv_models.add(new ServicesModel(serv_id, serv_name, serv_image, serv_phone1, serv_phone2));
                                }

                                ServicesModel servicesModel = new ServicesModel();
                                for (int i = 0; i < serv_models.size(); i++) {
                                    if (i == 0 || i == 1 || i == 3 || i == 4) {
                                    } else if (i == 2) {
                                        serv_models.add(2, servicesModel);
                                    } else if (((i % 5) == 0)) {
                                        serv_models.add(i, servicesModel);
                                    }
                                }
                                ServicesAdapter adapter = new ServicesAdapter(getActivity(), serv_models);
                                services_rv.setAdapter(adapter);
                                services_rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), services_rv, new RecyclerItemClickListener.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                setPhn1(serv_models.get(position).getServ_phonr1());
                                                setPhn2(serv_models.get(position).getServ_phone2());
                                                if (position == 0 || position == 1) {
                                                    showCallDialog(getPhn1(), getPhn2());
                                                } else if (position == 2) {
                                                } else if (position == 3 || position == 4) {
                                                    showCallDialog(getPhn1(), getPhn2());
                                                } else if (((position % 5) == 0)) {
                                                } else {
                                                    showCallDialog(getPhn1(), getPhn2());
                                                }

                                            }

                                            @Override
                                            public void onLongItemClick(View view, int position) {
                                            }
                                        })
                                );
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_off_incity), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), getString(R.string.toast_no_office), Toast.LENGTH_SHORT).show();
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

    private void dismissDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showDialog() {
        progressDialog.show();
    }

    public int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onCall(getSelected_phone());
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_call_permission), Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    public void onCall(String phone_number) {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 123);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + phone_number)));
        }
    }

    private void showCallDialog(final String phone1, final String phone2) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.service_call_dialog_lyout);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        TextView header_msg = (TextView) dialog.findViewById(R.id.header_msg);
        TextView or_tv = (TextView) dialog.findViewById(R.id.or_tv);
        TextView body_msg = (TextView) dialog.findViewById(R.id.body_msg);

        TextView phone2_btn = (Button) dialog.findViewById(R.id.phone2_btn);
        TextView phone1_btn = (Button) dialog.findViewById(R.id.phone1_btn);

        phone1_btn.setText(phone1);
        phone2_btn.setText(phone2);

        header_msg.setTypeface(MainActivity.lightFace);
        body_msg.setTypeface(MainActivity.lightFace);
        or_tv.setTypeface(MainActivity.lightFace);
        phone1_btn.setTypeface(MainActivity.lightFace);
        phone2_btn.setTypeface(MainActivity.lightFace);

        phone1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected_phone(phone1);
                onCall(phone1);
                dialog.dismiss();
            }
        });

        phone2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected_phone(phone2);
                onCall(phone2);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public String getPhn1() {
        return phn1;
    }

    public void setPhn1(String phn1) {
        this.phn1 = phn1;
    }

    public String getPhn2() {
        return phn2;
    }

    public void setPhn2(String phn2) {
        this.phn2 = phn2;
    }

    public String getSelected_phone() {
        return selected_phone;
    }

    public void setSelected_phone(String selected_phone) {
        this.selected_phone = selected_phone;
    }
}
