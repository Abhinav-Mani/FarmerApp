package com.androlord.farmerapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androlord.farmerapp.MainActivity;
import com.androlord.farmerapp.Models.Products;
import com.androlord.farmerapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class FarmersListAdapter extends RecyclerView.Adapter<FarmersListAdapter.MyViewHolder>{
    ArrayList<Products> list;
    Context context;
    ClickHandler handler;

    public static interface ClickHandler{
        public void ItemSelected(int position, ImageView imageView);
    }
    public FarmersListAdapter(ArrayList<Products> list, MainActivity activity) {
        this.list = list;
        this.context = activity.getBaseContext();
        this.handler=activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_farmer_product,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Products products=list.get(position);
        holder.productName.setText(products.getProductName());
        holder.price.setText("₹"+products.getPrice());
        holder.quantity.setText(products.getQuality()+"Kg");
        holder.deliveryCharge.setText(products.getDelivery());
        holder.requestCountHolder.setVisibility(View.GONE);

        if(products.getPendingRequests()!=0){
            holder.requestCountHolder.setVisibility(View.VISIBLE);
            holder.pendingOrders.setText(products.getPendingRequests()+"");
        }
        if(!list.get(position).getDelivery().equalsIgnoreCase("N/A"))
        holder.deliveryCharge.setText("₹"+list.get(position).getDelivery());
        Glide.with(context).asBitmap().
                load(list.get(position).getImg()).
                fitCenter().
                error(R.drawable.vegi).
                fallback(R.drawable.vegi).
                placeholder(R.drawable.vegi).
                diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.imageView);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.ItemSelected(position, holder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder {
        CardView item;
        ImageView imageView;
        TextView productName,price,quantity,deliveryCharge,pendingOrders;
        RelativeLayout requestCountHolder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.productImageSingle);
            productName=itemView.findViewById(R.id.productNameSingle);
            item=itemView.findViewById(R.id.SingleItem);
            price=itemView.findViewById(R.id.price);
            quantity=itemView.findViewById(R.id.quantity);
            deliveryCharge=itemView.findViewById(R.id.deliveryCharge);
            pendingOrders=itemView.findViewById(R.id.pendingRequests);
            requestCountHolder=itemView.findViewById(R.id.pendingRequestsCountHolder);
        }

    }
}
