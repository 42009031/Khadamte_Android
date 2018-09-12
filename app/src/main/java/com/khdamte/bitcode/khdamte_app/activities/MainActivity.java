package com.khdamte.bitcode.khdamte_app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.adapter.Navigation_Adapter;
import com.khdamte.bitcode.khdamte_app.fragments.OfficePlacesFragment;
import com.khdamte.bitcode.khdamte_app.models.AdsModel;
import com.khdamte.bitcode.khdamte_app.models.DBHelper;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.models.NavigationModel;
import com.khdamte.bitcode.khdamte_app.web_service.retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.khdamte.bitcode.khdamte_app.web_service.retrofit.KhadamtyApi.RETROFIT;

public class MainActivity extends AppCompatActivity {

    private static TextView title_toolbar;
    public static Typeface lightFace;
    private ListView nav_listView;
    private Navigation_Adapter nav_adapter;
    private ArrayList<NavigationModel> nav_items;
    private FragmentManager fragmentManager;
    private DrawerLayout drawer;
    private ImageView back_img;


    public static void setTitle_toolbar(String title) {
        title_toolbar.setText(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String language = Locale.getDefault().getDisplayLanguage();
        if (language.contains("العربية")) {
            lightFace = Typeface.createFromAsset(getAssets(), "fonts/arabic_font.ttf");
        } else {
            lightFace = Typeface.createFromAsset(getAssets(), "fonts/comic.ttf");
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        title_toolbar = (TextView) toolbar.findViewById(R.id.title_toolbar);
        back_img = (ImageView) toolbar.findViewById(R.id.back_btn);

        title_toolbar.setTypeface(lightFace, Typeface.BOLD);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Helper.setSrc4BackImg(back_img);

        setSupportActionBar(toolbar);



        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_frame, new OfficePlacesFragment()).addToBackStack(null).commit();

        nav_items = new ArrayList<NavigationModel>();

        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        final String user_id = prefs.getString("id", null);
        final String userRole = prefs.getString("userRole", null);

        nav_listView = (ListView) findViewById(R.id.nav_listView);


        if (user_id != null) {
            if(userRole.toLowerCase().equals("owner")){
                final String[] nav_owner_title = getResources().getStringArray(R.array.nav_after_login);
                final int[] nav_user_img = {R.drawable.nav_offices,
                        R.drawable.nav_myoffices,
                        R.drawable.nav_fav,
                        R.drawable.nav_about,
                        R.drawable.nav_share,
                        R.drawable.nav_logout};
                for (int i = 0; i < nav_owner_title.length; i++) {
                    nav_items.add(new NavigationModel(nav_user_img[i], nav_owner_title[i]));
                }

            }else{
                final String[] nav_owner_title = getResources().getStringArray(R.array.nav_after_login_user);
                final int[] nav_user_img = {R.drawable.nav_offices,
                        R.drawable.nav_myoffices,
                        R.drawable.nav_fav,
                        R.drawable.nav_about,
                        R.drawable.nav_share,
                        R.drawable.nav_logout};
                for (int i = 0; i < nav_owner_title.length; i++) {
                    nav_items.add(new NavigationModel(nav_user_img[i], nav_owner_title[i]));
                }
            }

//            String firebase_token = FirebaseInstanceId.getInstance().getToken();
        } else {
            final String[] nav_owner_title = getResources().getStringArray(R.array.nav_before_login);
            final int[] nav_user_img = {R.drawable.nav_login,
                    R.drawable.nav_offices,
                    R.drawable.nav_fav,
                    R.drawable.nav_about,
                    R.drawable.nav_share};
            for (int i = 0; i < nav_owner_title.length; i++) {
                nav_items.add(new NavigationModel(nav_user_img[i], nav_owner_title[i]));
            }
        }

        nav_adapter = new Navigation_Adapter(this, nav_items);
        nav_listView.setAdapter(nav_adapter);
        nav_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (user_id != null) {
// after login
                    if(userRole.toLowerCase().equals("owner")){
                        if (position == 0) {
                            fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container_frame, new OfficePlacesFragment()).addToBackStack(null).commit();
                        } else if (position == 1) {
                            startActivity(new Intent(MainActivity.this, MyOfficeProfileActivity.class));
                        } else if (position == 2) {
                            startActivity(new Intent(MainActivity.this, FavouritActivity.class));
                        } else if (position == 3) {
                            startActivity(new Intent(MainActivity.this, AboutAppActivity.class));
                        } else if (position == 4) {
                            ShareApp();
                        } else if (position == 5) {
                            LogoutDialog();
                        }
                    }else{
                        if (position == 0) {
                            fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container_frame, new OfficePlacesFragment()).addToBackStack(null).commit();
                        }  else if (position == 1) {
                            startActivity(new Intent(MainActivity.this, RegisterMaidsDataActivity.class));
                        }  else if (position == 2) {
                            startActivity(new Intent(MainActivity.this, FavouritActivity.class));
                        } else if (position == 3) {
                            startActivity(new Intent(MainActivity.this, AboutAppActivity.class));
                        } else if (position == 4) {
                            ShareApp();
                        } else if (position == 5) {
                            LogoutDialog();
                        }
                    }
// before login
                } else {
                    if (position == 0) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    } else if (position == 1) {
                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container_frame, new OfficePlacesFragment()).addToBackStack(null).commit();
                    } else if (position == 2) {
                        if (user_id != null) {
                            startActivity(new Intent(MainActivity.this, FavouritActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_login_first), Toast.LENGTH_SHORT).show();
                        }
                    } else if (position == 3) {
                        startActivity(new Intent(MainActivity.this, AboutAppActivity.class));
                    } else if (position == 4) {
                        ShareApp();
                    }
                }

                drawer.closeDrawers();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

    }


    private void LogoutDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.logout_title))
                .setMessage(getResources().getString(R.string.logout_body))
                .setPositiveButton(getResources().getString(R.string.logout_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        SharedPreferences editor = getSharedPreferences("USER_DATA", MODE_PRIVATE);
                        editor.edit().clear().apply();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));

                        DBHelper dbHelper = new DBHelper(MainActivity.this);
                        dbHelper.deleteAllOffices();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.logout_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0 || count == 1) {
                GetAllAdsFromServer();
            } else {
                super.onBackPressed();
            }
        }
    }

    public Typeface getLightFace() {
        return lightFace;
    }

    public void setLightFace(Typeface lightFace) {
        this.lightFace = lightFace;
    }

    private void ShareApp() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nav_logo);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/nav_logo.jpg";
        OutputStream out = null;
        File file = new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        path = file.getPath();
        Uri bmpUri = Uri.parse("file://" + path);
        Intent shareIntent = new Intent();
        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.about_details));
        shareIntent.setType("image/jpg");
        startActivity(Intent.createChooser(shareIntent, "Share with"));
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

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
                            Toast.makeText(MainActivity.this, "No Ads Found !!", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < adsJsonArray.length(); i++) {
                                JSONObject adsObj = adsJsonArray.getJSONObject(i);
                                String ads_img = adsObj.getString("advertise");
                                String office_name = adsObj.getString("name");
                                String office_id = adsObj.getString("officeid");
                                adsModels.add(new AdsModel("http://www.khdamte.co/" + ads_img, office_id, office_name));
                            }
                        }
                        Intent mainIntent = new Intent(MainActivity.this, MainAdsActivity.class);
                        mainIntent.putExtra("ads_list", adsModels);
                        MainActivity.this.startActivity(mainIntent);
                        MainActivity.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Ads Exist !!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error in response message !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
