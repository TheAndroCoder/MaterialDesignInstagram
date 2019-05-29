package com.example.instamaterial.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.example.instamaterial.R;
import com.example.instamaterial.Utilities.Utils;
import com.example.instamaterial.View.SquareImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CellFeedViewHolder> implements View.OnClickListener{
    private static final int ANIMATED_ITEMS_COUNT = 2;
    private OnFeedItemClicklistener onFeedItemClicklistener;
    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    public  FeedAdapter(Context context){
        this.context=context;
    }
    @NonNull
    @Override
    public CellFeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(context).inflate(R.layout.feed_row_layout,viewGroup,false);
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
    public void onBindViewHolder(@NonNull CellFeedViewHolder cellFeedViewHolder, int i) {
        runEnterAnimation(cellFeedViewHolder.itemView,i);
        if(i%2==0){
            cellFeedViewHolder.center.setImageResource(R.drawable.img_feed_center_1);
            cellFeedViewHolder.bottom.setImageResource(R.drawable.img_feed_bottom_1);
        }else{
            cellFeedViewHolder.center.setImageResource(R.drawable.img_feed_center_2);
            cellFeedViewHolder.bottom.setImageResource(R.drawable.img_feed_bottom_2);
        }
        cellFeedViewHolder.bottom.setOnClickListener(this);
        cellFeedViewHolder.bottom.setTag(i);
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.feedBottom){
            if(onFeedItemClicklistener!=null){
                onFeedItemClicklistener.onCommentsClick(view,(Integer) view.getTag());
            }
        }
    }

    public class CellFeedViewHolder extends RecyclerView.ViewHolder{
        ImageView bottom;
        SquareImageView center;
        public CellFeedViewHolder(View view){
            super(view);
            center=view.findViewById(R.id.feedCenter);
            bottom=view.findViewById(R.id.feedBottom);
        }
    }
    public void updateItems(){
        itemsCount=10;
        notifyDataSetChanged();
    }
    public void setOnFeedItemClicklistener(OnFeedItemClicklistener onFeedItemClicklistener){
        this.onFeedItemClicklistener=onFeedItemClicklistener;
    }
    public interface OnFeedItemClicklistener{
        void onCommentsClick(View v,int position);
    }
}
