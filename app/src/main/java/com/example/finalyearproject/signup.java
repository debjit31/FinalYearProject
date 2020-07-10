package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.lang.Character;

public class signup extends AppCompatActivity {

    private Button signUp;
    private EditText email, password, name;
    private TextView loginLink;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String mName, mEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.emailIN);
        password = (EditText) findViewById(R.id.passIN);
        name = (EditText) findViewById(R.id.username);


        progressBar = findViewById(R.id.progessBar);

        loginLink = (TextView) findViewById(R.id.loginLink);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(signup.this, login.class));
            }
        });

        signUp = (Button) findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    boolean passWordChecker(String pass) {
        //String pass = password.getText().toString().trim();
        boolean foundLetter = false, foundNumber = false;
        for (char c : pass.toCharArray()) {
            if (Character.isLetter(c)) {
                foundLetter = true;
            } else if (Character.isDigit(c))
                foundNumber = true;
        }
        if (foundLetter == true && foundNumber == true) {
            return true;
        } else {
            return false;
        }
    }

    void createAccount() {
        progressBar.setVisibility(View.VISIBLE);
        mEmail = email.getText().toString().trim();
        mPassword = password.getText().toString().trim();
        mName = name.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword) || TextUtils.isEmpty(mName)) {
            Toast.makeText(this, "Email or password or name cannot be empty!!!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            email.setText("");
            name.setText("");
            password.setText("");

        } else if (!passWordChecker(mPassword)) {
            Toast.makeText(this, "wrong password format", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            email.setText("");
            name.setText("");
            password.setText("");
        } else {
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);

                        String UID = mAuth.getCurrentUser().getUid();
                        Intent intent = new Intent(signup.this, Home.class);
                        intent.putExtra("UID", UID);
                        intent.putExtra("Name", mName);
                        intent.putExtra("Email", mEmail);
                        startActivity(intent);
                        //Toast.makeText(signup.this, "Welcome!!", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        name.setText("");
                        email.setText("");
                        password.setText("");
                        Toast.makeText(signup.this, "Failed to create user :- " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
}