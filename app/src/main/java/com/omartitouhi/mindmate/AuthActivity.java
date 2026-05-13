package com.omartitouhi.mindmate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.omartitouhi.mindmate.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
