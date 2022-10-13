package com.hcmut.admin.utrafficsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.ui.signin.SignInActivity;

public class DisclosureActivity extends AppCompatActivity {

    private TextView txtNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclosure);

        txtNext = (TextView) findViewById(R.id.txtNext);
        final Intent intent = new Intent(this, SignInActivity.class);

        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });
    }
}