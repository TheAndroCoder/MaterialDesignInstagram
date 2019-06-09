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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instamaterial.Adapters.MyPostsRecyclerAdapter;
import com.example.instamaterial.Models.Post;
import com.example.instamaterial.Models.User;
import com.example.instamaterial.Notification.APIService;
import com.example.instamaterial.Notification.Client;
import com.example.instamaterial.Notification.Data;
import com.example.instamaterial.Notification.MyResponse;
import com.example.instamaterial.Notification.Sender;
import com.example.instamaterial.Utilities.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private String uid="";
    private APIService apiService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupXmlViews();
        startIntroAnimation();
        //If coming from notification then handle it here
        if(getIntent().getBundleExtra("bundle").getString("from").equals("notification")){
            //It is coming from notification
            uid = getIntent().getBundleExtra("bundle").getString("UID");
            Log.d("sachin","Inside notification");
            fetchUser();
            edit_profile_btn.setText("ACCEPT REQUEST");
        }else if(getIntent().getBundleExtra("bundle").getString("from").equals("search_activity")){
            //It is someone else's profile hence coming from search activity
            settings_fab.setImageResource(R.drawable.ic_action_send);
            //TODO : count the number of posts, followers and following also check if already followed or follow request sent or recieved
            edit_profile_btn.setText("SEND FOLLOW REQUEST");
            uid = getIntent().getBundleExtra("bundle").getString("UID");
            //fetch user from UID and update views
            username.setText(getIntent().getBundleExtra("bundle").getString("USERNAME"));
            Glide.with(this).load(getIntent().getBundleExtra("bundle").getString("DP_URL")).into(profile_pic);
        }else{
            //it is my own profile hence coming from main activity
            username.setText(preferences.getString("USERNAME",null));
            Glide.with(this).load(preferences.getString("DP_URL",null)).into(profile_pic);
        }
    }
    private void setupXmlViews(){
        settings_fab=findViewById(R.id.settings_fab);
        edit_profile_btn=findViewById(R.id.edit_profile_btn);
        mAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        checkIfAlreadyFriends();
        profile_pic=findViewById(R.id.profile_pic);
        username=findViewById(R.id.username);
        preferences=getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        recycler=findViewById(R.id.recycler);
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        recycler.setLayoutManager(new GridLayoutManager(this,3, LinearLayoutManager.VERTICAL,false));
        posts=new ArrayList<>();
        fetchMyPosts();
        //TODO : set on click listeners here
        settings_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uid=="")
                    startActivity(new Intent(ProfileActivity.this,SettingsActivity.class));
                else
                    Toast.makeText(ProfileActivity.this, "Direct Messaging will be available from InstaMaterial v1.2", Toast.LENGTH_SHORT).show();
            }
        });
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_profile_btn.getText().equals("EDIT PROFILE")){
                    startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class), ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this,profile_pic,profile_pic.getTransitionName()).toBundle());
                }else if(edit_profile_btn.getText().equals("SEND FOLLOW REQUEST")){
                    //send request to the person whose profile is opened
                    sendFollowRequest();
                    edit_profile_btn.setText("SENT");
                    edit_profile_btn.setClickable(false);
                }else if(edit_profile_btn.getText().equals("ACCEPT REQUEST")){
                    edit_profile_btn.setClickable(false);
                    edit_profile_btn.setText("FOLLOWING");
                    myRef.child("Friends").child(mAuth.getCurrentUser().getUid()).push().setValue(uid);
                    myRef.child("Friends").child(uid).push().setValue(mAuth.getCurrentUser().getUid());
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
    private void sendFollowRequest(){
        Log.d("sachin","coming");
        myRef.child("Tokens").orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String tokenOfReciever = ds.getValue(String.class);
                    Data data=new Data(mAuth.getCurrentUser().getUid(),username.getText().toString()+"","New Follow Request",uid);
                    Sender sender= new Sender(data,tokenOfReciever);
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if(response.body().success!=1){
                                    Toast.makeText(ProfileActivity.this,"Failed to send",Toast.LENGTH_SHORT).show();
                                    edit_profile_btn.setText("SEND FOLLOW REQUEST");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Toast.makeText(ProfileActivity.this, "error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void fetchUser(){
        myRef.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getName());
                Glide.with(ProfileActivity.this).load(user.getDp_url()).into(profile_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkIfAlreadyFriends(){
        myRef.child("Friends").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getValue().toString().equals(uid)){
                        edit_profile_btn.setText("FOLLOWING");
                        edit_profile_btn.setClickable(false);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
