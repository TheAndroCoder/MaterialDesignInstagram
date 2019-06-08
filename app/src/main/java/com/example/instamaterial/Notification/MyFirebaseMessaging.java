package com.example.instamaterial.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.instamaterial.ProfileActivity;
import com.example.instamaterial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("sachin","Started Firebase Notification Service");
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        String sented = remoteMessage.getData().get("sented");
        if(mAuth.getCurrentUser()!=null && sented.equals(mAuth.getCurrentUser().getUid())){
            showNotification(remoteMessage);
        }
    }
    private void showNotification(RemoteMessage remoteMessage){
        String sent_by=remoteMessage.getData().get("user");
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");
        Log.d("sachin","sent_by "+sent_by+" ");
        Intent intent=new Intent(this, ProfileActivity.class);
        int notificationId=Integer.parseInt(sent_by.replaceAll("[\\D]",""));
        Bundle bundle=new Bundle();
        bundle.putString("UID",sent_by);
        bundle.putString("from","notification");
        intent.putExtra("bundle",bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Uri defaultTone= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String CHANNEL_ID = "my_channel";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            //Create notification channel as required by Android 8.0+
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"NAME",importance);
            Notification notification=new Notification.Builder(this,CHANNEL_ID)
                    .setContentTitle(title)
                    .setChannelId(CHANNEL_ID)
                    .setContentText(message)
                    .setSound(defaultTone)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this,notificationId,intent,PendingIntent.FLAG_ONE_SHOT))
                    .setSmallIcon(R.drawable.ic_launcher).build();
            NotificationManager manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            manager.notify(notificationId,notification);
        }else{
            NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultTone)
                    .setContentIntent(PendingIntent.getActivity(this,notificationId,intent,PendingIntent.FLAG_ONE_SHOT));
            NotificationManager manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(notificationId,builder.build());
        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("sachin","New token recieved");
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
        myRef.child("Tokens").child(mAuth.getCurrentUser().getUid()).setValue(s);
    }
}
