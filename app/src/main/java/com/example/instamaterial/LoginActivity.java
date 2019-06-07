package com.example.instamaterial;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.PatternMatcher;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instamaterial.Services.UserService;
import com.example.instamaterial.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    private EditText email,password;
    private ImageView logo;
    private Button registerBtn,loginBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private GoogleProgressBar progress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupXmlViews();
        startViewAnimations(savedInstanceState);
        //startService(new Intent(this, UserService.class));
    }
    private void setupXmlViews(){
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        logo=findViewById(R.id.logo);
        registerBtn=findViewById(R.id.registerBtn);
        loginBtn=findViewById(R.id.loginBtn);
        FirebaseApp.initializeApp(this);
        //FirebaseDatabase.getInstance(FirebaseApp.initializeApp(this));
        mAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        progress=findViewById(R.id.google_progress);
        progress.setVisibility(View.GONE);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final SweetAlertDialog alertDialog=new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.PROGRESS_TYPE);
//                alertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                alertDialog.setTitleText("Authenticating..");
//                alertDialog.setCancelable(false);
//                alertDialog.show();
                progress.setVisibility(View.VISIBLE);
                if(checkInputs()){
                    mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //alertDialog.dismiss();
                                //Intent to MainActivity and finish this activity
                                progress.setVisibility(View.GONE);
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();
                            }else{
                                //alertDialog.dismiss();
                                progress.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "You don't have an account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //alertDialog.dismiss();
                            progress.setVisibility(View.GONE);
                            Log.d("LOGIN_ACTIVITY",e.getMessage());
                        }
                    });
                }else{
                    //alertDialog.dismiss();
//                    SweetAlertDialog dialog=new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.ERROR_TYPE);
//                    dialog.setTitleText("Invalid Inputs");
//                    dialog.setCancelable(true);
//                    dialog.show();
                    progress.setVisibility(View.GONE);
                }
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

            }
        });
    }
    private boolean checkInputs(){
        //Email and password length validation only
        if(password.getText().toString().length()<6){
            password.setError("Password too short");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.setError("Invalid Email");
            return false;
        }
        return true;
    }
    private void startViewAnimations(Bundle savedInstance){
        if(savedInstance==null){
            logo.setTranslationY(-100f);
            logo.setAlpha(0.f);
            logo.animate().translationY(0).alpha(1.f).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
            email.setTranslationX(-Utils.getScreenWidth(this));
            email.setAlpha(0.f);
            email.animate().translationX(0).alpha(1.f).setDuration(500).setStartDelay(200).setInterpolator(new DecelerateInterpolator()).start();
            password.setAlpha(0.f);
            password.setTranslationX(-Utils.getScreenWidth(this));
            password.animate().translationX(0).alpha(1.f).setDuration(500).setStartDelay(300).setInterpolator(new DecelerateInterpolator()).start();
            registerBtn.setAlpha(0.f);
            loginBtn.setAlpha(0.f);
            registerBtn.setTranslationY(100f);
            loginBtn.setTranslationY(100f);
            registerBtn.animate().translationY(0).alpha(1.f).setDuration(500).setStartDelay(200).setInterpolator(new DecelerateInterpolator()).start();
            loginBtn.animate().translationY(0).alpha(1.f).setDuration(500).setStartDelay(300).setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(this,MainActivity.class));

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mAuth.getCurrentUser()==null){
            finishAffinity();
        }
    }
}
