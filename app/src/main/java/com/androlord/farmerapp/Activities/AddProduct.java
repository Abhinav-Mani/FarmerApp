package com.androlord.farmerapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.androlord.farmerapp.Models.Products;
import com.androlord.farmerapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;

public class AddProduct extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText price,quantity,DeliveryCharge;
    CheckBox isSelfDelivery;
    Spinner mode,product;
    Button submit;
    ImageView imageView;
    int clickedImage=0;
    private StorageReference mStorageRef;
    DatabaseReference mref;
    String ProductName,Price,Quantity,deliveyCharge="N/A",DeliverMode,ImageUrl,PhoneNo,ModeOFContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        init();

        setListners();
    }

    private void setListners() {
        isSelfDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isSelfDelivery.isChecked()){
                    DeliveryCharge.setVisibility(View.VISIBLE);
                }
                else
                {
                    DeliveryCharge.setVisibility(View.GONE);
                }
            }
        });
        imageView.setOnClickListener(this);
        submit.setOnClickListener(this);
        mode.setOnItemSelectedListener(this);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null&& requestCode==0) {
            Bitmap b = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(b);
            clickedImage=1;
        }
    }

    private void init() {
        product=findViewById(R.id.productName);
        price=findViewById(R.id.productPrice);
        quantity=findViewById(R.id.productQuantity);
        DeliveryCharge=findViewById(R.id.productDeliveryCharge);
        isSelfDelivery=findViewById(R.id.selfDeliveryCheck);
        mode=findViewById(R.id.contactMode);
        submit=findViewById(R.id.submit);
        imageView=findViewById(R.id.imageofproduct);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mref= FirebaseDatabase.getInstance().getReference().child("Products");
    }
    public void push(Products p)
    {
        mref.push().setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddProduct.this,"Product Added",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view==imageView){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!=null)
            {
                startActivityForResult(intent,0);

            }
        }else if(view==submit){
            if( validate()){
                upload();
            }
        }
    }

    private boolean validate() {
        boolean val=true;

        if(TextUtils.isEmpty(price.getText().toString().trim())) {
            val=false;
            Toast.makeText(this,"Price Cannot be empty",Toast.LENGTH_LONG).show();

        }
        if(TextUtils.isEmpty(quantity.getText().toString().trim())) {
            val=false;
            Toast.makeText(this,"Quantity Cannot be empty",Toast.LENGTH_LONG).show();

        }
        if(TextUtils.isEmpty(DeliveryCharge.getText().toString().trim())&&isSelfDelivery.isChecked()) {
            val=false;
            Toast.makeText(this,"Must set the deliver Charge",Toast.LENGTH_LONG).show();
        }

        return val;
    }

    private void upload() {
        Price = price.getText().toString().trim();
        Quantity=quantity.getText().toString().trim();
        if(isSelfDelivery.isChecked())
            deliveyCharge=DeliveryCharge.getText().toString().trim();
        else
            deliveyCharge="N/A";
        PhoneNo=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final Products products=new Products(ProductName,Price,"",Quantity,deliveyCharge,PhoneNo,ModeOFContact);
        if(clickedImage==0){
            push(products);
        }
        else {
            Bitmap mainImage=((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mainImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask mainImageUpload=mStorageRef.child("Product"+PhoneNo+ProductName).putBytes(data);
            mainImageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStorageRef.child("Product"+PhoneNo+ProductName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageUrl=String.valueOf(uri);
                            products.setImg(ImageUrl);
                            push(products);
                        }
                    });
                }
            });

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Price = ProductName;
        ProductName = getResources().getStringArray(R.array.Products)[i];
        ModeOFContact = getResources().getStringArray(R.array.contactMode)[i];
        Log.d("ak47", "onItemSelected: "+i+" "+ModeOFContact);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
