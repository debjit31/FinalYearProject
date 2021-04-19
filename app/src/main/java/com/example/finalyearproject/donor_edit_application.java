package com.example.finalyearproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class donor_edit_application extends AppCompatActivity {

    private Button mEditApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_edit_application);

        mEditApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do the necessary
            }
        });


    }
}