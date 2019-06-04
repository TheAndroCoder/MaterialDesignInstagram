package com.example.instamaterial.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.instamaterial.GalleryActivity;
import com.example.instamaterial.GalleryFragment;
import com.example.instamaterial.R;
import com.example.instamaterial.Utilities.Utils;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.ArrayList;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> images;
    private int lastAnimatedPosition=-1;



    public interface onItemClick{
        void onClick(String uri);
    }
    private onItemClick listener;
    public void setOnItemClickListener(onItemClick listener){
        this.listener=listener;
    }
    public PhotosAdapter(Context context, ArrayList<String> images){
        this.context=context;
        this.images=images;

        //Log.d("sachin","Called recycler Grid constructor");
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(context).inflate(R.layout.grid_recycler_item,viewGroup,false);
        return new MyViewHolder(v);
    }
    private void runEnterAnimation(View view,int position){
        if(position>=1)
            return;
        if(position>lastAnimatedPosition){
            lastAnimatedPosition=position;
            view.setTranslationY(Utils.getScreenHeight(context));
            view.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3.f)).setStartDelay(1000).setDuration(700).start();
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder,final int i) {
        //Log.d("sachin","Trying to load photo");
        //runEnterAnimation(myViewHolder.itemView,i);
        Glide.with(context).load(images.get(i)).into(myViewHolder.image);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onClick(images.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public MyViewHolder(View view){
            super(view);
            image=view.findViewById(R.id.photos);
        }


    }

}
