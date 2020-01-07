package com.example.instagram.Models;

public class User {
    public User() {

    }

    String id;
    String username;
    String fullname;
    String imgUrl;
    String bio;

    public User(String id, String username, String fullname, String imgUrl, String bio) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imgUrl = imgUrl;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
