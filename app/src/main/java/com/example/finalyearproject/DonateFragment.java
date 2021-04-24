package com.example.finalyearproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class DonateFragment extends Fragment {

    private Button submit;
    // Firebase Storage Reference
    private StorageReference mStorage;
    // Storage Upload Task
    private StorageTask mUploadTask;
    private FirebaseAuth mAuth;
    private DatabaseReference mdb;
    private Button imageSelectButton;
    private ProgressBar progressBar;
    private ImageView myImage;
    private TextView userName;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private EditText mAge, mContact, mAddress, mLandmark;
    private Spinner mBloodgGrp;
    private String bgrp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_donate, container, false);
        getActivity().setTitle("Donate");
        mAuth = FirebaseAuth.getInstance();
        mAge = root.findViewById(R.id.ageTV);
        mContact = root.findViewById(R.id.contactEd);
        mBloodgGrp = root.findViewById(R.id.bgrpTV);
        mAddress = root.findViewById(R.id.addressEd);
        mLandmark = root.findViewById(R.id.landmarkEd);

        List<String> bloodgroups = new ArrayList<String>();
        bloodgroups.add("A+");
        bloodgroups.add("B+");
        bloodgroups.add("AB+");
        bloodgroups.add("A-");
        bloodgroups.add("B-");
        bloodgroups.add("AB-");
        bloodgroups.add("O+");
        bloodgroups.add("O-");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, bloodgroups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBloodgGrp.setAdapter(adapter);

        mBloodgGrp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bgrp = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressBar = root.findViewById(R.id.progress_circular);
        // utility function to pre fetch the user name from database
        utility();

        submit = root.findViewById(R.id.upload);
        myImage = root.findViewById(R.id.selectedImage);
        userName = root.findViewById(R.id.nameTV);
        imageSelectButton = root.findViewById(R.id.selectImageButtn);
        mStorage = FirebaseStorage.getInstance().getReference().child("Medical Reports").child(mAuth.getCurrentUser().getUid());
        imageSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // function to select an image from the device internal storage and show it in the imageview
                select_from_gallery();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // function to upload the user basic information and the prescription image to database
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getActivity(), "Already Uploading......", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    add_to_storage();
                }
            }
        });
        return root;
    }

    public void add_to_storage() {
        if (ImageUri != null) {
            final StorageReference fileReference = mStorage.child(System.currentTimeMillis() + "." + getFileExtension(ImageUri));
            mUploadTask = fileReference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String fileUrl = uri.toString();
//                            Toast.makeText(getActivity(), "Image Link Generated...", Toast.LENGTH_SHORT);
                            register_donor(userName.getText().toString(), mAge.getText().toString(), mAddress.getText().toString(), mLandmark.getText().toString(), fileUrl, bgrp, mContact.getText().toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Some Error Occured!!!!", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    // method to select image from gallery
    public void select_from_gallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    // overriden method to load the selected image in the imageview
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            myImage.setImageURI(ImageUri);
        }
    }

    // method to get the image file extension
    public String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

    }

    public void updateUser() {
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        dbRef.child(uid).child("blood_grp").setValue(bgrp);
        dbRef.child(uid).child("contact").setValue(mContact.getText().toString());
        dbRef.child(uid).child("requestFiled").setValue("true");

        DonorStatusFragment donorStatusFragment = new DonorStatusFragment();
        Bundle args = new Bundle();
        args.putString("bloodgrp", bgrp);
        donorStatusFragment.setArguments(args);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, donorStatusFragment).commit();


    }
    public void register_donor(String name, String age, String address, String landmark, String fileUrl, String bgrp, String contactNo) {
        if (Integer.parseInt(age) >= 18) {
            String currUser = mAuth.getCurrentUser().getUid();
            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("donors").child(bgrp);
            HashMap<String, String> donorRequestMap = new HashMap<String, String>();
            donorRequestMap.put("name", name);
            donorRequestMap.put("age", age);
            donorRequestMap.put("medical_report", fileUrl);
            donorRequestMap.put("blood_grp", bgrp);
            donorRequestMap.put("contact", contactNo);
            donorRequestMap.put("address", address);
            donorRequestMap.put("landmark", landmark);
            db1.child(currUser).setValue(donorRequestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        updateUser();
                        userName.setText("");
                        mAge.setText("");
                        mContact.setText("");
                        myImage.setImageResource(R.drawable.sample_image);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Some Error Occurred!!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "You must be at least 18 years", Toast.LENGTH_LONG).show();
        }
    }
    // utility function to prefetch user name from database
    public void utility() {
        progressBar.setVisibility(View.VISIBLE);
        String currentUserId = mAuth.getCurrentUser().getUid();
        mdb = FirebaseDatabase.getInstance().getReference().child("users");
        mdb.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName.setText(snapshot.child("name").getValue().toString());
                progressBar.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
