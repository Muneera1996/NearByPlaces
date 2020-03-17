package com.example.nearby.Retrofit;

import com.example.nearby.Model.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyCR7YT8rw2wXmiqEVqojInlemVvb0UtzLA")
    Call<Example> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);

}
