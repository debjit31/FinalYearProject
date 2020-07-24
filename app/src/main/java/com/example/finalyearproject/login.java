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

public class login extends AppCompatActivity {

    private Button login;
    private TextView signUpLink;
    private EditText email, password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.emailIN);
        password = (EditText) findViewById(R.id.passIN);
        progressBar = (ProgressBar) findViewById(R.id.progessBar);

        login = (Button) findViewById(R.id.loginBtn);

        signUpLink = (TextView) findViewById(R.id.linkSignUp);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, signup.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_user();
            }
        });
    }
    void login_user()
    {
        progressBar.setVisibility(View.VISIBLE);
        String mEmail = email.getText().toString().trim();
        String mPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)) {
            Toast.makeText(this, "Email and password cannot be empty!!!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            mAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(com.example.finalyearproject.login.this, Home.class));
                        Toast.makeText(login.this, "Welcome back!!!", Toast.LENGTH_SHORT).show();
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(login.this, "Failed to Login.\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
}