package com.example.instamaterial;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.instamaterial.Adapters.CommentsAdapter;
import com.example.instamaterial.R;
import com.example.instamaterial.Utilities.Utils;

public class CommentsActivity extends AppCompatActivity {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private Button btnSend;
    private LinearLayout contentRoot;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setupXmlViews();
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter=new CommentsAdapter(CommentsActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.updateItems();
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    adapter.setAnimationsLocked(true);
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addItem();
                adapter.setAnimationsLocked(false);
                adapter.setDelayEnterAnimation(false);
                recyclerView.smoothScrollBy(0,recyclerView.getChildAt(0).getHeight()*adapter.getItemCount());
            }
        });
        //public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    }
    private void setupXmlViews(){
        toolbar=findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.rvComments);
        btnSend=findViewById(R.id.btnSendComment);
        contentRoot=findViewById(R.id.contentRoot);
    }

    @Override
    public void onBackPressed() {
        contentRoot.animate().translationY(Utils.getScreenHeight(this)).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                CommentsActivity.super.onBackPressed();
                overridePendingTransition(0,0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }
}
