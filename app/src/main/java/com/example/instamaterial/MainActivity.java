package com.example.instamaterial;

import android.animation.Animator;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.example.instamaterial.Adapters.FeedAdapter;
import com.example.instamaterial.Services.UserService;
import com.example.instamaterial.Utilities.DatabaseHelper;
import com.example.instamaterial.Utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements  FeedAdapter.OnFeedItemClicklistener{
    Toolbar toolbar;
    RecyclerView recyclerView;
    boolean pendingIntroAnim=false;
    FloatingActionButton fab;
    ImageView InstaText,inboxBtn;
    CircleImageView profile_pic;
    FeedAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupXmlLayouts();
        if(savedInstanceState==null){
            pendingIntroAnim=true;
            startIntroAnimation();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter=new FeedAdapter(this);
        adapter.setOnFeedItemClicklistener(this);
        recyclerView.setAdapter(adapter);
        //DatabaseHelper helper=DatabaseHelper.getInstance(this);
        //helper.fetchAllUsers();
        //toolbar.setNavigationIcon(R.drawable.ic_menu_white);
    }
    private void setupXmlLayouts(){
        toolbar=findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.rvFeed);
        fab=findViewById(R.id.fab3);
        inboxBtn=findViewById(R.id.inbox);
        InstaText=findViewById(R.id.ivLogo);
        profile_pic=findViewById(R.id.profile_pic);
        mAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        //For now the profile pic icon works as signout button
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
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
        inboxBtn.setTranslationX(actionBarSize);
        toolbar.animate().translationY(0).setDuration(300).setStartDelay(300).start();
        InstaText.animate().translationY(0).setDuration(400).setStartDelay(300).start();
        profile_pic.animate().translationX(0).setStartDelay(500).setDuration(300).start();
        inboxBtn.animate().translationX(0).setStartDelay(500).setDuration(300).setListener(new Animator.AnimatorListener() {
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
        adapter.updateItems();
    }

    @Override
    public void onCommentsClick(View v, int position) {
        //adapter.setOnFeedItemClicklistener(this);
        Intent intent=new Intent(this,CommentsActivity.class);
        int[] startLocation=new int[2];
        v.getLocationOnScreen(startLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION,startLocation[1]);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

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
        startService(new Intent(this, UserService.class));

    }
}
