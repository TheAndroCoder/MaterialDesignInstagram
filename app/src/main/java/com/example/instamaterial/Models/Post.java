package com.example.instamaterial.Models;

public class Post {
    private String id,by_id,post_date,comment_id,type,post_url,post_text,like_id;

    public Post() {
    }

    public Post(String id, String by_id, String post_date, String comment_id, String type, String post_url, String post_text, String like_id) {
        this.id = id;
        this.by_id = by_id;
        this.post_date = post_date;
        this.comment_id = comment_id;
        this.type = type;
        this.post_url = post_url;
        this.post_text = post_text;
        this.like_id = like_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBy_id() {
        return by_id;
    }

    public void setBy_id(String by_id) {
        this.by_id = by_id;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPost_url() {
        return post_url;
    }

    public void setPost_url(String post_url) {
        this.post_url = post_url;
    }

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public String getLike_id() {
        return like_id;
    }

    public void setLike_id(String like_id) {
        this.like_id = like_id;
    }
}
