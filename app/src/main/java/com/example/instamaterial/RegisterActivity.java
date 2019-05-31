package com.example.instamaterial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instamaterial.Models.User;
import com.example.instamaterial.Utilities.DatabaseHelper;
import com.example.instamaterial.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;


public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn,loginBtn;
    private EditText email,password,username;
    private ImageView logo;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private GoogleProgressBar progress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupXmlViews();
        startAnimations(savedInstanceState);
    }
    private void setupXmlViews(){
        logo=findViewById(R.id.logo);
        registerBtn=findViewById(R.id.registerBtn);
        loginBtn=findViewById(R.id.loginBtn);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        username=findViewById(R.id.username);
        progress=findViewById(R.id.google_progress);
        progress.setVisibility(View.GONE);
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        if(mAuth==null){
            Log.d("sachin","mAuth is null");
        }
        myRef= FirebaseDatabase.getInstance().getReference();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("REGISTER_ACTIVITY","clicked btn");
                progress.setVisibility(View.VISIBLE);
                if(checkInputs()){
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //alertDialog.dismiss();
                            if(task.isSuccessful()){
                                //Put User data into Firebase Database and Sqlite
                                User user=new User(mAuth.getCurrentUser().getUid(),username.getText().toString(),email.getText().toString(),password.getText().toString(),"");
                                myRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(user);
                                //DatabaseHelper dbhelper=DatabaseHelper.getInstance(RegisterActivity.this);
                                mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            progress.setVisibility(View.GONE);
                                            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                            finish();
                                        }else{
                                            progress.setVisibility(View.GONE);
                                            Toast.makeText(RegisterActivity.this, "Failed to SignIn", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                progress.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Failed to register!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progress.setVisibility(View.GONE);
                            Log.d("REGISTER_ACTIVITY",e.getMessage());
                            //alertDialog.dismiss();
                        }
                    });
                }else{
                    //alertDialog.dismiss();
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }
    private void startAnimations(Bundle savedInstance){
        if(savedInstance==null){
            logo.setTranslationY(-100f);
            logo.setAlpha(0.f);
            logo.animate().translationY(0).alpha(1.f).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
            email.setTranslationX(-Utils.getScreenWidth(this));
            email.setAlpha(0.f);
            email.animate().translationX(0).alpha(1.f).setDuration(500).setStartDelay(200).setInterpolator(new DecelerateInterpolator()).start();
            password.setAlpha(0.f);
            password.setTranslationX(-Utils.getScreenWidth(this));
            password.animate().translationX(0).alpha(1.f).setDuration(500).setStartDelay(400).setInterpolator(new DecelerateInterpolator()).start();
            registerBtn.setAlpha(0.f);
            loginBtn.setAlpha(0.f);
            registerBtn.setTranslationY(100f);
            loginBtn.setTranslationY(100f);
            registerBtn.animate().translationY(0).alpha(1.f).setDuration(500).setStartDelay(200).setInterpolator(new DecelerateInterpolator()).start();
            loginBtn.animate().translationY(0).alpha(1.f).setDuration(500).setStartDelay(300).setInterpolator(new DecelerateInterpolator()).start();
            username.setTranslationX(-Utils.getScreenWidth(this));
            username.setAlpha(0.f);
            username.animate().translationX(0).alpha(1.f).setDuration(500).setStartDelay(300).setInterpolator(new DecelerateInterpolator()).start();
        }
    }
    private boolean checkInputs(){
        if(password.getText().toString().length()<6){
            password.setError("Password too short");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.setError("Invalid Email");
            return false;
        }
        return true;
    }
}
