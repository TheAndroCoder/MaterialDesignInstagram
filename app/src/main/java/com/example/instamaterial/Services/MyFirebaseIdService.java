package com.example.instamaterial.Services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
            myRef.child("Tokens").child(mAuth.getCurrentUser().getUid()).setValue(FirebaseInstanceId.getInstance().getToken());
        }

    }
}
