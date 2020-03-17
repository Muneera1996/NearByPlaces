package com.example.nearby.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.nearby.Model.Result;
import com.example.nearby.R;

import java.io.Serializable;

public class PlaceDetailActivity extends AppCompatActivity {

    TextView textView;
    Intent intent;
    Result result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        textView = findViewById(R.id.details);
        intent=getIntent();

        if (intent!=null){
            result = (Result)getIntent().getSerializableExtra("PlaceData");
            textView.setText("Name: " +  result.getName() + "\n" + "Vicinity: " + result.getVicinity() + "\n" + "Location: " + result.getGeometry().getLocation() + "\n" + "Lat and Lng: "
                    + result.getGeometry().getLocation().getLat() + " and " + result.getGeometry().getLocation().getLng() + "\n" + "Hours: "+ result.getOpeningHours());




        }

    }
}
