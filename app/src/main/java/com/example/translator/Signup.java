package com.example.translator;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Signup extends AppCompatActivity {

    private EditText phone, otp, code;
    private String verificationid, phonenumber;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private ProgressDialog pd;
    DatabaseReference reference;
    EditText password;
    Spinner lang;
    Button getotp;
    Button btnMorph;
    static boolean b = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        otp = findViewById(R.id.otp);
        code = findViewById(R.id.code);
        //(main)progressBar=findViewById(R.id.progressbar);
        phone = findViewById(R.id.phone);
        getotp = findViewById(R.id.getotp);
        // pd=new ProgressDialog(signexp.this);


        btnMorph = findViewById(R.id.sign);
        btnMorph.setEnabled(true);

        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//(main)                progressBar.setVisibility(View.VISIBLE);
                //  animatedCircleLoadingView.setVisibility(View.VISIBLE);
//                animatedCircleLoadingView.resetLoading();


                String pcode = code.getText().toString();

                String number = phone.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    phone.setError("Valid number is required");
                    phone.requestFocus();
//                    animatedCircleLoadingView.stopFailure();
                    return;
                }
//                progressBar.setVisibility(View.VISIBLE);
                phonenumber = pcode + number;
                mAuth = FirebaseAuth.getInstance();
//                animatedCircleLoadingView.setPercent(60);

                sendVerificationCode(phonenumber);
////                Intent intent = new Intent(Signup.this, login.class);
////                intent.putExtra("phonenumber", phonenumber);
////                startActivity(intent);
            }
        });
        btnMorph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             Intent intent = new Intent(Signup.this,MainActivity.class);
             startActivity(intent);
            }
        });
    }

    //
    private void verifyCode(String code) {
        Toast.makeText(this, "being", Toast.LENGTH_LONG).show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }

    //
    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Toast.makeText(Signup.this,"ans:"+an,Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(Signup.this, signDetails.class);
//                            intent.putExtra("Phone", phonenumber);
////                            intent.putExtra("Code",code.getText().toString());
//                            startActivity(intent);

                        } else {
                            Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }
    //

    //


    private void sendVerificationCode(String number) {
//
      //  animatedCircleLoadingView.setPercent(70);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    //
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
           // animatedCircleLoadingView.setPercent(100);

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            //   Toast.makeText(Signup.this,"ph:"+verificationid+"     code:"+code,Toast.LENGTH_LONG).show();
            // Toast.makeText(Signup.this,"22ph:"+verificationid+"     code:"+code,Toast.LENGTH_LONG).show();

            otp.setText(code);
           // animatedCircleLoadingView.stopOk();
          //  animatedCircleLoadingView.removeAllViews();
          //  animatedCircleLoadingView.setVisibility(View.INVISIBLE);
            //  progressBar.setVisibility(View.INVISIBLE);
            //verifyCode(code);
//            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }
    }
}
