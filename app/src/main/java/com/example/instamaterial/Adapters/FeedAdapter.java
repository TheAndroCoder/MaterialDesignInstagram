package com.example.instamaterial.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.instamaterial.Models.Post;
import com.example.instamaterial.Models.User;
import com.example.instamaterial.R;
import com.example.instamaterial.Utilities.Utils;
import com.example.instamaterial.View.SquareImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CellFeedViewHolder> {
    private static final int ANIMATED_ITEMS_COUNT = 2;
    //private OnFeedItemClicklistener onFeedItemClicklistener;
    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private ArrayList<Post> posts;
    private ArrayList<User> users;
    private ArrayList<Integer> comment_count_list;
    private ArrayList<Integer> like_count_list;
    public  FeedAdapter(Context context, ArrayList<Post> posts, ArrayList<User> users,ArrayList<Integer> comment_count_list,ArrayList<Integer> like_count_list){
        this.context=context;
        this.posts=posts;
        this.users=users;
        this.like_count_list=like_count_list;
        this.comment_count_list=comment_count_list;

    }
    @NonNull
    @Override
    public CellFeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(context).inflate(R.layout.main_feed_row_layout,viewGroup,false);
        return new CellFeedViewHolder(v);
    }
    private void runEnterAnimation(View view,int position){
        if(position>=ANIMATED_ITEMS_COUNT-1){
            return;
        }
        if(position>lastAnimatedPosition){
            lastAnimatedPosition=position;
            view.setTranslationY(Utils.getScreenHeight(context));
            view.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3.f)).setDuration(700).start();
        }

    }
    @Override
    public void onBindViewHolder(@NonNull CellFeedViewHolder holder, int i) {
        runEnterAnimation(holder.itemView,i);
        if(posts.get(i).getType().equals("image")){
            holder.username.setText(users.get(i).getName());
            holder.timestamp.setText(posts.get(i).getPost_date());
            Glide.with(context).load(posts.get(i).getPost_url()).into(holder.post_pic);
            Glide.with(context).load(users.get(i).getDp_url()).into(holder.profile_pic);
            //TODO count animation for comments and likes
            if(comment_count_list.get(i)!=0)
            holder.comments_text.setText("View all "+comment_count_list.get(i)+" Comments");
            else
                holder.comments_text.setText("No comments yet");
            holder.likes_count.setText(like_count_list.get(i)+" Likes");
        }else{
            holder.post_pic.setVisibility(View.GONE);
            holder.post_video.setVisibility(View.VISIBLE);
            holder.username.setText(users.get(i).getName());
            holder.timestamp.setText(posts.get(i).getPost_date());
            holder.post_video.setVideoURI(Uri.parse(posts.get(i).getPost_url()));
            if(comment_count_list.get(i)!=0)
                holder.comments_text.setText("View all "+comment_count_list.get(i)+" Comments");
            else
                holder.comments_text.setText("No comments yet");
            holder.likes_count.setText(like_count_list.get(i)+" Likes");
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }



    public class CellFeedViewHolder extends RecyclerView.ViewHolder{
        ImageView post_pic;
        VideoView post_video;
        TextView comments_text,username,timestamp,likes_count;
        ImageView like_btn;
        CircleImageView profile_pic;

        public CellFeedViewHolder(View view){
            super(view);
            post_pic=view.findViewById(R.id.post_picture);
            post_video=view.findViewById(R.id.post_video);
            username=view.findViewById(R.id.username);
            comments_text=view.findViewById(R.id.commentText);
            timestamp=view.findViewById(R.id.timestamp);
            like_btn=view.findViewById(R.id.heart);
            profile_pic=view.findViewById(R.id.profile_pic);
            likes_count=view.findViewById(R.id.likes_count);
        }
    }


}
