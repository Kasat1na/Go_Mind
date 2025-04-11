package com.example.gomind.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.fragments.GreetingFragment;
import com.example.gomind.fragments.LoginFragment;
import com.example.gomind.fragments.RegisterFragment;
import com.example.gomind.sound.SoundManager;

public class AuthenticationActivity extends AppCompatActivity {

    Fragment fragment;
    FragmentManager fragmentManager;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        fragmentManager = getSupportFragmentManager();
        // Инициализация SoundManager
        soundManager = SoundManager.getInstance(this);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            // Если пользователь уже авторизован, сразу в `MainActivity`
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Если не авторизован, показываем `LoginFragment`
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.authentication_container, new GreetingFragment())
                    .commit();
        }
    }
    /////

    public void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.authentication_container, fragment)
                .commit();
    }

    public void onClick(View view) {
        soundManager.playSound();
        int id = view.getId();
        if (id == R.id.login_button) {
            replaceFragment(new LoginFragment());
        } else if (id == R.id.enter_btn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.register_button) {
            replaceFragment(new RegisterFragment());
        }
    }
}