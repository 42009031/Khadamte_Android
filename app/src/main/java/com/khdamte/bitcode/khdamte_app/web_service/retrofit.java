package com.khdamte.bitcode.khdamte_app.web_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class retrofit {


    public interface KhadamtyApi {


        Retrofit RETROFIT = new Retrofit.Builder()
                .baseUrl("http://api.khadamte.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .build();


        @Headers({"Accept: application/json",
                "Content-Type: application/json"})
        @GET("Ads/get")
        Call<JsonObject> getAds();

        @GET("Country/GetNationality")
        Call<JsonObject> getNationalities();

        @GET("OthereService/Get")
        Call<JsonObject> getOtherServices();

        @Headers({"Accept: application/json",
                "Content-Type: application/json"})
        @GET("Country/Get")
        Call<JsonObject> getCountries();

        @Headers({"Accept: application/json",
                "Content-Type: application/json"})
        @GET("Country/Get/{country_id}")
        Call<JsonObject> getCities(@Path("country_id") String country_id);

        @Headers({"Accept: application/json", "Content-Type: application/json"})
        @GET("Office/GetByUserId/{user_id}")
        Call<JsonObject> getOfficeByUser(@Path("user_id") String user_id);

        @Headers({"Accept: application/json", "Content-Type: application/json"})
        @GET("Office/Get/{office_id}")
        Call<JsonObject> getOffice(@Path("office_id") String office_id);

        @Headers({"Accept: application/json",
                "Content-Type: application/json"})
        @GET("Ads/getoffice/{office_id}")
        Call<JsonObject> getAdsInfo(@Path("office_id") String office_id);

        @POST("Ads/PostView/{office_id}")
        Call<JsonObject> postView(@Path("office_id") String office_id);

        @POST("User/PostRegister")
        Call<JsonObject> postRegistration(@Body JsonObject userData);

        @POST("office/AddOfficePhoto/{office_id}")
        @Multipart
        Call<ResponseBody> AddOfficePhoto(@Path("office_id") String office_id,
                                        @Part MultipartBody.Part photo);

        @POST("User/PostLogged")
        Call<JsonObject> postLogin(@Body JsonObject loginData);

        @POST("User/ForgetPassword")
        Call<JsonArray> postForgotPassword(@Body JsonObject userEmail);

        @POST("Office/PostOffice")
        Call<JsonObject> postOffice(@Body JsonObject officeData);

        @POST("Office/UpdateOffice")
        Call<JsonObject> updateOffice(@Body JsonObject officeData);

        @GET("Office/GetByCityId/{city_id}")
        Call<JsonObject> getAllOfficesInCity(@Path("city_id") String city_id);


        @GET("Office/GetByName/{city_id}/{office_name}")
        Call<JsonObject> getOfficeByName(@Path("city_id") String city_id, @Path("office_name") String office_name);

        @GET("User/GetLikes/{user_id}")
        Call<JsonObject> getUserLikes(@Path("user_id") String user_id);

        @GET("Office/GetCommentsByUser/{user_id}")
        Call<JsonObject> getUserComments(@Path("user_id") String user_id);

        @GET("Services/GetByName/{city_id}/{office_name}")
        Call<JsonObject> getServOfficeByName(@Path("city_id") String city_id, @Path("office_name") String office_name);

        @GET("Office/GetByNationality/{city_id}/{nation_id}")
        Call<JsonObject> getOfficesByNation(@Path("city_id") String city_id, @Path("nation_id") String nation_id);

        @GET("Office/SortByReview/{city_id}")
        Call<JsonObject> sortByReviews(@Path("city_id") String city_id);

        @GET("Office/GetComments/{office_id}")
        Call<JsonObject> getAllOfficesComments(@Path("office_id") String office_id);

        @GET("Services/Get/{city_id}")
        Call<JsonObject> getAllServicesInCity(@Path("city_id") String city_id);

        @POST("Rate/PostRate/{office_id}")
        Call<JsonObject> postRate(@Path("office_id") String office_id, @Body JsonObject rateData);

        @POST("Office/Comment")
        Call<JsonObject> postComment(@Body JsonObject userData);

        @POST("Office/View")
        Call<JsonObject> postView(@Body JsonObject userData);

        @POST("Office/Call")
        Call<JsonObject> postCall(@Body JsonObject userData);

        @POST("Office/Like")
        Call<JsonObject> postLike(@Body JsonObject userData);

        @POST("Maid/PostMaid/{userID}")
        @Multipart
        Call<ResponseBody> postIndividualMaid(@Path("userID") String userID,
                                              @Part("name") RequestBody name,
                                              @Part("descrip") RequestBody descrip,
                                              @Part("age") RequestBody age,
                                              @Part("nationalityId") RequestBody nationalityId,
                                              @Part("stateId") RequestBody stateId,
                                              @Part("religion") RequestBody religion,
                                              @Part MultipartBody.Part photo,
                                              @Part("price") RequestBody price,
                                              @Part("currentMonth") RequestBody currentMonth);

        @GET("Maid/GetAllIndividualsMaid/{city_id}")
        Call<JsonObject> GetAllIndividualsMaid(@Path("city_id") String city_id);

        @GET("Maid/GetIndividualsMaid/{maid_id}")
        Call<JsonObject> GetIndividualsMaid(@Path("maid_id") String maid_id);
    }
}
