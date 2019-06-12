package com.example.instamaterial;

import android.Manifest;
import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.instamaterial.Adapters.FeedAdapter;
import com.example.instamaterial.Models.Post;
import com.example.instamaterial.Models.User;
import com.example.instamaterial.Services.UserService;
import com.example.instamaterial.Utilities.DatabaseHelper;
import com.example.instamaterial.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    boolean pendingIntroAnim=false;
    FloatingActionButton fab;
    ImageView InstaText,searchBtn;
    CircleImageView profile_pic;
    FeedAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private SharedPreferences preferences;
    private ArrayList<User> users;
    private  ArrayList<Post> posts;
    private ArrayList<Integer> likesCount;
    private ArrayList<Integer> commentsCount;
    private RelativeLayout waitingLayout;

    private interface FirebaseCallback1{
         void friendsCallback(ArrayList<String> list);
    }
    private interface FirebaseCallback2{
        void postsCallback(Post post,User user);
    }
    private interface FirebaseCallback3{
        void userCallback(User user);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupXmlLayouts();
        askForPermissions();
        if(savedInstanceState==null){
            pendingIntroAnim=true;
            startIntroAnimation();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        //Fetch Posts data for populating the Main Feed

        //CallBack Hell starts
        fetchFriends(new FirebaseCallback1() {
            @Override
            public void friendsCallback(ArrayList<String> list) {
                fetchPostsAndUser(new FirebaseCallback2() {
                    @Override
                    public void postsCallback(Post post, User user) {
                        posts.add(post);
                        users.add(user);
                        for(int i=0;i<posts.size();i++){
                            commentsCount.add(0);
                            likesCount.add(0);
                        }
                        adapter=new FeedAdapter(MainActivity.this,posts,users,commentsCount,likesCount);
                        recyclerView.setAdapter(adapter);
                        waitingLayout.setVisibility(View.GONE);
                    }
                },list);
            }
        });
        //CallBack Hell ends

        //Fetch the user data and store it in Shared Preferences
        preferences=getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        if(preferences.getString("UID",null)==null)
            saveUserToPreferences();
        else
            Glide.with(this).load(preferences.getString("DP_URL",null)).into(profile_pic);
    }
    private void setupXmlLayouts(){
        toolbar=findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.rvFeed);
        fab=findViewById(R.id.fab3);
        searchBtn=findViewById(R.id.search);
        InstaText=findViewById(R.id.ivLogo);
        profile_pic=findViewById(R.id.profile_pic);
        mAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        posts=new ArrayList<>();
        users=new ArrayList<>();
        likesCount=new ArrayList<>();
        waitingLayout=findViewById(R.id.waiting);
        waitingLayout.setVisibility(View.VISIBLE);
        commentsCount=new ArrayList<>();
        //For now the profile pic icon works as signout button
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mAuth.signOut();
                Bundle b = new Bundle();
                b.putString("from","main_activity");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,profile_pic,"sharedProfilePic");
                startActivity(new Intent(MainActivity.this,ProfileActivity.class).putExtra("bundle",b),options.toBundle());
                //finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,GalleryActivity.class));
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open new activity with ovveridePendingTransition(0,0) and don't finish the current activity
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
                overridePendingTransition(0,0);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    private void startIntroAnimation(){
        fab.setTranslationY(2*getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        int actionBarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionBarSize);
        profile_pic.setTranslationX(-actionBarSize);
        InstaText.setTranslationY(-actionBarSize);
        searchBtn.setTranslationX(actionBarSize);
        toolbar.animate().translationY(0).setDuration(300).setStartDelay(300).start();
        InstaText.animate().translationY(0).setDuration(400).setStartDelay(300).start();
        profile_pic.animate().translationX(0).setStartDelay(500).setDuration(300).start();
        searchBtn.animate().translationX(0).setStartDelay(500).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startContentAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();

    }
    private void startContentAnimation(){
        fab.animate().translationY(0).setInterpolator(new OvershootInterpolator(1.f)).setStartDelay(300).setDuration(400).start();
        //adapter.updateItems();
    }

//    @Override
//    public void onCommentsClick(View v, int position) {
//        //adapter.setOnFeedItemClicklistener(this);
//        Intent intent=new Intent(this,CommentsActivity.class);
//        int[] startLocation=new int[2];
//        v.getLocationOnScreen(startLocation);
//        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION,startLocation[1]);
//        startActivity(intent);
//        overridePendingTransition(0,0);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            finishAffinity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                String token=task.getResult().getToken();
                myRef.child("Tokens").child(mAuth.getCurrentUser().getUid()).setValue(token);
            }
        });
        //startService(new Intent(this, UserService.class));

    }
    private void askForPermissions(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET},1001);
        }
    }
    private void saveUserToPreferences(){

        final SharedPreferences.Editor editor=preferences.edit();
        myRef.child("Users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                editor.putString("UID",mAuth.getCurrentUser().getUid());
                editor.putString("USERNAME",user.getName());
                editor.putString("EMAIL",user.getEmail());
                editor.putString("DP_URL",user.getDp_url());
                editor.putString("PASSWORD",user.getPassword());
                Glide.with(MainActivity.this).load(user.getDp_url()).into(profile_pic);
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void fetchFriends(final FirebaseCallback1 firebaseCallback1){
        //First fetch friends list then fetch the posts of friends and self
        myRef.child("Friends").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list=new ArrayList<>();
                list.add(mAuth.getCurrentUser().getUid());
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    list.add(ds.getValue(String.class));
                }
                firebaseCallback1.friendsCallback(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void fetchPostsAndUser(final FirebaseCallback2 firebaseCallback2,final ArrayList<String> list){
        myRef.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //final ArrayList<Post> l=new ArrayList<>();
                //ArrayList<User> u = new ArrayList<>();
                for(final DataSnapshot ds : dataSnapshot.getChildren()){
                    if(list.contains(ds.getValue(Post.class).getBy_id())){
                        //l.add(ds.getValue(Post.class));
                        myRef.child("Users").child(ds.getValue(Post.class).getBy_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                firebaseCallback2.postsCallback(ds.getValue(Post.class),dataSnapshot.getValue(User.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                //firebaseCallback2.postsCallback(l);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
