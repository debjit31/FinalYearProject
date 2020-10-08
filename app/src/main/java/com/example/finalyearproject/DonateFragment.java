package com.example.finalyearproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class DonateFragment extends Fragment {

    private Button upload;
    private StorageReference mStorage;
    private StorageTask mUploadTask;
    private FirebaseAuth mAuth;
    private DatabaseReference mdb;
    private Button imageSelectButton;
    private ImageView myImage;
    private TextView userName;
    private String productRandomKey, downloadImageUrl;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private EditText mAge, mBloodgGrp, mContact;
    private String fileUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_donate, container, false);
        getActivity().setTitle("Donate");
        mAuth = FirebaseAuth.getInstance();
        utility();
        mAge = root.findViewById(R.id.ageTV);
        mBloodgGrp = root.findViewById(R.id.bgrpTV);
        mContact = root.findViewById(R.id.contactEd);
        upload = root.findViewById(R.id.upload);
        myImage = root.findViewById(R.id.selectedImage);
        userName = root.findViewById(R.id.nameTV);
        imageSelectButton = root.findViewById(R.id.selectImageButtn);
        mStorage = FirebaseStorage.getInstance().getReference().child("Medical Reports").child(mAuth.getCurrentUser().getUid());
        imageSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_from_gallery();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_to_storage();
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
                            fileUrl = uri.toString();
                            Toast.makeText(getActivity(), "Image Link Generated...", Toast.LENGTH_SHORT);
                            register_donor(userName.getText().toString(), mAge.getText().toString(), mBloodgGrp.getText().toString(), mContact.getText().toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    public void select_from_gallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            myImage.setImageURI(ImageUri);

        }
    }

    public String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

    }

    public void register_donor(String name, String age, String bgrp, String contactNo) {
        if (Integer.parseInt(age) >= 18) {
            String currUser = mAuth.getCurrentUser().getUid();
            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("donors").child(bgrp);
            HashMap<String, String> donorRequestMap = new HashMap<String, String>();
            donorRequestMap.put("name", name);
            donorRequestMap.put("age", age);
            donorRequestMap.put("medical_report", fileUrl);
            donorRequestMap.put("blood_grp", bgrp);
            donorRequestMap.put("contact", contactNo);
            donorRequestMap.put("status", "Under Review");
            db1.child(currUser).setValue(donorRequestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Donor Request Filed Successfully!!!", Toast.LENGTH_SHORT).show();
                        userName.setText("");
                        mAge.setText("");
                        mBloodgGrp.setText("");
                        mContact.setText("");
                    }
                }
            });
            startActivity(new Intent(getActivity(), donor_status.class));
        } else {
            Toast.makeText(getActivity(), "Details are not right!!!", Toast.LENGTH_SHORT).show();
        }


    }

    public void utility() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        mdb = FirebaseDatabase.getInstance().getReference().child("users");
        mdb.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName.setText(snapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
