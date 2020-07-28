package com.example.finalyearproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button logout;
    private FirebaseAuth mAuth;
    private DatabaseReference mdb;
    private String UID;
    private TextView uname, uemail, uphoneNumber;
    private ProgressBar progressBar;
    private CircleImageView profilePicture;
    private Button upload, choose;
    private ImageView userPhoneupdate;
    private ImageView userEmailupdate;
    private Uri imageUri;

    private StorageReference mStorageRef;

    private StorageTask mUploadTask;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Profile");

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        uname = v.findViewById(R.id.userName);
        uemail = v.findViewById(R.id.userEmailTV);
        uphoneNumber = v.findViewById(R.id.userPhoneTV);
        progressBar = v.findViewById(R.id.progress_circular);
        profilePicture = v.findViewById(R.id.profilePicture);
        upload = v.findViewById(R.id.uploadButton);
        choose = v.findViewById(R.id.chooseImage);
        userPhoneupdate = v.findViewById(R.id.phoneImage);
        userEmailupdate = v.findViewById(R.id.emailImage);


        userPhoneupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Photo Added!!!", Toast.LENGTH_SHORT).show();
                if(mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(getActivity(), "Upload in Progress!!!!!", Toast.LENGTH_SHORT).show();
                }else{
                    uploadImage();
                }
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload.setVisibility(View.VISIBLE);
                choose.setVisibility(View.VISIBLE);
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        mdb = FirebaseDatabase.getInstance().getReference().child("users");
        mStorageRef = FirebaseStorage.getInstance().getReference("DP");

        mdb.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uname.setText(snapshot.child("name").getValue().toString());
                uemail.setText(snapshot.child("email").getValue().toString());
                if(snapshot.child("profilepicture").exists()){
                    Picasso.get().load(snapshot.child("profilepicture").getValue().toString()).into(profilePicture);
                }
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

    // getting file extension from an image
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    // uploading to Firebase storage and storing imageuri to firebase realtime database
    public void uploadImage() {
        if (imageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));


            mUploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Upload upload = new Upload("Profile Image", uri.toString());
                            mdb.child(UID).child(upload.getmName()).setValue(upload.getmImageURl());
                            Toast.makeText(getActivity(), "Upload Successfull", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_LONG).show();
        }

    }

    // method to choose image from internal storage
    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profilePicture);
        }
    }

    public void openDialog() {

    }

    void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), login.class));
    }
}
