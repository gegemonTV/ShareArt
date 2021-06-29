package com.minerva.shareart.post;

import android.net.Uri;

public class Post {
    public String user_id, description, category;
    public String image_url;
    public long likes, post_time;

    public Post() {
    }

    public Post(String user_id, String description, String category, String image_url, long likes, long post_time) {
        this.user_id = user_id;
        this.description = description;
        this.category = category;
        this.image_url = image_url;
        this.likes = likes;
        this.post_time = post_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "user_id='" + user_id + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", image_url=" + image_url +
                ", likes=" + likes +
                '}';
    }
}
