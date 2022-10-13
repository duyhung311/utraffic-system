package com.hcmut.admin.utrafficsystem.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hcmut.admin.utrafficsystem.ui.signin.SignInActivity;

import java.util.Calendar;

public class SplashActivity extends AppCompatActivity {
    public static long time;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        time = Calendar.getInstance().getTimeInMillis();
        super.onCreate(savedInstanceState);
        boolean background = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean foreground = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        Intent disclosureIntent = new Intent(this, DisclosureActivity.class);
        Intent signInIntent = new Intent(this, SignInActivity.class);

        Intent intent = foreground ? signInIntent : disclosureIntent;
        startActivity(intent);
        finish();
    }
}
