package com.androlord.farmerapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.androlord.farmerapp.R;

public class EditProductDialog extends AppCompatDialogFragment {
    EditText quantity,price,deliveryPrice;
    String Quantity,Price,DeliveryPrice;
    DialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_product_fragment_dialog, null);
        quantity=view.findViewById(R.id.productAmountEdit);
        price=view.findViewById(R.id.productPriceEdit);
        deliveryPrice=view.findViewById(R.id.productDeliveryChargeEdit);
        builder.setView(view)
                .setTitle("Edit Product")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Quantity=quantity.getText().toString().trim();
                Price=price.getText().toString().trim();
                DeliveryPrice=deliveryPrice.getText().toString().trim();
                listener.applyEdits(Quantity,Price,DeliveryPrice);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener=(DialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }

    }

    public interface DialogListener {
        public void applyEdits(String quantity,String price,String deliverPrice);
    }
}
