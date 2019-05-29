package com.example.instamaterial.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
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

    //COMMENTS TABLE COLUMN NAMES

    //QUERIES FOR TABLE CREATION
    private static final String CREATE_TABLE_USERS = "create table "+USER_TABLE+"("+USER_ID+" text primary key,"+USER_NAME+" text,"+USER_EMAIL+" text,"+USER_PASSWORD+" text,"+PROFILE_PICTURE_URL+" text)";
    private static final String CREATE_TABLE_POSTS = "";
    private static final String CREATE_TABLE_COMMENTS = "";
    public DatabaseHelper(@Nullable Context context) {
        super(context,DB_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_POSTS);
        db.execSQL(CREATE_TABLE_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+POST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+COMMENT_TABLE);
        onCreate(db);
    }
}
