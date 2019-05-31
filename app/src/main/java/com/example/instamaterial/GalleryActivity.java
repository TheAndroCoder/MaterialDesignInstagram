package com.example.instamaterial;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.instamaterial.Adapters.ViewPagerAdapter;
import com.example.instamaterial.Utilities.Utils;

public class GalleryActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewpager;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Log.d("sachin","Coming into gallery activity");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1001);
        }
        setupXmlViews();
        startIntroAnimations();
        setupViewPager();
    }
    private void startIntroAnimations(){
        fab.setTranslationY(Utils.dpToPx(80));
        fab.animate().translationY(0).setStartDelay(1000).setDuration(300).setInterpolator(new OvershootInterpolator()).start();
        tabLayout.setTranslationY(-Utils.dpToPx(60));
        tabLayout.setAlpha(0.f);
        tabLayout.animate().translationY(0.f).alpha(1.f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
    }
    private void setupXmlViews(){
        tabLayout=findViewById(R.id.tablayout);
        viewpager=findViewById(R.id.viewpager);
        fab=findViewById(R.id.fab);
    }
    private void setupViewPager(){
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragments(new GalleryFragment(),"GALLERY");
        adapter.addFragments(new CameraFragment(),"CAMERA");
        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);
    }
}
