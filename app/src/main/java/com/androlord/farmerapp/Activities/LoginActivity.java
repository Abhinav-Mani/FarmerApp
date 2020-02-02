package com.androlord.farmerapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androlord.farmerapp.MainActivity;
import com.androlord.farmerapp.Models.Farmer;
import com.androlord.farmerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mobileno,verifycode,name, address;
    Button submit,verify;
    String number,VerificationId,UID,DisplayName,Displayaddress;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intit();

    }

    private void intit() {
        mobileno=findViewById(R.id.phoneno);
        verifycode=findViewById(R.id.verficatioCode);
        submit=findViewById(R.id.loginsubmit);
        name=findViewById(R.id.name);

        address =findViewById(R.id.address);
        verify=findViewById(R.id.verify);
        submit.setOnClickListener(this);
        verify.setOnClickListener(this);

        UID=verifycode.getText().toString().trim();
        mAuth=FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view==submit) {
            if(TextUtils.isEmpty(name.getText().toString().trim())) {
                Toast.makeText(LoginActivity.this,"Name cannot Be Empty",Toast.LENGTH_LONG).show();
            }else if(TextUtils.isEmpty(address.getText().toString().trim())){
                Toast.makeText(LoginActivity.this,"Address cannot Be Empty",Toast.LENGTH_LONG).show();
            }else {
                Displayaddress=address.getText().toString().trim();
                DisplayName=name.getText().toString().trim();
                number="+91"+mobileno.getText().toString().trim();
                sendVerificationCode(number);
            }
        }else if(view==verify){
            String code=verifycode.getText().toString().trim();
            verifyCode(code);
        }
    }
    private void sendVerificationCode(String number){
        Log.d("ak47", "sendVerificationCode: ");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,             // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }
    private void verifyCode(String code){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(VerificationId,code);
        signInWithCredentials(credential);
    }

    private void signInWithCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Farmer farmer=new Farmer(mAuth.getCurrentUser().getPhoneNumber().toString(),DisplayName,Displayaddress);
                    intent.putExtra ("Farmer",farmer);
                    startActivity(intent);
                }
            }
        });

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            VerificationId=s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code!=null)
            {
                verifycode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this,"Something Went Wrong"+e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };
}
