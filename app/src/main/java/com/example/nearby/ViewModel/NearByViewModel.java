package com.example.nearby.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nearby.Activity.MapsActivity;
import com.example.nearby.Model.Example;
import com.example.nearby.Repository.NearByAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearByViewModel extends ViewModel {

    private NearByAPI nearByAPI;
    private MutableLiveData<Example> ExampleLiveData;


    public void setExampleLiveData(double latitude, double longitude, String nearbyPlace){
        nearByAPI = new NearByAPI();
        this.ExampleLiveData = nearByAPI.getNearByPlaces(latitude,longitude,nearbyPlace);

    }

    public LiveData<Example> getExampleLiveData() {
        return ExampleLiveData;
    }
}
