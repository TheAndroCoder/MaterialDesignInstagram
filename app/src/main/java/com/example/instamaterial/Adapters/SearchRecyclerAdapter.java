package com.example.instamaterial.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;

import android.support.v7.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instamaterial.Models.User;
import com.example.instamaterial.ProfileActivity;
import com.example.instamaterial.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<User> users;
    public SearchRecyclerAdapter(Context context,ArrayList<User> users){
        this.context=context;
        this.users=users;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.search_item_layout,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        Glide.with(context).load(users.get(i).getDp_url()).into(myViewHolder.profile_pic);
        myViewHolder.username.setText(users.get(i).getName());
        myViewHolder.email.setText(users.get(i).getEmail());
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("UID",users.get(i).getUid());
                b.putString("DP_URL",users.get(i).getDp_url());
                b.putString("USERNAME",users.get(i).getName());
                b.putString("from","search_activity");
                Intent intent=new Intent(context,ProfileActivity.class);
                intent.putExtra("bundle",b);
                ActivityOptions options =  ActivityOptions.makeSceneTransitionAnimation((Activity)context, myViewHolder.profile_pic,"sharedProfilePic");
                context.startActivity(intent,options.toBundle());

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profile_pic;
        TextView username,email;
        public MyViewHolder(View view){
            super(view);
            profile_pic=view.findViewById(R.id.profile_pic);
            username=view.findViewById(R.id.username);
            email=view.findViewById(R.id.email);
        }
    }
}
