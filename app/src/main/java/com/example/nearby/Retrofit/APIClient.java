package com.example.nearby.Retrofit;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {

    public static Retrofit retrofit=null;

    public static Retrofit getClient() {

        if(retrofit!=null){
            return retrofit;
        }
        else {

            String url = "https://maps.googleapis.com/maps/";

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit;

        }

    }

}