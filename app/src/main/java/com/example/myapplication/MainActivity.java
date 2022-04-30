package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static List<String> address=new ArrayList<>();
   static  List<LatLng> location=new ArrayList<LatLng>();
   static  ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView= findViewById(R.id.list);
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);
        ArrayList<String>latitudes=new ArrayList<>();
        ArrayList<String>longitudes=new ArrayList<>();
        address.clear();
        latitudes.clear();
        longitudes.clear();
        location.clear();
        try{

            address=(ArrayList<String>)ObjectSerializer.deserialize("places",sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));

            latitudes=(ArrayList<String>)ObjectSerializer.deserialize("lats",sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes=(ArrayList<String>)ObjectSerializer.deserialize("lons",sharedPreferences.getString("lons",ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("address", String.valueOf(address));
            Log.i("address", String.valueOf(latitudes));
            Log.i("address", String.valueOf(longitudes));
            assert latitudes != null;
            assert longitudes != null;
            if(address.size()>0&&latitudes.size()>0&&longitudes.size()>0){
                if(address.size()==latitudes.size() && address.size()==longitudes.size()){
                   for(int i=0;i<latitudes.size();i++){
                       location.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
                   }
                }
            }else {
                address.add("Add the places");
                location.add(new LatLng(0,0));

            }
        }catch (Exception e){
            e.printStackTrace();
        }



         adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,address);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                  Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                  intent.putExtra("placeNumber",i);
                  startActivity(intent);

            }
        });

    }
}