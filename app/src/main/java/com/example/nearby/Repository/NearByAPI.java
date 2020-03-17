package com.example.nearby.Repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nearby.Model.Example;
import com.example.nearby.Retrofit.APIClient;
import com.example.nearby.Retrofit.APIInterface;
import com.example.nearby.SimpleClasses.Constants;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NearByAPI {

    private static final String TAG = NearByAPI.class.getSimpleName();
    private APIInterface apiInterface;
    MutableLiveData<Example> data;

    public NearByAPI() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    public MutableLiveData<Example> getNearByPlaces(double latitude, double longitude, String nearbyPlace) {

       data = new MutableLiveData<>();

        Call<Example> call = apiInterface.getNearbyPlaces(nearbyPlace, latitude + "," + longitude, Constants.PROXIMITY_RADIUS);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                if (response.body() != null) {
                    try {
                        data.setValue(response.body());
                        Log.e(TAG, response.body().toString());
                    } catch (Exception e) {
                        Log.d(TAG, "There is an error");
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                data.setValue(null);
               Log.e(TAG,t.getMessage());
            }
        });
        return data;

    }
}
