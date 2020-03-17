package com.example.nearby.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nearby.Adapter.NearByAdapter;
import com.example.nearby.Model.Result;
import com.example.nearby.R;
import com.example.nearby.ViewModel.NearByViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlaceListActivity extends AppCompatActivity implements NearByAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = PlaceListActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private NearByAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerView);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new NearByAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        mAdapter.setResults(MapsActivity.results);


    }



    @Override
    public void onItemClickListener(Result result) {

        Intent intent=new Intent(PlaceListActivity.this,PlaceDetailActivity.class);
        intent.putExtra("PlaceData",result);
        startActivity(intent);

    }
}

