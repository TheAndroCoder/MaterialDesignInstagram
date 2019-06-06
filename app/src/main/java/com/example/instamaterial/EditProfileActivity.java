package com.example.instamaterial;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instamaterial.Models.User;
import com.example.instamaterial.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView blurryView,back_btn,update_btn;
    private CircleImageView profile_pic;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private EditText username,email,password;
    private ProgressBar progress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setupXmlViews();

    }
    private void setupXmlViews(){
        blurryView=findViewById(R.id.blurryImage);
        profile_pic=findViewById(R.id.profile_pic);
        mAuth=FirebaseAuth.getInstance();
        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        progress=findViewById(R.id.progress);
        back_btn=findViewById(R.id.back);
        update_btn=findViewById(R.id.saveDetails);
        myRef= FirebaseDatabase.getInstance().getReference();
        sharedPreferences=getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        Glide.with(this).load(sharedPreferences.getString("DP_URL",null)).into(profile_pic);
        //Glide.with(this).load(sharedPreferences.getString("DP_URL",null)).transform(n)
        new Thread(new Runnable() {
            @Override
            public void run() {
               // Bitmap bitmap= Utils.getBitmapFromUrl( sharedPreferences.getString("DP_URL",null));

                try{
                    URL url = new URL(sharedPreferences.getString("DP_URL",null));
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input=connection.getInputStream();
                    Bitmap bmp = BitmapFactory.decodeStream(input);
                    Log.d("sachin","bitmap decoded");
                    Utils.blurBitmapWithRenderscript(RenderScript.create(EditProfileActivity.this),bmp);
                    blurryView.setImageBitmap(bmp);


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        username.setText(sharedPreferences.getString("USERNAME","Something went wrong !"));
        email.setText(sharedPreferences.getString("EMAIL","Something went wrong !"));
        password.setText(sharedPreferences.getString("PASSWORD","Something went wrong !"));
        //setup on click listeners
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_btn.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                if(checkValidations()){
                    if(!sharedPreferences.getString("EMAIL",null).equals(email.getText().toString())){
                        //AuthCredential credential= EmailAuthProvider.getCredential(sharedPreferences.getString("EMAIL",null),sharedPreferences.getString("PASSWORD",null));
                        mAuth.getCurrentUser().updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(email.getText().toString());
                                    Toast.makeText(EditProfileActivity.this,"UPDATED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("EMAIL",email.getText().toString());
                                    editor.commit();
                                    EditProfileActivity.this.onBackPressed();
                                }else{
                                    Toast.makeText(EditProfileActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                                    progress.setVisibility(View.GONE);
                                    update_btn.setVisibility(View.VISIBLE);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.setVisibility(View.GONE);
                                update_btn.setVisibility(View.VISIBLE);
                                Toast.makeText(EditProfileActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if(!sharedPreferences.getString("PASSWORD",null).equals(password.getText().toString())){
                        AuthCredential credential= EmailAuthProvider.getCredential(sharedPreferences.getString("EMAIL",null),sharedPreferences.getString("PASSWORD",null));
                        mAuth.getCurrentUser().updatePassword(password.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("password").setValue(password.getText().toString());
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("PASSWORD",password.getText().toString());
                                    editor.commit();
                                    EditProfileActivity.this.onBackPressed();
                                }else{
                                    Log.d("sachin",task.getException().toString());
                                    progress.setVisibility(View.GONE);
                                    update_btn.setVisibility(View.VISIBLE);
                                    Toast.makeText(EditProfileActivity.this, "Failed updation "+task.getException(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                    if(!sharedPreferences.getString("USERNAME",null).equals(username.getText().toString())){
                        myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("name").setValue(username.getText().toString());
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("USERNAME",username.getText().toString());
                        editor.commit();
                        EditProfileActivity.this.onBackPressed();
                    }
                }

            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileActivity.this.onBackPressed();
            }
        });
    }
    private boolean checkValidations(){
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.setError("Invalid Email");
            return false;
        }else if(password.getText().toString().length()<6){
            password.setError("Password too short");
        }
        return true;
    }
}
