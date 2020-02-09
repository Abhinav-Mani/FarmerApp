package com.androlord.farmerapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.androlord.farmerapp.Activities.AddProduct;
import com.androlord.farmerapp.Activities.BlockedUser;
import com.androlord.farmerapp.Activities.LoginActivity;
import com.androlord.farmerapp.Activities.OrderList;
import com.androlord.farmerapp.Adapter.FarmersListAdapter;
import com.androlord.farmerapp.LanguageHelper.SettingsActivity;
import com.androlord.farmerapp.LanguageHelper.SettingsFragment;
import com.androlord.farmerapp.Models.Farmer;
import com.androlord.farmerapp.Models.Products;
import com.androlord.farmerapp.Utils.SetPersistence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FarmersListAdapter.ClickHandler {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FloatingActionButton addProduct;
    RecyclerView myProductList;
    ArrayList<Products> list;
    FarmersListAdapter adapter;
    LocationManager locationManager;
    LocationListener locationListener;



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
                } else {
                    reference.child("Farmers").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Rejects").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("ak47", "onDataChange: "+dataSnapshot.getValue());
                            if(dataSnapshot.getValue()==null){

                            }else if((long)dataSnapshot.getValue()>2) {
                                Intent intent=new Intent(MainActivity.this, BlockedUser.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

        setListener();

        check();

        fetch();


    }

    private void setListener() {
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddProduct.class));
            }
        });
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    private void check() {
        if(getIntent().hasExtra("Farmer")) {
            Farmer farmer = (Farmer) getIntent().getSerializableExtra("Farmer");
            reference.child("Farmers").child(farmer.getPhoneNumber()).setValue(farmer);
            setLocation();
        }
        setLocation();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(locationManager!=null)
            reference.child("Farmers").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("address").setValue(lastKnownLocation.getLatitude()+" "+lastKnownLocation.getLongitude());
        }
    }

    private void setLocation() {
        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        reference.child("Farmers").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())).child("address").setValue(lastKnownLocation.getLatitude()+" "+lastKnownLocation.getLongitude());
    }

    private void fetch() {

        reference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Products products=(Products) dataSnapshot1.getValue(Products.class);
                    products.setKey(dataSnapshot1.getKey());
                    if(mAuth.getCurrentUser()!=null&&products.getPhoneNo().equalsIgnoreCase(mAuth.getCurrentUser().getPhoneNumber())) {
                        list.add(products);
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
        try{
            SetPersistence persistence=new SetPersistence();

        } catch (Exception e) {
            e.printStackTrace();
        }
        list=new ArrayList<Products>();
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        addProduct=findViewById(R.id.addItem);
        myProductList=findViewById(R.id.FarmersProductList);
        myProductList.setLayoutManager(new LinearLayoutManager(this));
        adapter=new FarmersListAdapter(list,this);
        myProductList.setAdapter(adapter);
    }
    @Override
    public void ItemSelected(int position, ImageView imageView) {
        Intent intent=new Intent(this, OrderList.class);
        intent.putExtra("item",list.get(position));
        ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(this,imageView, ViewCompat.getTransitionName(imageView));
        startActivity(intent, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 1000);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", "Request code is " + requestCode + ", result code is " + resultCode);
        switch (requestCode) {
            case 1000:
                if (resultCode == SettingsFragment.LANGUAGE_CHANGED) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
        }
    }

}

