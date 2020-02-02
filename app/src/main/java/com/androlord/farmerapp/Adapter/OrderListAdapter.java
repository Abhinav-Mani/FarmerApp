package com.androlord.farmerapp.Adapter;

import android.graphics.Color;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androlord.farmerapp.Activities.OrderList;
import com.androlord.farmerapp.Models.OrderRequest;
import com.androlord.farmerapp.Models.Products;
import com.androlord.farmerapp.R;

import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {
    ArrayList<OrderRequest> list;
    OrderList orderList;
    ClickHandler clickHandler;
    public static interface ClickHandler{
        public void call(int position);
        public void accept(int position);
        public void decline(int position);
    }
    public OrderListAdapter(ArrayList<OrderRequest> list, OrderList orderList) {
        this.list = list;
        this.clickHandler = orderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_order,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        OrderRequest orderRequest=list.get(position);
        holder.product.setText ("Price:"+"₹"+list.get(position).getProductPrice());
        holder.delivery.setText("Delivery Price:"+"₹"+list.get(position).getDeliverPrice());
        if(list.get(position).getDeliverPrice().equalsIgnoreCase("N/A"))
        holder.delivery.setText("Delivery Price:"+list.get(position).getDeliverPrice());
        holder.quantity.setText("Quantity:"+list.get(position).getAmount()+"Kg");
        if(!list.get(position).getStatus().equalsIgnoreCase("Pending..."))
        {
            holder.linearLayout.setBackgroundColor(Color.GREEN);
            holder.accept.setEnabled(false);
            holder.decline.setEnabled(false);
        }
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHandler.call(position);
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHandler.accept(position);

            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHandler.decline(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView quantity,product,delivery;
        Button call,accept,decline;
        LinearLayout linearLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quantity=itemView.findViewById(R.id.quantity);
            product=itemView.findViewById(R.id.productPay);
            delivery=itemView.findViewById(R.id.deliverPay);
            call=itemView.findViewById(R.id.callCustomer);
            accept=itemView.findViewById(R.id.acceptCustomer);
            decline=itemView.findViewById(R.id.declineCustomer);
            linearLayout=itemView.findViewById(R.id.orderItem);

        }
    }
}
