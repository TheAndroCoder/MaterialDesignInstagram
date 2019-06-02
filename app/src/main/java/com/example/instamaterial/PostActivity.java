package com.example.instamaterial;

import android.animation.Animator;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.instamaterial.Models.Post;
import com.example.instamaterial.Utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostActivity extends AppCompatActivity {
    private ImageView postImage,typeImage;
    private RelativeLayout toolbar;
    private FloatingActionButton sendPost;
    private String type="";
    private Bitmap bitmap=null;
    private Uri videoUri=null;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setupXmlViews();
        startIntroAnimations();
    }
    private void setupXmlViews(){
        sendPost=findViewById(R.id.sendPost);
        toolbar=findViewById(R.id.toolbar);
        postImage=findViewById(R.id.postImage);
        typeImage=findViewById(R.id.typeImage);
        mAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        //get the image data from intent and post
        if(getIntent().getExtras()!=null){
            type=getIntent().getBundleExtra("bundle").getString("type");
            if(type.equals("image")){
                //It is an Image
                bitmap=getIntent().getBundleExtra("bundle").getParcelable("data");
                postImage.setImageBitmap(bitmap);
                typeImage.setImageResource(R.drawable.ic_action_camera);
            }else {
                //It is a video
                videoUri=getIntent().getBundleExtra("bundle").getParcelable("uri");
                String path=getRealPathFromUri();
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
                //BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                postImage.setImageBitmap(thumb);
                typeImage.setImageResource(R.drawable.ic_action_video);
            }

        }

        //set button onClick Listeners here
        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Put the post into Sqlite DB, Firebase DB and Image/Video into Firebase Storage
                //Upload the image to firebase storage first so that we get the download link and then we can set the Post POJO
                Post post = new Post(Utils.getRandomkey(),mAuth.getCurrentUser().getUid(),Utils.getDateString(),Utils.getRandomkey(),type,"","","");
            }
        });
    }
    private void startIntroAnimations(){
        toolbar.setTranslationY(-Utils.dpToPx(100));
        toolbar.setAlpha(0.f);
        sendPost.setScaleY(0);
        sendPost.setScaleX(0);
        toolbar.animate().translationY(0).alpha(1.f).setDuration(500).setStartDelay(200).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                sendPost.animate().setDuration(300).scaleY(1).scaleX(1).setInterpolator(new OvershootInterpolator()).start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }
    private String getRealPathFromUri(){
        String res;
        Cursor cursor=getContentResolver().query(videoUri,null,null,null,null);
        if(cursor==null){
            res=videoUri.getPath();
        }else{
            cursor.moveToFirst();
            int idx=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            res=cursor.getString(idx);
        }
        return  res;
    }
}
