package com.example.cs65_final_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cs65_final_project.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");
    }
}