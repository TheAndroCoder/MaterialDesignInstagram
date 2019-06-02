package com.example.instamaterial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.instamaterial.Adapters.SearchRecyclerAdapter;
import com.example.instamaterial.Models.User;
import com.example.instamaterial.Utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private ImageView backBtn;
    private RelativeLayout toolbar,searchBar;
    private RecyclerView recycler;
    private SearchRecyclerAdapter adapter;
    private EditText username;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupXmlViews();
        startIntroAnimations();
    }
    private void setupXmlViews(){
        toolbar=findViewById(R.id.toolbar);
        backBtn=findViewById(R.id.back);
        searchBar=findViewById(R.id.searchBar);
        recycler=findViewById(R.id.recycler);
        username=findViewById(R.id.username);
        myRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.super.onBackPressed();
                overridePendingTransition(0,0);
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==0){
                    recycler.setVisibility(View.GONE);
                }else{
                    recycler.setVisibility(View.VISIBLE);
                    fetchUsers(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void fetchUsers(final CharSequence charSequence) {
        myRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<User> users=new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(user.getName().contains(charSequence) && !user.getUid().equals(mAuth.getCurrentUser().getUid())){
                        users.add(user);
                    }
                }
                adapter=new SearchRecyclerAdapter(SearchActivity.this,users);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startIntroAnimations(){
        //toolbar.setTranslationY(-Utils.dpToPx(50));
        //toolbar.setAlpha(0.f);
        backBtn.setTranslationX(-Utils.dpToPx(50));
        //searchBar.setTranslationY(-Utils.dpToPx(50));
        backBtn.animate().translationX(0).setStartDelay(200).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
    }
}
