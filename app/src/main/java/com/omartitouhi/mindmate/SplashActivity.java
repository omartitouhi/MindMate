package com.omartitouhi.mindmate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.omartitouhi.mindmate.databinding.ActivitySplashBinding;
import com.omartitouhi.mindmate.ui.auth.AuthViewModel;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.splashAnimation.setAlpha(0f);
        binding.splashAnimation.setScaleX(0.94f);
        binding.splashAnimation.setScaleY(0.94f);
        binding.splashAnimation.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(450)
                .start();

        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        Class<?> destination = authViewModel.isUserLoggedIn() ? MainActivity.class : AuthActivity.class;
        startActivity(new Intent(this, destination));
        finish();
    }
}
