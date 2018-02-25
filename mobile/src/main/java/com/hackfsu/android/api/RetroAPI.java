package com.hackfsu.android.api;

import android.content.ClipData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hackfsu.android.app.activity.MainActivity;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Randy Bruno-Piverger on 2/22/2018.
 */

public interface RetroAPI {


    final static String Test_Base = "https://testapi.hackfsu.com/api/";
    final static String Test_Login = "user/login";
    final static String Test_Profile = "user/get/profile";
    final static String Test_getHacks = "judge/hacks";
    final static String Test_sendHacks = "judge/hacks/upload";



    @POST(Test_Login)
    Call<ResponseBody> postUser(@Body RequestBody requestBody);

    @POST(Test_sendHacks)
    Call<ResponseBody> postHacks(@Body RequestBody requestbody);

   // @GET(Test_Profile)
   // void getProfile(@Query("mid") String param1, Callback<List<MainActivity.Profile>> callback);


    public class RetrofitClient {

        private Retrofit retrofit = null;

        public Retrofit getClient(String baseUrl) {
            if (retrofit==null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    public class Post {

        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("password")
        @Expose
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String title) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String body) {
            this.password = password;
        }

        public String toString() {
            return "Post{" +
                    "email=" + email +
                    ", password=" + password +
                    '}';
        }
    }




}