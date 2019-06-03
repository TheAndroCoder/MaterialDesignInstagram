package com.example.instamaterial.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.instamaterial.Models.Post;
import com.example.instamaterial.Models.User;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    private static final String DB_NAME = "INSTA_DB";
    private static final String USER_TABLE = "Users";
    private static final String POST_TABLE = "Posts";
    private static final String COMMENT_TABLE = "Comments";
    //USER TABLE COLUMN NAMES
    private static final String USER_ID = "id";
    private static final String USER_NAME = "name";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";
    private static final String PROFILE_PICTURE_URL = "dp_url";
    //POSTS TABLE COLUMN NAMES
    private static final String POST_ID="id";
    private static final String BY_ID="by_id";
    private static final String POST_DATE="post_date";
    private static final String COMMENT_ID="comment_id";
    private static final String POST_TYPE="type";
    private static final String POST_URL="post_url";
    private static final String POST_TEXT="post_text";
    private static final String LIKE_ID="like_id";

    //COMMENTS TABLE COLUMN NAMES
    private static final String COMMENT_TEXT="comment_text";
    private static final String COMMENT_DATE="comment_date";
    private static final String COMMENT_BY="comment_by";

    //QUERIES FOR TABLE CREATION
    private static final String CREATE_TABLE_USERS = "create table "+USER_TABLE+"("+USER_ID+" text primary key,"+USER_NAME+" text,"+USER_EMAIL+" text,"+USER_PASSWORD+" text,"+PROFILE_PICTURE_URL+" text)";
    private static final String CREATE_TABLE_POSTS = "create table "+POST_TABLE+"("+POST_ID+" text primary key,"+BY_ID+" text,"+POST_DATE+" text,"+COMMENT_ID+" text,"+POST_TYPE+" text,"+POST_URL+" text,"+POST_TEXT+" text,"+LIKE_ID+" text)";
    private static final String CREATE_TABLE_COMMENTS = "create table "+COMMENT_TABLE+"("+COMMENT_ID+" text primary key,"+COMMENT_TEXT+" text,"+COMMENT_DATE+" text,"+COMMENT_BY+" text)";
    private DatabaseHelper(Context context) {
        super(context,DB_NAME,null,1);
    }
    public static DatabaseHelper getInstance(Context context){
        if(instance==null){
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
            instance=new DatabaseHelper(context);
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_POSTS);
        //db.execSQL(CREATE_TABLE_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+POST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+COMMENT_TABLE);
        onCreate(db);
    }
    public void updateUsers(ArrayList<User> users){
        SQLiteDatabase db=this.getWritableDatabase();
        for(User user : users){
            Cursor cursor=db.rawQuery("select * from "+USER_TABLE+" where "+USER_ID+"='"+user.getUid()+"'",null);
            if(!cursor.moveToFirst()){
                db.execSQL("insert into "+USER_TABLE+" values('"+user.getUid()+"','"+user.getName()+"','"+user.getEmail()+"','"+user.getPassword()+"','"+user.getDp_url()+"'");
            }
        }
    }
    public void fetchAllUsers(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+USER_TABLE,null);
        while(cursor.moveToNext()){
            Log.d("sachin","fetched "+cursor.getString(cursor.getColumnIndex(USER_ID)));
        }
        Log.d("sachin","kintu eta call korechi");
    }
    public void insertPost(Post post){
        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY = "insert into "+POST_TABLE+" values('"+post.getId()+"','"+post.getBy_id()+"','"+post.getPost_date()+"','"+post.getComment_id()+"','"+post.getType()+"','"+post.getPost_url()+"','"+post.getPost_text()+"','"+post.getLike_id()+"')";
        db.execSQL(QUERY);
    }
}
