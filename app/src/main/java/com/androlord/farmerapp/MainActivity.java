package com.androlord.farmerapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.androlord.farmerapp.Activities.AddProduct;
import com.androlord.farmerapp.Activities.LoginActivity;
import com.androlord.farmerapp.Activities.OrderList;
import com.androlord.farmerapp.Adapter.FarmersListAdapter;
import com.androlord.farmerapp.Models.Farmer;
import com.androlord.farmerapp.Models.Products;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FarmersListAdapter.ClickHandler {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FloatingActionButton addProduct;
    RecyclerView myProductList;
    ArrayList<Products> list;
    FarmersListAdapter adapter;
    AppLocationService appLocationService;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        try {
            fetch();
        }catch (Exception e){
            Log.e("Error", "onCreate: "+e.getMessage());
        }

        fetch();
        appLocationService =new AppLocationService(this);
        Location location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);

        //you can hard-code the lat & long if you have issues with getting it
        //remove the below if-condition and use the following couple of lines
        //double latitude = 37.422005;
        //double longitude = -122.084095

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
        } else {
            showSettingsAlert();
        }

    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void fetch() {
        reference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Products products=(Products) dataSnapshot1.getValue(Products.class);
                    products.setKey(dataSnapshot1.getKey());
                    Log.d("ak47", "onDataChange: "+products.getKey());
                    if(mAuth.getCurrentUser()!=null&&products.getPhoneNo().equalsIgnoreCase(mAuth.getCurrentUser().getPhoneNumber())) {
                        list.add(products);
                        Log.d("ak47", "onDataChange: ");
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        String TAG="infoAuth";
        list=new ArrayList<Products>();
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        addProduct=findViewById(R.id.addItem);


        myProductList=findViewById(R.id.FarmersProductList);
        myProductList.setLayoutManager(new LinearLayoutManager(this));
        adapter=new FarmersListAdapter(list,this);
        myProductList.setAdapter(adapter);


        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddProduct.class));
            }
        });
        if(getIntent().hasExtra("Farmer")) {
            Farmer farmer = (Farmer) getIntent().getSerializableExtra("Farmer");
            reference.child("Farmers").child(farmer.getPhoneNumber()).setValue(farmer);
        }


    }

    @Override
    public void ItemSelected(int position, ImageView imageView) {
        Intent intent=new Intent(this, OrderList.class);
        intent.putExtra("id",list.get(position).getKey());
        ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(this,imageView, ViewCompat.getTransitionName(imageView));
        startActivity(intent, options.toBundle());
        //Toast.makeText(this,list.get(position).getKey()+" ",Toast.LENGTH_LONG).show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            String FILE_NAME = "Local Address";
            FileOutputStream fileOutputStream =null;
            try {
                fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
                fileOutputStream.write(locationAddress.getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        }
    }

