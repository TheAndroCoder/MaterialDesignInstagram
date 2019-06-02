package com.example.instamaterial;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
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
import android.widget.TextView;
import android.widget.VideoView;

import com.example.instamaterial.Adapters.VideosAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class VideoFragment extends Fragment {
    private VideoView videoView;
    private RecyclerView recycler;
    private VideosAdapter adapter;
    private ImageView playpause;
    private TextView timeTextView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_video,container,false);
        videoView=view.findViewById(R.id.videoView);
        recycler=view.findViewById(R.id.recycler);
        playpause=view.findViewById(R.id.playpause);
        timeTextView=view.findViewById(R.id.time);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(),3,LinearLayoutManager.VERTICAL,false));
        fetchVideos();
        return view;
    }
    private void fetchVideos(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<String> list=new ArrayList<>();
                String projection[]={MediaStore.Video.VideoColumns.DATA,MediaStore.Video.Media.DISPLAY_NAME};
                Cursor cursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
                while(cursor.moveToNext()){
                    list.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                }
                cursor.close();
                //Log.d("sachin","Uri is "+list.get(0));
                if(list.size()>0) {
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(list.get(0), MediaStore.Images.Thumbnails.MINI_KIND);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                    videoView.setBackgroundDrawable(bitmapDrawable);
                    videoView.setVideoURI(Uri.parse(list.get(0)));
                    getVideoLength(list.get(0));
                }
                playpause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(videoView.isPlaying()){
                            videoView.pause();
                            playpause.setImageResource(R.drawable.ic_action_play);
                        }else{
                            videoView.setBackgroundDrawable(null);
                            videoView.start();
                            playpause.setImageResource(R.drawable.ic_action_pause);
                        }
                    }
                });
                adapter=new VideosAdapter(getActivity(), list, new VideosAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("sachin",list.get(position));
                        videoView.setVideoURI(Uri.parse(list.get(position)));
                        //videoView.start();
                        if(!videoView.isPlaying()) {
                            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(list.get(position), MediaStore.Images.Thumbnails.MINI_KIND);
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                            videoView.setBackgroundDrawable(bitmapDrawable);
                            getVideoLength(list.get(position));
                        }
                        playpause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(videoView.isPlaying()){
                                    //videoView.stopPlayback();
                                    //videoView.setBackgroundDrawable(null);
                                    videoView.pause();
                                    playpause.setImageResource(R.drawable.ic_action_play);
                                }else{
                                    videoView.setBackgroundDrawable(null);
                                    videoView.start();
                                    playpause.setImageResource(R.drawable.ic_action_pause);
                                }
                            }
                        });
                    }
                });
                recycler.setAdapter(adapter);

            }
        }).start();
    }
    private void getVideoLength(String uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(getActivity(),Uri.parse(uri));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeMillis=Long.parseLong(time);
        long minutes = (timeMillis/1000)/60;
        long seconds = timeMillis/1000-60*minutes;
        if(seconds<10)
        timeTextView.setText(minutes+":0"+seconds);
        else
            timeTextView.setText(minutes+":"+seconds);
    }
}
