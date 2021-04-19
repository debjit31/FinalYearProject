package com.example.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FindDonorsFragment extends Fragment {

    private Spinner mBloodgGrp;
    private Button mFetch;
    private String bgrp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_finddonors, container, false);
        getActivity().setTitle("Donors");

        mBloodgGrp = root.findViewById(R.id.bgrpTV);
        mFetch = root.findViewById(R.id.fetchDonorsBtn);
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
        mFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.i("Blood Grp", bgrp);
                Intent intent = new Intent(getContext(), donor_results.class);
                intent.putExtra("bloodgrp", bgrp);
                startActivity(intent);
            }
        });


        return root;
    }
}
