package com.example.instamaterial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class ProfileActivity extends AppCompatActivity {
    private FloatingActionButton settings_fab;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupXmlViews();
        startIntroAnimation();
    }
    private void setupXmlViews(){
        settings_fab=findViewById(R.id.settings_fab);


        //set on click listeners here
        settings_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,SettingsActivity.class));
            }
        });
    }
    private void startIntroAnimation(){
        settings_fab.setScaleX(0);
        settings_fab.setScaleY(0);
        settings_fab.animate().setStartDelay(1000).setDuration(300).scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).start();
    }
}
