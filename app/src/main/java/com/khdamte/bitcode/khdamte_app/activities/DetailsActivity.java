package com.khdamte.bitcode.khdamte_app.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.Nationalty_Maids_Adapter;
import com.khdamte.bitcode.khdamte_app.adapter.PhoneAdapter;
import com.khdamte.bitcode.khdamte_app.models.DBHelper;
import com.khdamte.bitcode.khdamte_app.models.Flags_Model;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.models.Office_Model;
import com.khdamte.bitcode.khdamte_app.models.PhoneModel;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class DetailsActivity extends Activity implements View.OnClickListener {

    private TextView office_name_tv, nation_maids_title_tv, address_tv, adrs_val_tv, email_tv, email_val_tv, office_desc_tv,
            rate_count_tv, desc_title_tv, views_tv, call_tv, like_tv, comment_tv, otherServ_title_tv, otherSer_tv;
    private ImageView fav_img, back_btn, comment_img, like_img;
    private RecyclerView maids_natio_rv;
    private RatingBar ratingBar;
    private Nationalty_Maids_Adapter adapter;
    private Typeface lightFace;
    private ListView phone_lv;
    private PhoneAdapter phone_adapter;
    private ArrayList<PhoneModel> phone_arrayList;
    private int newPosition;
    private AlertDialog progressDialog;
    private ArrayList<Flags_Model> flags_arrayList;
    private ArrayList<Office_Model> office_models;
    private DBHelper dbHelper;
    private String office_id, user_id;
    private boolean check_like;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.office_details_layout);

        lightFace = MainActivity.lightFace;
        office_id = getIntent().getExtras().getString("office_id");
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        user_id = prefs.getString("id", null);

        otherServ_title_tv = (TextView) findViewById(R.id.otherServ_title_tv);
        otherSer_tv = (TextView) findViewById(R.id.otherSer_tv);

        office_name_tv = (TextView) findViewById(R.id.office_name_tv);
        office_desc_tv = (TextView) findViewById(R.id.desc_tv);
        rate_count_tv = (TextView) findViewById(R.id.rate_count_tv);
        nation_maids_title_tv = (TextView) findViewById(R.id.nation_maids_title_tv);
        address_tv = (TextView) findViewById(R.id.address_tv);
        adrs_val_tv = (TextView) findViewById(R.id.adrs_val_tv);
        email_tv = (TextView) findViewById(R.id.email_tv);
        email_val_tv = (TextView) findViewById(R.id.email_val_tv);
        desc_title_tv = (TextView) findViewById(R.id.desc_title_tv);

        views_tv = (TextView) findViewById(R.id.views_tv);
        call_tv = (TextView) findViewById(R.id.call_tv);
        like_tv = (TextView) findViewById(R.id.like_tv);
        comment_tv = (TextView) findViewById(R.id.comment_tv);

        comment_img = (ImageView) findViewById(R.id.comment_img);
        like_img = (ImageView) findViewById(R.id.like_img);
        fav_img = (ImageView) findViewById(R.id.fav_img);
        back_btn = (ImageView) findViewById(R.id.back_btn);

        office_models = new ArrayList<>();
        dbHelper = new DBHelper(this);

        office_name_tv.setTypeface(lightFace);
        nation_maids_title_tv.setTypeface(lightFace);
        address_tv.setTypeface(lightFace);
        adrs_val_tv.setTypeface(lightFace);
        email_tv.setTypeface(lightFace);
        email_val_tv.setTypeface(lightFace);
        office_desc_tv.setTypeface(lightFace);
        rate_count_tv.setTypeface(lightFace);
        desc_title_tv.setTypeface(lightFace);
        call_tv.setTypeface(lightFace);
        views_tv.setTypeface(lightFace);
        like_tv.setTypeface(lightFace);
        comment_tv.setTypeface(lightFace);
        otherSer_tv.setTypeface(lightFace);
        otherServ_title_tv.setTypeface(lightFace);

        Helper.setSrc4BackImg(back_btn, Locale.getDefault().getDisplayLanguage());

        progressDialog = new SpotsDialog(DetailsActivity.this, R.style.Custom);

        phone_arrayList = new ArrayList<>();
        phone_lv = (ListView) findViewById(R.id.phones_lv);
        phone_adapter = new PhoneAdapter(DetailsActivity.this, phone_arrayList);
        phone_lv.setAdapter(phone_adapter);
        phone_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                setNewPosition(position);
                new AlertDialog.Builder(DetailsActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.call_dialog_title))
                        .setMessage(getString(R.string.call_dialog_body) + " [" + phone_arrayList.get(position).getPhone_number() + "] ?")
                        .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Handler mHandler = new Handler(getMainLooper());
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        onCall(position);
                                        postOfficeCall(office_id);
                                    }
                                });
                            }

                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });


        like_img.setOnClickListener(this);
        fav_img.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        comment_img.setOnClickListener(this);

        flags_arrayList = new ArrayList<>();
        maids_natio_rv = (RecyclerView) findViewById(R.id.maids_natio_rv);
        adapter = new Nationalty_Maids_Adapter(DetailsActivity.this, flags_arrayList);
        maids_natio_rv.setAdapter(adapter);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setIsIndicator(true);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (user_id != null) {
                    showRateDilaog();
                } else {
                    Toast.makeText(DetailsActivity.this, getString(R.string.toast_login_first), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        if (isNetworkAvailable()) {
            if (user_id != null) {
                CheckUserLikes(user_id);
                CheckUserComments(user_id);
            }
            GetMyOfficeByOfficeId(office_id);
            postOfficeView(office_id);
        } else {
            Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
        }

        if (dbHelper.checkOfficeExist(office_id)) {
            fav_img.setImageResource(R.drawable.icon_fav_act);
        } else {
            fav_img.setImageResource(R.drawable.icon_fav);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.like_img:
                if (user_id != null) {
                    if (!check_like) { // dislike
                        postOfficeLike(office_id, user_id, "1");
                        like_img.setImageResource(R.drawable.icon_like);
                        check_like = true;
                        int like_val = Integer.parseInt(like_tv.getText().toString());
                        like_val++;
                        like_tv.setText(String.valueOf(like_val));
                    } else { // like
                        postOfficeLike(office_id, user_id, "0");
                        like_img.setImageResource(R.drawable.icon_like_gray);
                        check_like = false;
                        int like_val = Integer.parseInt(like_tv.getText().toString());
                        like_val--;
                        like_tv.setText(String.valueOf(like_val));
                    }
                } else {
                    Toast.makeText(this, getString(R.string.toast_login_first), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.comment_img:
                if (user_id != null) {
                    startActivity(new Intent(DetailsActivity.this, Comment_Activity.class).putExtra("office_id", office_id));
                } else {
                    Toast.makeText(this, getString(R.string.toast_login_first), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fav_img:
                if (dbHelper.checkOfficeExist(office_id)) {
                    dbHelper.deleteOffice(office_id);
                    fav_img.setImageResource(R.drawable.icon_fav);

                } else {
                    dbHelper.insertFavOffice(office_models.get(0).getOffice_id(), office_models.get(0).getOffice_name(),
                            office_models.get(0).getOffice_img(), office_models.get(0).getOffice_desc());
                    fav_img.setImageResource(R.drawable.icon_fav_act);
                }
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onCall(getNewPosition());
                } else {
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_call_permission), Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    public void onCall(int position) {
        int permissionCheck = ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
        } else {
            String phone_number = phone_arrayList.get(position).getPhone_number();
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + phone_number)));
        }
    }

    public int getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(int newPosition) {
        this.newPosition = newPosition;
    }

    private void showRateDilaog() {
        final Dialog dialog = new Dialog(DetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.rate_office_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView rate_title_bg = (TextView) dialog.findViewById(R.id.rate_title_bg);
        final RatingBar rate_office_rb = (RatingBar) dialog.findViewById(R.id.rate_office_rb);
        final Button cancel_btn = (Button) dialog.findViewById(R.id.cancel_btn);
        final Button rate_btn = (Button) dialog.findViewById(R.id.rate_btn);

        rate_title_bg.setTypeface(lightFace);
        cancel_btn.setTypeface(lightFace);
        rate_btn.setTypeface(lightFace);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rateVal = rate_office_rb.getRating();
                postRate(office_id, user_id, String.valueOf(rateVal));
                dialog.cancel();
            }
        });
        dialog.show();
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
                            Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_apped_office), Toast.LENGTH_LONG).show();
                        } else {
                            JSONObject offObj = mainJsonObj.getJSONObject("Response");
                            String office_id = offObj.getString("id");
                            String office_name = offObj.getString("name");
                            String office_description = offObj.getString("description");
                            String office_mob1 = offObj.getString("mob1");
                            String office_mob2 = offObj.getString("mob2");
                            String office_mob3 = offObj.getString("mob3");
                            String office_image = offObj.getString("image");
                            if (!office_image.equals("null")) {
                                office_image = "http://www.khdamte.co" + office_image;
                            } else {
                                int default_logos[] = {R.drawable.logo1, R.drawable.logo2, R.drawable.logo3, R.drawable.logo4, R.drawable.logo5};
                                office_image = "" + default_logos[randInt(0, default_logos.length - 1)];
                            }
                            String office_address = offObj.getString("address");
                            String office_email = offObj.getString("email");
                            int rates = 0;
                            double rates_average = 0.0, sum = 0.0;

                            JSONArray rates_array = offObj.getJSONArray("Rate");
                            for (int i = 0; i < rates_array.length(); i++) {
                                JSONObject rateObj = rates_array.getJSONObject(i);
                                String value = rateObj.getString("value");
                                rates = i + 1;
                                if (!value.equals("null")) {
                                    sum += Double.parseDouble(value);
                                    rates_average = sum / i;
                                }
                            }
                            if (rates == 0) {
                                rate_count_tv.setText(rates + "  " + getResources().getString(R.string.rates));
                            }else {
                                rate_count_tv.setText(rates + "  " + getResources().getString(R.string.rates));
                            }
                            ratingBar.setRating((float) rates_average);

                            JSONArray nat_array = offObj.getJSONArray("Nationality");
                            for (int i = 0; i < nat_array.length(); i++) {
                                JSONObject natObj = nat_array.getJSONObject(i);
                                String id = natObj.getString("Id");
                                String name = natObj.getString("name");
                                String pic = natObj.getString("pic");
                                pic = "http://www.khdamte.co" + pic;
                                flags_arrayList.add(new Flags_Model(name, id, pic));
                            }
                            adapter = new Nationalty_Maids_Adapter(DetailsActivity.this, flags_arrayList);
                            maids_natio_rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if (!office_mob1.equals("")) {
                                phone_arrayList.add(new PhoneModel(getResources().getString(R.string.phone1), office_mob1));
                            }
                            if (!office_mob2.equals("")) {
                                phone_arrayList.add(new PhoneModel(getResources().getString(R.string.phone2), office_mob2));
                            }
                            if (!office_mob3.equals("")) {
                                phone_arrayList.add(new PhoneModel(getResources().getString(R.string.phone3), office_mob3));
                            }
                            phone_adapter = new PhoneAdapter(DetailsActivity.this, phone_arrayList);
                            phone_lv.setAdapter(phone_adapter);

                            StringBuilder otherServStr= new StringBuilder();
                            JSONArray OtherServices = offObj.getJSONArray("OtherServices");
                            for (int i = 0; i < OtherServices.length(); i++) {
                                JSONObject servObj = OtherServices.getJSONObject(i);
                                String serv_name = servObj.getString("name");
                                otherServStr.append(serv_name+" , ");
                            }
                            if(OtherServices.length() != 0){
                                otherServ_title_tv.setVisibility(View.VISIBLE);
                                otherSer_tv.setVisibility(View.VISIBLE);
                                otherSer_tv.setText(otherServStr.substring(0, otherServStr.length()-2));
                            }


                            String likes_str = offObj.getString("likes");
                            String comments_str = offObj.getString("comments");
                            String views_str = offObj.getString("views");
                            String calls_str = offObj.getString("calls");

                            office_name_tv.setText(office_name);
                            office_desc_tv.setText(office_description);
                            adrs_val_tv.setText(office_address);
                            email_val_tv.setText(office_email);
                            if (views_str.equals("null"))
                                views_tv.setText("0");
                            else
                                views_tv.setText(views_str);
                            if (likes_str.equals("null"))
                                like_tv.setText("0");
                            else
                                like_tv.setText(likes_str);
                            if (comments_str.equals("null"))
                                comment_tv.setText("0");
                            else
                                comment_tv.setText(comments_str);
                            if (calls_str.equals("null"))
                                call_tv.setText("0");
                            else
                                call_tv.setText(calls_str);

                            office_models.add(new Office_Model(office_id, office_name, office_image, office_description));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void CheckUserLikes(String user_id) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getUserLikes(user_id);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONArray jsonArray = obj.getJSONArray("Response");
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String off_id = jsonObject.getString("Office");
                                if (off_id.equals(office_id)) {
                                    like_img.setImageResource(R.drawable.icon_like);
                                    check_like = true;
                                }
                            }
                        } else {
                            like_img.setImageResource(R.drawable.icon_like_gray);
                            check_like = false;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void CheckUserComments(String user_id) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getUserComments(user_id);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainobj = new JSONObject(result);
                        JSONArray jsonArray = mainobj.getJSONArray("Response");
                        if(jsonArray.length() != 0){
                            for(int i=0 ; i<jsonArray.length() ; i++){
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String officeID = obj.getString("officeId");
                                if (officeID.equalsIgnoreCase(office_id)) {
                                    comment_img.setImageResource(R.drawable.icon_comment);
                                    return;
                                }
                            }
                        }else{
                            comment_img.setImageResource(R.drawable.icon_comment_gray);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void postRate(final String office_id, String user_id, String rate_val) {
        showDialog();

        JsonObject rateObj = new JsonObject();
        rateObj.addProperty("value", rate_val);
        rateObj.addProperty("userid", user_id);

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.postRate(office_id, rateObj);
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
                        if (success) {
                            Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_rate_success), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(DetailsActivity.this, DetailsActivity.class).putExtra("office_id", office_id));
                        } else {
                            Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_rate_error), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DetailsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void postOfficeView(String office_id) {
        showDialog();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("officeId", office_id);

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.postView(jsonObj);
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
                        Toast.makeText(DetailsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void postOfficeLike(String office_id, String user_id, String value) {
        showDialog();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("officeId", office_id);
        jsonObj.addProperty("userid", user_id);
        jsonObj.addProperty("value", value);

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.postLike(jsonObj);
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
                        Toast.makeText(DetailsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) DetailsActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public void onBackPressed() {finish();
    }
}
