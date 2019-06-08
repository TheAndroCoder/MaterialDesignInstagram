package com.example.instamaterial.Services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * This is a deprecated method hence don't use it
 */
public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("sachin","New token recieved");
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference();
        myRef.child("Tokens").child(mAuth.getCurrentUser().getUid()).setValue(s);
    }
}
