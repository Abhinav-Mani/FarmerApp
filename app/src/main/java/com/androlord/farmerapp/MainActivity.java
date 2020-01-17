package com.androlord.farmerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androlord.farmerapp.Activities.AddProduct;
import com.androlord.farmerapp.Activities.LoginActivity;
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

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FloatingActionButton addProduct;
    RecyclerView myProductList;
    ArrayList<Products> list;
    FarmersListAdapter adapter;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        fetch();
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
                    if(products.getPhoneNo().equalsIgnoreCase(mAuth.getCurrentUser().getPhoneNumber())) {
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
}
