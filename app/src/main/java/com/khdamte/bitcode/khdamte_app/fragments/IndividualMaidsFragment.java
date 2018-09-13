package com.khdamte.bitcode.khdamte_app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.DetailsActivity;
import com.khdamte.bitcode.khdamte_app.activities.IndividualMaidsDetailsActivity;
import com.khdamte.bitcode.khdamte_app.adapter.IndividualMaidsAdapter;
import com.khdamte.bitcode.khdamte_app.models.IndividualMaidsModel;
import com.khdamte.bitcode.khdamte_app.ui.RecyclerItemClickListener;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class IndividualMaidsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView maids_lv;
    private IndividualMaidsAdapter adapter;
    private ArrayList<IndividualMaidsModel> maidsList;
    private AlertDialog progressDialog;
    private SwipeRefreshLayout swipeRefresh;
    private String city_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maids_layout, container, false);

        city_id = getArguments().getString("city_id");

        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);
        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        maids_lv = (RecyclerView) rootView.findViewById(R.id.maids_lv);
        GridLayoutManager glm = new GridLayoutManager(getContext(), 1);

        maids_lv.setLayoutManager(glm);

        maidsList = new ArrayList<>();
        adapter = new IndividualMaidsAdapter(getActivity(), maidsList);
        maids_lv.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(this);


        loadData();

        return rootView;
    }


    @Override
    public void onRefresh() {
        Log.d("MainActivity_", "onRefresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);
                loadData();
            }
        }, 2000);
    }

    private void loadData() {
        if (isNetworkAvailable()) {
            maidsList.clear();
            GetAllMaids(city_id,false);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
        }
    }


    private void GetAllMaids(String city_id, final boolean sort_alpha) {
        showDialog();
        maidsList = new ArrayList<>();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.GetAllIndividualsMaid((city_id));
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
                                String office_id = office_Obj.getString("id");
                                String office_image = office_Obj.getString("image");
                                String office_name = office_Obj.getString("name");
                                String office_desc = office_Obj.getString("descrip");
                                String nationality = office_Obj.getString("nationality");
                                String price = office_Obj.getString("price");
                                if(!price.contains("DK") || !price.contains("Dk") || !price.contains("dk")){
                                    price = price+" DK";
                                }
                                maidsList.add(new IndividualMaidsModel(office_id, office_image,office_name, office_desc, nationality, price));
                            }
                            IndividualMaidsModel individualMaidsModel = new IndividualMaidsModel();
                            for (int i = 0; i < maidsList.size(); i++) {
                                if (i == 0 || i == 1 || i == 3 || i == 4) {
                                } else if (i == 2) {
                                    maidsList.add(2, individualMaidsModel);
                                } else if (((i % 5) == 0)) {
                                    maidsList.add(i, individualMaidsModel);
                                }
                            }

                            if (sort_alpha) {
                                ArrayList<IndividualMaidsModel> sortOffice_models = new ArrayList<>();
                                sortOffice_models.addAll(maidsList);

                                Collections.sort(sortOffice_models, new Comparator<IndividualMaidsModel>() {
                                    @Override
                                    public int compare(IndividualMaidsModel obj1, IndividualMaidsModel obj2) {
                                        String office_name1 = obj1.getName().toString();
                                        String office_name2 = obj2.getName().toString();
                                        return office_name1.compareTo(office_name2);
                                    }
                                });

                                IndividualMaidsModel office_model1 = new IndividualMaidsModel();
                                for (int i = 0; i < sortOffice_models.size(); i++) {
                                    if (i == 0 || i == 1 || i == 3 || i == 4) {
                                    } else if (i == 2) {
                                        sortOffice_models.add(2, office_model1);
                                    } else if (((i % 5) == 0)) {
                                        sortOffice_models.add(i, office_model1);
                                    }
                                }
                                adapter = new IndividualMaidsAdapter(getActivity(), sortOffice_models);
                                maids_lv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter = new IndividualMaidsAdapter(getActivity(), maidsList);
                                maids_lv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }

                            maids_lv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), maids_lv, new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            if (position == 0 || position == 1) {
                                                startActivity(new Intent(getActivity(), IndividualMaidsDetailsActivity.class).putExtra("maidId", maidsList.get(position).getId()));
                                            } else if (position == 2) {
                                            } else if (position == 3 || position == 4) {
                                                startActivity(new Intent(getActivity(), IndividualMaidsDetailsActivity.class).putExtra("maidId", maidsList.get(position).getId()));
                                            } else if (((position % 5) == 0)) {
                                            } else {
                                                startActivity(new Intent(getActivity(), IndividualMaidsDetailsActivity.class).putExtra("maidId", maidsList.get(position).getId()));
                                            }
                                        }

                                        @Override
                                        public void onLongItemClick(View view, int position) {
                                        }
                                    })
                            );

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_off_incity), Toast.LENGTH_LONG).show();
                        }

                        if (maidsList.size() == 0) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_maids_incity), Toast.LENGTH_LONG).show();
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


}
