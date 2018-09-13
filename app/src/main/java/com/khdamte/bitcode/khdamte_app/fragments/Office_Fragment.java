package com.khdamte.bitcode.khdamte_app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.DetailsActivity;
import com.khdamte.bitcode.khdamte_app.activities.Offices_Activity;
import com.khdamte.bitcode.khdamte_app.adapter.Office_Adapter;
import com.khdamte.bitcode.khdamte_app.models.Office_Model;
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

public class Office_Fragment extends Fragment {

    private RecyclerView office_RecyclerView;
    private ArrayList<Office_Model> office_models;
    private AlertDialog progressDialog;
    private String city_id, office_name, maids_nation;
    private boolean sort_review, sort_alpha;
    private Office_Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_offices_layout, container, false);

        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);

        Offices_Activity.changeTitle(getResources().getString(R.string.maid_office));

        office_RecyclerView = (RecyclerView) rootView.findViewById(R.id.office_rv);
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
        office_RecyclerView.setLayoutManager(glm);

        city_id = getArguments().getString("city_id");
        office_name = getArguments().getString("office_name");
        maids_nation = getArguments().getString("maids_nation");
        sort_review = getArguments().getBoolean("sort_review");
        sort_alpha = getArguments().getBoolean("sort_alpha");
        if (isNetworkAvailable()) {
            if (office_name.equals("no") && maids_nation.equals("no") && !sort_review && !sort_alpha) {
                GetAllOffices(city_id, false);
            } else if (!office_name.equals("no") && maids_nation.equals("no") && !sort_review && !sort_alpha) {
                GetOfficeByName(office_name);
            } else if (office_name.equals("no") && !maids_nation.equals("no") && !sort_review && !sort_alpha) {
                GetOfficeByNation(maids_nation);
            } else if (office_name.equals("no") && maids_nation.equals("no") && sort_review && !sort_alpha) {
                SortAllOfficesByReview(city_id);
            } else if (office_name.equals("no") && maids_nation.equals("no") && !sort_review && sort_alpha) {
                GetAllOffices(city_id, true);
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    private void GetOfficeByName(String office_name) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getOfficeByName(city_id,office_name.trim());
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                try {
                    result = response.body().toString();
                    if (!result.equals("")) {
                        try {
                            office_models = new ArrayList<Office_Model>();
                            JSONObject mainObj = new JSONObject(result);
                            JSONArray offices_JsonArray = mainObj.getJSONArray("Response");
                            if (offices_JsonArray.length() != 0) {

                                for (int i = 0; i < offices_JsonArray.length(); i++) {
                                    JSONObject office_Obj = offices_JsonArray.getJSONObject(i);
                                    String office_id = office_Obj.getString("id");
                                    String office_image = office_Obj.getString("image");
                                    if (!office_image.equals("null")) {
                                        office_image = "http://www.khdamte.co" + office_image;
                                    } else {
                                        int default_logos[] = {R.drawable.logo1, R.drawable.logo2, R.drawable.logo3, R.drawable.logo4, R.drawable.logo5};
                                        office_image = "" + default_logos[randInt(0, default_logos.length - 1)];
                                    }
                                    String office_name = office_Obj.getString("name");
                                    String office_desc = office_Obj.getString("description");
                                    office_models.add(new Office_Model(office_id, office_name, office_image, office_desc));
                                }
                                Office_Model office_model = new Office_Model();
                                for (int i = 0; i < office_models.size(); i++) {
                                    if (i == 0 || i == 1 || i == 3 || i == 4) {
                                    } else if (i == 2) {
                                        office_models.add(2, office_model);
                                    } else if (((i % 5) == 0)) {
                                        office_models.add(i, office_model);
                                    }
                                }
                                Office_Adapter adapter = new Office_Adapter(getActivity(), office_models);
                                office_RecyclerView.setAdapter(adapter);
                                office_RecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), office_RecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                if (position == 0 || position == 1) {
                                                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                                } else if (position == 2) {
                                                } else if (position == 3 || position == 4) {
                                                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                                } else if (((position % 5) == 0)) {
                                                } else {
                                                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                                }
                                            }

                                            @Override
                                            public void onLongItemClick(View view, int position) {
                                            }
                                        })
                                );
                            }
                            if (office_models.size() == 0) {
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
                    ex.printStackTrace();
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

    private void GetOfficeByNation(String nation_id) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getOfficesByNation(city_id, nation_id);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();

                if (!result.equals("")) {
                    try {
                        office_models = new ArrayList<Office_Model>();
                        JSONObject mainObj = new JSONObject(result);
                        JSONArray offices_JsonArray = mainObj.getJSONArray("Response");
                        if (offices_JsonArray.length() != 0) {
                            for (int i = 0; i < offices_JsonArray.length(); i++) {
                                JSONObject office_Obj = offices_JsonArray.getJSONObject(i);
                                String office_id = office_Obj.getString("officeid");
                                String office_image = office_Obj.getString("image");
                                if (!office_image.equals("null")) {
                                    office_image = "http://www.khdamte.co" + office_image;
                                } else {
                                    int default_logos[] = {R.drawable.logo1, R.drawable.logo2, R.drawable.logo3, R.drawable.logo4, R.drawable.logo5};
                                    office_image = "" + default_logos[randInt(0, default_logos.length - 1)];
                                }
                                String office_name = office_Obj.getString("name");
                                String office_desc = office_Obj.getString("description");
                                office_models.add(new Office_Model(office_id, office_name, office_image, office_desc));
                            }
                            Office_Model office_model = new Office_Model();
                            for (int i = 0; i < office_models.size(); i++) {
                                if (i == 0 || i == 1 || i == 3 || i == 4) {
                                } else if (i == 2) {
                                    office_models.add(2, office_model);
                                } else if (((i % 5) == 0)) {
                                    office_models.add(i, office_model);
                                }
                            }
                            Office_Adapter adapter = new Office_Adapter(getActivity(), office_models);
                            office_RecyclerView.setAdapter(adapter);
                            office_RecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), office_RecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            if (position == 0 || position == 1) {
                                                startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                            } else if (position == 2) {
                                            } else if (position == 3 || position == 4) {
                                                startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                            } else if (((position % 5) == 0)) {
                                            } else {
                                                startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                            }
                                        }

                                        @Override
                                        public void onLongItemClick(View view, int position) {
                                        }
                                    })
                            );
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.toast_no_office), Toast.LENGTH_SHORT).show();
                        }
                        if (office_models.size() == 0) {
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

    private void GetAllOffices(String city_id, final boolean sort_alpha) {
        showDialog();
        office_models = new ArrayList<>();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getAllOfficesInCity((city_id));
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
//                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_append_office), Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_off_incity), Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray offices_JsonArray = mainJsonObj.getJSONArray("Response");
                            if (offices_JsonArray.length() != 0) {
                                for (int i = 0; i < offices_JsonArray.length(); i++) {
                                    JSONObject office_Obj = offices_JsonArray.getJSONObject(i);
                                    String office_id = office_Obj.getString("id");
                                    String office_image = office_Obj.getString("image");
                                    if (!office_image.equals("null")) {
                                        office_image = "http://www.khadamte.com" + office_image;
                                    } else {
                                        int default_logos[] = {R.drawable.logo1, R.drawable.logo2, R.drawable.logo3, R.drawable.logo4, R.drawable.logo5};
                                        office_image = "" + default_logos[randInt(0, default_logos.length - 1)];
                                    }
                                    String office_name = office_Obj.getString("name");
                                    String office_desc = office_Obj.getString("description");
                                    office_models.add(new Office_Model(office_id, office_name, office_image, office_desc));
                                }
                                Office_Model office_model = new Office_Model();
                                for (int i = 0; i < office_models.size(); i++) {
                                    if (i == 0 || i == 1 || i == 3 || i == 4) {
                                    } else if (i == 2) {
                                        office_models.add(2, office_model);
                                    } else if (((i % 5) == 0)) {
                                        office_models.add(i, office_model);
                                    }
                                }

                                if (sort_alpha) {
                                    ArrayList<Office_Model> sortOffice_models = new ArrayList<Office_Model>();
                                    for (int i = 0; i < office_models.size(); i++) {
                                        String office_name = office_models.get(i).getOffice_name();
                                        String office_id = office_models.get(i).getOffice_id();
                                        String office_image = office_models.get(i).getOffice_img();
                                        String office_desc = office_models.get(i).getOffice_desc();
                                        if (office_name != null) {
                                            sortOffice_models.add(new Office_Model(office_id, office_name, office_image, office_desc));
                                        }
                                    }
                                    Collections.sort(sortOffice_models, new Comparator<Office_Model>() {
                                        public int compare(Office_Model obj1, Office_Model obj2) {
                                            String office_name1 = obj1.getOffice_name();
                                            String office_name2 = obj2.getOffice_name();
                                            return office_name1.compareTo(office_name2);
                                        }
                                    });
                                    Office_Model office_model1 = new Office_Model();
                                    for (int i = 0; i < sortOffice_models.size(); i++) {
                                        if (i == 0 || i == 1 || i == 3 || i == 4) {
                                        } else if (i == 2) {
                                            sortOffice_models.add(2, office_model1);
                                        } else if (((i % 5) == 0)) {
                                            sortOffice_models.add(i, office_model1);
                                        }
                                    }
                                    adapter = new Office_Adapter(getActivity(), sortOffice_models);
                                    office_RecyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    adapter = new Office_Adapter(getActivity(), office_models);
                                    office_RecyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }

                                office_RecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), office_RecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                if (position == 0 || position == 1) {
                                                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                                } else if (position == 2) {
                                                } else if (position == 3 || position == 4) {
                                                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                                } else if (((position % 5) == 0)) {
                                                } else {
                                                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
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

    public int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private void SortAllOfficesByReview(String city_id) {
        showDialog();
        office_models = new ArrayList<>();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.sortByReviews((city_id));
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainObj = new JSONObject(result);
                        JSONArray offices_JsonArray = mainObj.getJSONArray("Response");
                        if (offices_JsonArray.length() != 0) {
                            for (int i = 0; i < offices_JsonArray.length(); i++) {
                                JSONObject office_Obj = offices_JsonArray.getJSONObject(i);
                                String office_id = office_Obj.getString("id");
                                String office_image = office_Obj.getString("image");
                                if (!office_image.equals("null")) {
                                    office_image = "http://www.khdamte.co" + office_image;
                                } else {
                                    int default_logos[] = {R.drawable.logo1, R.drawable.logo2, R.drawable.logo3, R.drawable.logo4, R.drawable.logo5};
                                    office_image = "" + default_logos[randInt(0, default_logos.length - 1)];
                                }
                                String office_name = office_Obj.getString("name");
                                String office_desc = office_Obj.getString("description");
                                office_models.add(new Office_Model(office_id, office_name, office_image, office_desc));
                            }
                            Office_Model office_model = new Office_Model();
                            for (int i = 0; i < office_models.size(); i++) {
                                if (i == 0 || i == 1 || i == 3 || i == 4) {
                                } else if (i == 2) {
                                    office_models.add(2, office_model);
                                } else if (((i % 5) == 0)) {
                                    office_models.add(i, office_model);
                                }
                            }
                            adapter = new Office_Adapter(getActivity(), office_models);
                            office_RecyclerView.setAdapter(adapter);
                            office_RecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), office_RecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            if (position == 0 || position == 1) {
                                                startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                            } else if (position == 2) {
                                            } else if (position == 3 || position == 4) {
                                                startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
                                            } else if (((position % 5) == 0)) {
                                            } else {
                                                startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("office_id", office_models.get(position).getOffice_id()));
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
                        if (office_models.size() == 0) {
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
}
