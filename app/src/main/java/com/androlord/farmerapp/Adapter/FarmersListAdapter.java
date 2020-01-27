package com.androlord.farmerapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        public void ItemSelected(int position);
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView.setText(list.get(position).getProductName());
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
                handler.ItemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("ak47", "getItemCount: "+list.size());
        return list.size();
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder {
        CardView item;
        ImageView imageView;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.productImageSingle);
            textView=itemView.findViewById(R.id.product_name);
            item=itemView.findViewById(R.id.SingleItem);
        }
    }
}
