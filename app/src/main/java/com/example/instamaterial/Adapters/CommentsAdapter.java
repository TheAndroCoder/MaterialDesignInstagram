package com.example.instamaterial.Adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.shape.RoundedCornerTreatment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.instamaterial.CommentsActivity;
import com.example.instamaterial.R;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {
    private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;
    public CommentsAdapter(Context context){
        this.context=context;
        this.avatarSize=context.getResources().getDimensionPixelSize(R.dimen.btn_fab_size);
    }
    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.comments_row_layout,viewGroup,false);
        return new CommentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder commentsViewHolder, int i) {
        runEnterAnimation(commentsViewHolder.itemView,i);
        switch(i%3){
            case 0:
                commentsViewHolder.comment.setText("Heyy Man How are You");
                break;
            case 1:
                commentsViewHolder.comment.setText("How You Doinn'");
                break;
            case 2 :
                commentsViewHolder.comment.setText("Beautiful Picture");
                break;
        }
        Glide.with(context).load(R.drawable.ic_launcher).into(commentsViewHolder.avatar);
        //Picasso.get().load(R.drawable.ic_launcher).centerCrop().resize(avatarSize,avatarSize).into(commentsViewHolder.avatar);
    }
    private void runEnterAnimation(View v , int position){
        if(animationsLocked)return;
        if(position>lastAnimatedPosition){
            lastAnimatedPosition=position;
            v.setTranslationY(100);
            v.setAlpha(0.f);
            v.animate().translationY(0).alpha(1.f).setStartDelay(delayEnterAnimation?20*position:0)
                    .setInterpolator(new DecelerateInterpolator(3.f)).setDuration(300).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    animationsLocked=true;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).start();

        }
    }
    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder{
        ImageView avatar;
        TextView comment;
        public CommentsViewHolder(View view){
            super(view);
            comment=view.findViewById(R.id.tvComment);
            avatar=view.findViewById(R.id.ivUserAvatar);
        }
    }
    public void updateItems(){
        itemsCount=10;notifyDataSetChanged();
    }
    public void addItem(){
        itemsCount++;
        notifyItemInserted(itemsCount-1);
    }
    public void setAnimationsLocked(boolean lock){
        this.animationsLocked=lock;
    }
    public void setDelayEnterAnimation(boolean delayEnterAnimation){
        this.delayEnterAnimation=delayEnterAnimation;
    }
}
