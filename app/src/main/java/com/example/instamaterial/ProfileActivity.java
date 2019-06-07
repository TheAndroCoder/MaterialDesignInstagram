package com.example.instamaterial;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instamaterial.Adapters.MyPostsRecyclerAdapter;
import com.example.instamaterial.Models.Post;
import com.example.instamaterial.Models.User;
import com.example.instamaterial.Utilities.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private FloatingActionButton settings_fab;
    private TextView edit_profile_btn,username;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private CircleImageView profile_pic;
    private SharedPreferences preferences;
    private RecyclerView recycler;
    private ArrayList<Post> posts;
    private MyPostsRecyclerAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupXmlViews();
        startIntroAnimation();
        //check if it is my profile or someone else's profile
        if(getIntent().getBundleExtra("bundle")!=null){
            //It is someone else's profile
            settings_fab.setImageResource(R.drawable.ic_action_send);
            //TODO : count the number of posts, followers and following also check if already followed or follow request sent or recieved
            edit_profile_btn.setText("SEND FOLLOW REQUEST");
            String uid = getIntent().getBundleExtra("bundle").getString("UID");
            //fetch user from UID and update views
            username.setText(getIntent().getBundleExtra("bundle").getString("USERNAME"));
            Glide.with(this).load(getIntent().getBundleExtra("bundle").getString("DP_URL")).into(profile_pic);
        }else{
            //it is my own profile
            username.setText(preferences.getString("USERNAME",null));
            Glide.with(this).load(preferences.getString("DP_URL",null)).into(profile_pic);
        }
    }
    private void setupXmlViews(){
        settings_fab=findViewById(R.id.settings_fab);
        edit_profile_btn=findViewById(R.id.edit_profile_btn);
        mAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        profile_pic=findViewById(R.id.profile_pic);
        username=findViewById(R.id.username);
        preferences=getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        recycler=findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this,3, LinearLayoutManager.VERTICAL,false));
        posts=new ArrayList<>();
        fetchMyPosts();
        //TODO : set on click listeners here
        settings_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,SettingsActivity.class));
            }
        });
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_profile_btn.getText().equals("EDIT PROFILE")){
                    startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class), ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this,profile_pic,profile_pic.getTransitionName()).toBundle());
                }
            }
        });
    }
    private void startIntroAnimation(){
        settings_fab.setScaleX(0);
        settings_fab.setScaleY(0);
        settings_fab.animate().setStartDelay(1000).setDuration(300).scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).start();
    }

    private void fetchMyPosts(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseHelper helper=DatabaseHelper.getInstance(ProfileActivity.this);
                posts=helper.getMyPosts();
                adapter=new MyPostsRecyclerAdapter(ProfileActivity.this,posts);
                recycler.setAdapter(adapter);
                Log.d("sachin","adding post");
            }
        }).start();
    }
}
