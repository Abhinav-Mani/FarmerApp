package com.androlord.farmerapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androlord.farmerapp.Adapter.OrderListAdapter;
import com.androlord.farmerapp.Fragments.EditProductDialog;
import com.androlord.farmerapp.Models.OrderRequest;
import com.androlord.farmerapp.Models.Products;
import com.androlord.farmerapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderList extends AppCompatActivity implements OrderListAdapter.ClickHandler, View.OnClickListener, EditProductDialog.DialogListener{
    RecyclerView recyclerView;
    OrderListAdapter adapter;
    ArrayList<OrderRequest> list;
    DatabaseReference mRef;
    ImageView imageView;
    boolean ready;
    String id;
    Button edit,remove;
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

        setListners();
    }

    private void setListners() {
        edit.setOnClickListener(this);
        remove.setOnClickListener(this);
    }

    private void setData() {
        name.setText("Name:        "+product.getProductName());
        price.setText("Price:          "+"₹"+product.getPrice());
        quantity.setText("Quantity:     "+product.getQuality()+"Kg");
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
                            orderRequest.setAmount(orderRequest.getAmount().replace("Kg",""));
                            orderRequest.setAmount(orderRequest.getAmount().replace("kg",""));
                            if(!orderRequest.getStatus().equalsIgnoreCase("Cancel"))
                                list.add(orderRequest);
                        }
                        adapter.notifyDataSetChanged();
                        ready=true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void init() {
        ready=false;
        list=new ArrayList<>();
        imageView=findViewById(R.id.productImage);
        recyclerView=findViewById(R.id.OrderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new OrderListAdapter(list,this);
        recyclerView.setAdapter(adapter);

        name=findViewById(R.id.ProductName);
        price=findViewById(R.id.ProductPrice);
        quantity=findViewById(R.id.ProductAmount);

        edit=findViewById(R.id.editProduct);
        remove=findViewById(R.id.removeProduct);


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
        OrderRequest orderRequest=list.get(position);
        int rem=Integer.valueOf(product.getQuality())-Integer.valueOf(orderRequest.getAmount());
        if(rem<0)
        {
            Toast.makeText(OrderList.this,"U seems to be sort of Supply",Toast.LENGTH_LONG).show();
            return;
        }
        if(orderRequest.getDeliverPrice().equalsIgnoreCase("N/A"))
        {
            mRef.child("Requests").child(id).child(orderRequest.getKey()).child("status").setValue("Accepted");
        }
        else
        {
            mRef.child("Requests").child(id).child(orderRequest.getKey()).child("status").setValue("Out For Delivery");
        }
        product.setQuality(String.valueOf(rem));
        setData();
        mRef.child("Market").child(product.getProductName()).child(String.valueOf(System.currentTimeMillis())).setValue(orderRequest.getProductPrice());
        mRef.child("Products").child(id).child("quality").setValue(String.valueOf(rem));
        mRef.child("Products").child(id).child("pendingRequests").setValue(product.getPendingRequests()-1);
    }

    @Override
    public void decline(int position) {
        mRef.child("Requests").child(id).child(list.get(position).getKey()).child("status").setValue("Cancel");
        mRef.child("Products").child(id).child("pendingRequests").setValue(product.getPendingRequests()-1);
    }

    @Override
    public void onClick(View view) {
        if(!ready){
            Toast.makeText(OrderList.this,"Fetching Data...",Toast.LENGTH_LONG).show();
            return;
        }
        if(view==remove){
            final int[] count = {list.size()};

            for (OrderRequest orderRequest:list)
            {
                mRef.child("Requests").child(id).child(orderRequest.getKey()).child("status").setValue("Out of Stock").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        count[0]--;
                        if(count[0]==0)
                        {
                            removeProduct();
                        }
                    }
                });
            }
            if(list.size()==0)
            {
                removeProduct();
            }
            Toast.makeText(OrderList.this,"Remove",Toast.LENGTH_LONG).show();
        }else if(view==edit){
            openDialog();
            Toast.makeText(OrderList.this,"Edit",Toast.LENGTH_LONG).show();
        }
    }

    private void openDialog() {
        EditProductDialog editProductDialog=new EditProductDialog();
        editProductDialog.show(getSupportFragmentManager(),"Edit Product Dialog");

    }

    private void removeProduct() {
        mRef.child("Products").child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(OrderList.this,"Product Removed",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void applyEdits(String quantity, String price, String deliverPrice) {
        product.setQuality(quantity);
        product.setPrice(price);
        product.setDelivery(deliverPrice);
        mRef.child("Products").child(id).setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setData();
            }
        });
    }
}
