package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class donor_results extends AppCompatActivity {

    private RecyclerView donorResults;
    private DatabaseReference mdb;
    private ProgressBar mProgressBar;
    private String mBloodGrp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_results);

        mBloodGrp = getIntent().getStringExtra("bloodgrp");
        Log.i("blood", mBloodGrp);
        final ProgressDialog loading = ProgressDialog.show(this,"Loading","Please wait");


        donorResults = findViewById(R.id.recyclerView);
        mdb = FirebaseDatabase.getInstance().getReference().child("donors").child(mBloodGrp);

        donorResults.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        donorResults.setLayoutManager(linearLayoutManager);

        displayDetails();
        loading.dismiss();
    }
    private void displayDetails() {

        FirebaseRecyclerAdapter<Donor, DonorViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Donor, DonorViewHolder>
                (
                        Donor.class,
                        R.layout.donor_layout,
                        DonorViewHolder.class,
                        mdb
                ){
            @Override
            protected void populateViewHolder(DonorViewHolder donorViewHolder, Donor donor, int i) {
                donorViewHolder.setName(donor.getName());
                donorViewHolder.setContact(donor.getContact());
                donorViewHolder.setAge(donor.getAge());
            }
        };
        donorResults.setAdapter(firebaseRecyclerAdapter);
    }
    public static class DonorViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public DonorViewHolder(@NonNull View itemView) {
            super(itemView);
            mView  = itemView;
        }
        public void setName(String name) {
            TextView mName = (TextView) mView.findViewById(R.id.name_layout);
            mName.setText(name);
        }

        public void setContact(String contact) {
            TextView mContact = (TextView) mView.findViewById(R.id.contact_layout);
            mContact.setText(contact);
        }

        public void setAge(String age) {
            TextView mAge = (TextView) mView.findViewById(R.id.age_layout);
            mAge.setText(age);
        }
    }
}