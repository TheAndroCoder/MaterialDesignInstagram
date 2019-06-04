package com.example.instamaterial.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.instamaterial.Models.Post;
import com.example.instamaterial.R;

import java.util.ArrayList;

public class MyPostsRecyclerAdapter extends RecyclerView.Adapter<MyPostsRecyclerAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Post> posts;
    public MyPostsRecyclerAdapter(Context context, ArrayList<Post> posts){
        this.context=context;
        this.posts=posts;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.grid_recycler_item,viewGroup,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Glide.with(context).load(posts.get(i).getPost_url()).into(myViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public MyViewHolder(View view){
            super(view);
            image=view.findViewById(R.id.photos);
        }
    }
}
