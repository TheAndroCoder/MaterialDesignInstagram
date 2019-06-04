package com.example.instamaterial;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.instamaterial.Adapters.PhotosAdapter;
import com.example.instamaterial.Adapters.ViewPagerAdapter;
import com.example.instamaterial.Utilities.Utils;

import java.io.File;
import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewpager;
    private FloatingActionButton camera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        //Log.d("sachin","Coming into gallery activity");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1001);
        }
        setupXmlViews();
        startIntroAnimations();
        setupViewPager();
    }
    private void startIntroAnimations(){
        //fab.setTranslationY(Utils.dpToPx(80));
        //fab.animate().translationY(0).setStartDelay(2000).setDuration(300).setInterpolator(new OvershootInterpolator()).start();
        camera.setTranslationY(Utils.dpToPx(80));
        camera.animate().translationY(0).setStartDelay(2000).setDuration(300).setInterpolator(new OvershootInterpolator()).start();
        tabLayout.setTranslationY(-Utils.dpToPx(60));
        tabLayout.setAlpha(0.f);
        tabLayout.animate().translationY(0.f).alpha(1.f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
    }
    private void setupXmlViews(){
        tabLayout=findViewById(R.id.tablayout);
        viewpager=findViewById(R.id.viewpager);
        //fab=findViewById(R.id.fab);
        camera=findViewById(R.id.camera);
        //setup fab click listeners
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tabLayout.getSelectedTabPosition()==0){
                    //Open Camera for still photo capture
                    Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,1002);
                }else{
                    //open video camera for video capture
                    Intent videoIntent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(videoIntent,1003);
                }
            }
        });

    }
    private void setupViewPager(){
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragments(new GalleryFragment(),"PHOTO");
        adapter.addFragments(new VideoFragment(),"VIDEO");
        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);
        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("PHOTO")){
                    camera.setImageResource(R.drawable.ic_action_camera);
                }else{
                    camera.setImageResource(R.drawable.ic_action_video);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1002 && resultCode== Activity.RESULT_OK){
            Bitmap photo=(Bitmap)data.getExtras().get("data");
            //Take to next Activity
            Bundle bundle=new Bundle();
            bundle.putString("from","gallery_activity");
            bundle.putString("type","image");
            bundle.putParcelable("data",photo);
            Intent intent = new Intent(GalleryActivity.this,PostActivity.class);
            intent.putExtra("bundle",bundle);
            startActivity(intent);
        }
        else if(requestCode==1003 && resultCode==Activity.RESULT_OK){
            Uri videoUri=data.getData();
            Bundle bundle=new Bundle();
            bundle.putString("type","video");
            bundle.putString("from","gallery_activity");
            bundle.putParcelable("uri",videoUri);
            Intent intent = new Intent(GalleryActivity.this,PostActivity.class);
            intent.putExtra("bundle",bundle);
            startActivity(intent);

        }
    }



    private Bitmap getBitmap(String uri){
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.fromFile(new File(uri)));
        } catch (IOException e) {
            Log.d("sachin","into exp "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }



}
