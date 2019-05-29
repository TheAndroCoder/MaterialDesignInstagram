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
import com.example.instamaterial.Utilities.Utils;


public class MainActivity extends AppCompatActivity implements  FeedAdapter.OnFeedItemClicklistener{
    Toolbar toolbar;
    RecyclerView recyclerView;
    boolean pendingIntroAnim=false;
    FloatingActionButton fab;
    ImageView navigation,InstaText,inboxBtn;
    FeedAdapter adapter;
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
        //toolbar.setNavigationIcon(R.drawable.ic_menu_white);
    }
    private void setupXmlLayouts(){
        toolbar=findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.rvFeed);
        fab=findViewById(R.id.fab3);
        inboxBtn=findViewById(R.id.inbox);
        InstaText=findViewById(R.id.ivLogo);
        navigation=findViewById(R.id.toolbar_navigation);
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
        navigation.setTranslationX(-actionBarSize);
        InstaText.setTranslationY(-actionBarSize);
        inboxBtn.setTranslationX(actionBarSize);
        toolbar.animate().translationY(0).setDuration(300).setStartDelay(300).start();
        InstaText.animate().translationY(0).setDuration(400).setStartDelay(300).start();
        navigation.animate().translationX(0).setStartDelay(500).setDuration(300).start();
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
}
