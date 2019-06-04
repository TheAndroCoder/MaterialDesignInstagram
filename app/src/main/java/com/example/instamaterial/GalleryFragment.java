package com.example.instamaterial;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.instamaterial.Adapters.PhotosAdapter;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GalleryFragment extends Fragment implements PhotosAdapter.onItemClick{
    public ImageView selectedImage;
    private RecyclerView recycler;
    private ArrayList<String> imageList;
    private PhotosAdapter adapter;
    public String URI;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery,container,false);
        recycler=v.findViewById(R.id.recycler);
        selectedImage=v.findViewById(R.id.selectedPic);
        fab=v.findViewById(R.id.fab);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(),3, LinearLayoutManager.VERTICAL,false));
        LayoutAnimationController animation= AnimationUtils.loadLayoutAnimation(getActivity(),R.anim.layout_animation_from_bottom);
        recycler.setLayoutAnimation(animation);
        imageList=new ArrayList<>();
        getAllPhotos();
        setOnClickListeners();
        return v;
    }
    private void getAllPhotos(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projection={MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
                Cursor cursor=getContext().getContentResolver().query(uri,projection,null,null,null);
                int column_index_data=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                int column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                while(cursor.moveToNext()){
                    imageList.add(cursor.getString(column_index_data));
                    //Log.d("sachin",cursor.getString(column_index_data));
                }
                Log.d("sachin","Image list contains "+ imageList.get(0));
                adapter=new PhotosAdapter(getActivity(),imageList);
                recycler.setAdapter(adapter);
                //adapter.setOnItemClickListener(GalleryFragment.this);
                adapter.setOnItemClickListener(GalleryFragment.this);
                URI=imageList.get(0);
                selectedImage.setImageURI(Uri.parse(imageList.get(0)));
            }
        }).start();
    }

    @Override
    public void onClick(String uri) {
        Log.d("sachin","Recieved as uri :"+uri);
        URI=uri;
        selectedImage.setImageURI(Uri.parse(uri));
    }
    private void setOnClickListeners(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //It is image
                Bundle b = new Bundle();
                Intent intent=new Intent(getActivity(),PostActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),selectedImage,selectedImage.getTransitionName());
                b.putString("type","image");
                b.putString("from","gallery_fragment");
                b.putString("data",URI);
                intent.putExtra("bundle",b);
                startActivity(intent,options.toBundle());
            }
        });
    }
//    private Bitmap getBitmapFromUri(){
//        try{
//            return MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),Uri.parse(URI));
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
}
