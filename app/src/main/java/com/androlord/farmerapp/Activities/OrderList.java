package com.androlord.farmerapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androlord.farmerapp.Adapter.OrderListAdapter;
import com.androlord.farmerapp.Models.OrderRequest;
import com.androlord.farmerapp.Models.Products;
import com.androlord.farmerapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderList extends AppCompatActivity implements OrderListAdapter.ClickHandler {
    RecyclerView recyclerView;
    OrderListAdapter adapter;
    ArrayList<OrderRequest> list;
    DatabaseReference mRef;
    ImageView imageView;
    String id;
    TextView name,price,quantity;
    Products product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        check();

        init();

        setData();

        fetch();
    }

    private void setData() {
        name.setText(product.getProductName());
        price.setText(product.getPrice());
        quantity.setText(product.getQuality());
    }

    private void check() {
        Intent intent=getIntent();
        id="";
        if(intent.hasExtra("item"))
        {
            product=(Products) intent.getSerializableExtra("item");
            id=product.getKey();
        }
        else {
            finish();
        }
    }

    private void fetch() {


        mRef.child("Requests").child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            OrderRequest orderRequest=(OrderRequest)dataSnapshot1.getValue(OrderRequest.class);
                            orderRequest.setKey(dataSnapshot1.getKey());
                            if(!orderRequest.getStatus().equalsIgnoreCase("Cancel"))
                                list.add(orderRequest);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void init() {
        list=new ArrayList<>();
        imageView=findViewById(R.id.productImage);
        recyclerView=findViewById(R.id.OrderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new OrderListAdapter(list,this);
        recyclerView.setAdapter(adapter);

        name=findViewById(R.id.ProductName);
        price=findViewById(R.id.ProductPrice);
        quantity=findViewById(R.id.ProductAmount);


        mRef= FirebaseDatabase.getInstance().getReference();

        Glide.with(getApplicationContext()).asBitmap().
                load(product.getImg()).
                fitCenter().
                error(R.drawable.vegi).
                fallback(R.drawable.vegi).
                placeholder(R.drawable.vegi).
                diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
    }

    @Override
    public void call(int position) {
        String phone = list.get(position).getFrom().trim();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);

    }

    @Override
    public void accept(int position) {
        mRef.child("Requests").child(id).child(list.get(position).getKey()).child("status").setValue("Accepted");
    }

    @Override
    public void decline(int position) {
        mRef.child("Requests").child(id).child(list.get(position).getKey()).child("status").setValue("Cancel");
    }
}
