package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.EOFException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    LocationManager locationManager;
    LocationListener locationListener;

    private GoogleMap mMap;
    public void addLocation(Location location,String title){
        if(location!=null){
         LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
         mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,12));

    }}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                addLocation(lastLocation,"Your Location");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
               mMap.setOnMapLongClickListener(this);
        Intent intent=getIntent();
        int k=intent.getIntExtra("placeNumber",0);
        if(k==0) {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    addLocation(location, "Your Location");

                }
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                addLocation(lastLocation,"Your Location");

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }


        }else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.location.get(intent.getIntExtra("placeNumber", 0)).latitude);
            placeLocation.setLongitude(MainActivity.location.get(intent.getIntExtra("placeNumber", 0)).longitude);
            if(placeLocation!=null) {
                addLocation(placeLocation, MainActivity.address.get(intent.getIntExtra("placeNumber", 0)));
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geo=new Geocoder(getApplicationContext(), Locale.getDefault());
        String address="";
        try {
            List<Address> listAddress = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(listAddress!=null && listAddress.size()>0){
                if(listAddress.get(0).getThoroughfare()!=null) {
                    if (listAddress.get(0).getSubThoroughfare() != null) {
                        address += listAddress.get(0).getSubThoroughfare() + " ";
                    }
                    address += listAddress.get(0).getThoroughfare()+" ";



                }}
        }catch (Exception e){
            e.printStackTrace();
        }if(address.equals("")){
            SimpleDateFormat sdf=new SimpleDateFormat( "HH:mm yyyy-MM-dd");
            address +=sdf.format(new Date());
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        MainActivity.address.add((address));
        MainActivity.location.add(latLng);
        MainActivity.adapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);

        try{
            ArrayList<String>latitudes=new ArrayList<>();
            ArrayList<String>longitudes=new ArrayList<>();
            for(LatLng cord:MainActivity.location){
                latitudes.add(Double.toString(cord.latitude));
                longitudes.add(Double.toString(cord.longitude));
            }
            sharedPreferences.edit().putString("places",ObjectSerializer.serialize((Serializable) MainActivity.address)).apply();
            sharedPreferences.edit().putString("lats",ObjectSerializer.serialize((Serializable) latitudes)).apply();
            sharedPreferences.edit().putString("lons",ObjectSerializer.serialize((Serializable) longitudes)).apply();


        }catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
    }
}