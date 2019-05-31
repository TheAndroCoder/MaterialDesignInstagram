package com.example.instamaterial;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.instamaterial.Adapters.PhotosAdapter;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.ArrayList;

public class GalleryFragment extends Fragment{
    public static ImageView selectedImage;
    private RecyclerView recycler;
    private ArrayList<String> imageList;
    private PhotosAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery,container,false);
        recycler=v.findViewById(R.id.recycler);
        selectedImage=v.findViewById(R.id.selectedPic);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(),3, LinearLayoutManager.VERTICAL,false));
        imageList=new ArrayList<>();
        getAllPhotos();

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
                adapter=new PhotosAdapter(getActivity(),imageList);
                recycler.setAdapter(adapter);
                selectedImage.setImageURI(Uri.parse(imageList.get(0)));
            }
        }).start();
    }

}
