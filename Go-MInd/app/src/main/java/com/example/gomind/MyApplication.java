package com.example.gomind;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
//import com.example.gomind.PaymentsModule;
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //PaymentsModule.install(this);


    }
}
