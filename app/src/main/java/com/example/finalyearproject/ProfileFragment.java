package com.example.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private Button logout;
    private FirebaseAuth mAuth;
    private DatabaseReference mdb;
    private String UID;
    private TextView uname, uemail, uphoneNumber;
    private ProgressBar progressBar;
    private ImageView imageView;
    private Button upload;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Profile");

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        uname = v.findViewById(R.id.userName);
        uemail = v.findViewById(R.id.userEmailTV);
        uphoneNumber = v.findViewById(R.id.userPhoneTV);
        progressBar = v.findViewById(R.id.progress_circular);
        imageView = v.findViewById(R.id.profilePicture);
        upload = v.findViewById(R.id.uploadButton);
        
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Photo Added!!!", Toast.LENGTH_SHORT).show();
            }
        });
                
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload.setVisibility(View.VISIBLE);
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        mdb =  FirebaseDatabase.getInstance().getReference().child("users");

        mdb.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    uname.setText(snapshot.child("name").getValue().toString());
                    uemail.setText(snapshot.child("email").getValue().toString());
                    progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        logout = (Button) v.findViewById(R.id.signoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        return v;
    }
    void logoutUser()
    {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), login.class));
    }
}
