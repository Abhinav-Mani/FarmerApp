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

import com.androlord.farmerapp.Adapter.OrderListAdapter;
import com.androlord.farmerapp.Models.OrderRequest;
import com.androlord.farmerapp.R;
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
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        init();

        fetch();
    }

    private void fetch() {
        Intent intent=getIntent();
        id="";
        if(intent.hasExtra("id"))
        {
            id=intent.getStringExtra("id");
        }
        else {
            finish();
        }

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
        recyclerView=findViewById(R.id.OrderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new OrderListAdapter(list,this);
        recyclerView.setAdapter(adapter);

        mRef= FirebaseDatabase.getInstance().getReference();
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
