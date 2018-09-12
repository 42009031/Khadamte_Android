package com.khdamte.bitcode.khdamte_app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.CommentAdapter;
import com.khdamte.bitcode.khdamte_app.models.CommentModels;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.activities.MainActivity.lightFace;
import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

/**
 * Created by Amado on 8/10/2017.
 */

public class Comment_Activity extends Activity implements View.OnClickListener {

    private ImageView back_btn;
    private TextView title_toolbar;
    private ListView comment_listView;
    private Button add_comment_btn;
    private CommentAdapter adapter;
    private ArrayList<CommentModels> commentModels;
    private AlertDialog progressDialog;
    private String office_id, user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_list_layout);

        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        user_id = prefs.getString("id", null);
        office_id = getIntent().getExtras().getString("office_id");

        progressDialog = new SpotsDialog(this, R.style.Custom);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        comment_listView = (ListView) findViewById(R.id.comment_listView);
        add_comment_btn = (Button) findViewById(R.id.add_comment_btn);

        title_toolbar.setTypeface(lightFace);
        add_comment_btn.setTypeface(lightFace);

        Helper.setSrc4BackImg(back_btn, Locale.getDefault().getDisplayLanguage());

        back_btn.setOnClickListener(this);
        add_comment_btn.setOnClickListener(this);

        commentModels = new ArrayList<>();
        adapter = new CommentAdapter(Comment_Activity.this, commentModels);
        comment_listView.setAdapter(adapter);

        if (isNetworkAvailable()) {
            GetAllComments(office_id);
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_error_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.add_comment_btn:
                if (user_id != null) {
                    showCommentDialog();
                } else {
                    Toast.makeText(this, getString(R.string.toast_login_first), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showCommentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.comment_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView dialog_title_textView = (TextView) dialog.findViewById(R.id.dialog_title_textView);
        final TextView editTextDialogUserInput = (EditText) dialog.findViewById(R.id.editTextDialogUserInput);
        final Button cancel_btn = (Button) dialog.findViewById(R.id.cancel_btn);
        final Button add_btn = (Button) dialog.findViewById(R.id.add_btn);

        dialog_title_textView.setTypeface(lightFace);
        editTextDialogUserInput.setTypeface(lightFace);
        add_btn.setTypeface(lightFace);
        cancel_btn.setTypeface(lightFace);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content_msg = editTextDialogUserInput.getText().toString();
                if (!content_msg.equals("")) {
                    postOfficeComment(office_id, user_id, content_msg);
                    dialog.cancel();
                } else {
                    Toast.makeText(Comment_Activity.this, getString(R.string.toast_type_alldata), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void postOfficeComment(final String office_id, String user_id, String value) {
        showDialog();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("officeId", office_id);
        jsonObj.addProperty("userid", user_id);
        jsonObj.addProperty("value", value);

        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.postComment(jsonObj);
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
                            Toast.makeText(Comment_Activity.this, getResources().getString(R.string.comment_added_success), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Comment_Activity.this, Comment_Activity.class).putExtra("office_id", office_id));
                            Comment_Activity.this.finish();
                        } else {
                            Toast.makeText(Comment_Activity.this, getResources().getString(R.string.failed_addedd_comment), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Comment_Activity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Comment_Activity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(Comment_Activity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void GetAllComments(String office_id) {
        showDialog();
        retrofit.KhadamtyApi KHADAMTY_API = RETROFIT.create(retrofit.KhadamtyApi.class);
        Call<JsonObject> connection = KHADAMTY_API.getAllOfficesComments(office_id);
        connection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = "";
                dismissDialog();
                result = response.body().toString();
                if (!result.equals("")) {
                    try {
                        JSONObject mainobj = new JSONObject(result);
                        JSONArray mainJsonObj = mainobj.getJSONArray("Response");
                        for (int i = 0; i < mainJsonObj.length(); i++) {
                            JSONObject obj = mainJsonObj.getJSONObject(i);
                            String content = obj.getString("value");
                            String username = obj.getString("fullname");
                            String date = obj.getString("postdate");
                            commentModels.add(new CommentModels(content, username, date));
                        }
                        adapter = new CommentAdapter(Comment_Activity.this, commentModels);
                        comment_listView.setAdapter(adapter);
                        if (commentModels.size() == 0) {
                            Toast.makeText(Comment_Activity.this, getString(R.string.noComments), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Comment_Activity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Comment_Activity.this, getResources().getString(R.string.toast_no_office), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(Comment_Activity.this, getResources().getString(R.string.toast_res_msg), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
