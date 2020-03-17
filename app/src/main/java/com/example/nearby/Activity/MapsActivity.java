package com.example.nearby.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearby.Model.Example;
import com.example.nearby.Model.Result;
import com.example.nearby.R;
import com.example.nearby.Repository.NearByAPI;
import com.example.nearby.SimpleClasses.Constants;
import com.example.nearby.ViewModel.NearByViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private boolean mLocationPermissionGranted = false;

    private FusedLocationProviderClient fusedLocationClient;

    Marker currentMarker;

    public static Location myLocation;

    private LocationCallback mLocationCallback;

    MaterialButton btnRestaurant,btnHospital,btnDetails;

    NearByViewModel nearByViewModel;

    public static String type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        btnRestaurant = findViewById(R.id.btnRestaurant);

        btnHospital = findViewById(R.id.btnHospital);

        btnDetails = findViewById(R.id.btnDetails);

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this,PlaceListActivity.class);
                startActivity(intent);
            }
        });

    }

    public static GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                Log.e("granted", "onResume");
                getLastLocation();
            } else {
                getLocationPermission();
            }
        }
    }
    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, Constants.ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(MapsActivity.this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, Constants.PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(MapsActivity.this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.e("granted", "permission");

            mLocationPermissionGranted = true;
            getLastLocation();

        } else {
            Log.e("granted", "no location permission");

            // reuqest for permission
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ENABLE_GPS: {
                Log.e("granted", "gps");

                if (mLocationPermissionGranted) {
                    getLastLocation();
                } else {
                    getLocationPermission();
                }
                break;
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("granted", "fine location permission granted");

                    mLocationPermissionGranted = true;

                    getLastLocation();
                }
                else {
                    Toast.makeText(MapsActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            }
        }
    }
    private void getLastLocation() {
        Log.e("TAG","GET LAST GRANTED");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Log.e("TAG","NOT GRANTED");
            return;
        }


        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(Constants.UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(Constants.FASTEST_INTERVAL);


        Log.d(TAG, "getLocation: getting location information.");
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    if (location != null) {

                        myLocation=location;

                        Log.e(TAG, "last location");

                        fusedLocationClient.removeLocationUpdates(mLocationCallback);

                        animateCamera(location);

                    }

                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy,
                    mLocationCallback,
                    null /* Looper */);

        }catch (SecurityException e){
            Log.e("Exception",e.toString());

        }

    }
    private void animateCamera(final Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition newCamPos = new CameraPosition(latLng,
                15.5f,
                mMap.getCameraPosition().tilt, //use old tilt
                mMap.getCameraPosition().bearing); //use old bearing
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null);

        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title("My Location"));
        currentMarker.showInfoWindow();

        btnRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "restaurant";
                Log.d(TAG, "Button is Clicked");

                setRadius();


            }
        });
        btnHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "hospital";
                Log.d(TAG, "Button is Clicked");

                setRadius();

            }
        });
    }

    private void setRadius() {
        //launch opacity chooser
        final Dialog seekDialog = new Dialog(MapsActivity.this);
        seekDialog.setTitle("Set Radius:");

        seekDialog.setContentView(R.layout.layout_radius);
        Window window = seekDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //get ui elements
        final TextView seekTxt = (TextView)seekDialog.findViewById(R.id.opq_txt);
        final SeekBar seekOpq = (SeekBar)seekDialog.findViewById(R.id.opacity_seek);
        //set max
        seekOpq.setMax(10000);
        //show current level
        int currLevel = Constants.PROXIMITY_RADIUS;
        seekTxt.setText(Integer.toString(currLevel));
        seekOpq.setProgress(currLevel);
        //update as user interacts
        seekOpq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekTxt.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });
        //listen for clicks on ok
        MaterialButton opqBtn = (MaterialButton)seekDialog.findViewById(R.id.opq_ok);
        opqBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Constants.PROXIMITY_RADIUS=seekOpq.getProgress();
                seekDialog.dismiss();
                if (type.equalsIgnoreCase("hospital"))
                    btnDetails.setText("Hospital List");
                if (type.equalsIgnoreCase("restaurant"))
                    btnDetails.setText("Restaurant List");
                btnDetails.setVisibility(View.VISIBLE);
                setViewModel();

            }
        });
        //show dialog
        seekDialog.show();
    }

    private void setViewModel() {
        nearByViewModel = ViewModelProviders.of(MapsActivity.this).get(NearByViewModel.class);
        nearByViewModel.setExampleLiveData(myLocation.getLatitude(), myLocation.getLongitude(),type);
        nearByViewModel.getExampleLiveData().observe(MapsActivity.this, new Observer<Example>() {
            @Override
            public void onChanged(Example example) {
                if (example!=null){
                    setPlaces(example);
                }
            }
        });
    }


    public static List<Result> results;
    private void setPlaces(Example response) {
        try {
            results=response.getResults();
            mMap.clear();
            // This loop will go through all the results and add marker on each location.
            for (int i = 0; i < response.getResults().size(); i++) {
                Double lat = response.getResults().get(i).getGeometry().getLocation().getLat();
                Double lng = response.getResults().get(i).getGeometry().getLocation().getLng();
                String placeName = response.getResults().get(i).getName();
                String vicinity = response.getResults().get(i).getVicinity();
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(lat, lng);
                // Position of Marker on Map
                markerOptions.position(latLng);
                // Adding Title to the Marker
                markerOptions.title(placeName + " : " + vicinity);
                // Adding Marker to the Camera.
                Marker m = mMap.addMarker(markerOptions);
                // Adding colour to the marker
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                // move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                animateCamera(myLocation);
            }
        } catch (Exception e) {
            Log.d("onResponse", "There is an error");
            e.printStackTrace();
        }
    }

}
