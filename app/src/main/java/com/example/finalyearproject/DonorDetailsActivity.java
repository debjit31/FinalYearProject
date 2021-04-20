package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.Manifest.permission.CALL_PHONE;

public class DonorDetailsActivity extends AppCompatActivity {

    private String user_id, mBloodGrp;
    private DatabaseReference dbRef;
    private TextView mName, mAge, mBgrp, mAddress, mlandmark, mPhone;
    private Button mCall;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_details);

        user_id = getIntent().getExtras().get("userId").toString();
        mBloodGrp = getIntent().getExtras().get("bloodGrp").toString();
//        Log.i("userId", user_id);
//        Toast.makeText(this, user_id, Toast.LENGTH_LONG).show();

        mName = findViewById(R.id.nameField);
        mAge = findViewById(R.id.ageField);
        mBgrp = findViewById(R.id.bloodGrpField);
        mAddress = findViewById(R.id.addressField);
        mlandmark = findViewById(R.id.landmarkField);
        mPhone = findViewById(R.id.contactField);
        mCall = findViewById(R.id.callBtn);
        mProgressBar = findViewById(R.id.progress_circular);
        mProgressBar.setVisibility(View.VISIBLE);

        dbRef = FirebaseDatabase.getInstance().getReference().child("donors").child(mBloodGrp);
        dbRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("name").exists()){
                    mName.setText(snapshot.child("name").getValue().toString());
                }else{
                    mName.setText("null");
                }
                if(snapshot.child("age").exists()){
                    mAge.setText(snapshot.child("age").getValue().toString());
                }else{
                    mAge.setText("null");
                }
                if(snapshot.child("blood_grp").exists()){
                    mBgrp.setText(snapshot.child("blood_grp").getValue().toString());
                }else{
                    mBgrp.setText("null");
                }
                if(snapshot.child("address").exists()){
                    mAddress.setText(snapshot.child("address").getValue().toString());
                }else{
                    mAddress.setText("null");
                }
                if(snapshot.child("landmark").exists()){
                    mlandmark.setText(snapshot.child("landmark").getValue().toString());
                }else{
                    mlandmark.setText("null");
                }
                if(snapshot.child("contact").exists()){
                    mPhone.setText(snapshot.child("contact").getValue().toString());
                }else{
                    mPhone.setText("null");
                }
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String phone_number = mPhone.getText().toString();
                callIntent.setData(Uri.parse("tel:"+phone_number));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    ActivityCompat.requestPermissions(DonorDetailsActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);
                    startActivity(callIntent);
                }
            }
        });
    }
}