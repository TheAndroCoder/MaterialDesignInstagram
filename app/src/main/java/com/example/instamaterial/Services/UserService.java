package com.example.instamaterial.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.instamaterial.Models.User;
import com.example.instamaterial.Utilities.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserService extends Service {
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private Context context=this;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //super.onCreate();
        Log.d("sachin","Service started !!");
        mAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
//        myRef.child("Users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<User> users=new ArrayList<>();
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    User user=ds.getValue(User.class);
//                    users.add(user);
//                }
//                DatabaseHelper helper=DatabaseHelper.getInstance(context);
//                helper.updateUsers(users);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }
}
