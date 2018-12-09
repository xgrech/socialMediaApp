package gmd.socialmediaapp;

import com.google.firebase.Timestamp;

public class Post {

    private String type;
    private String videourl;
    private String imageurl;
    private String username;
    private Timestamp date;
    private String userid;

    public Post(){};

    public Post(String type, String videourl, String imageurl, String username, Timestamp date, String userid) {
        this.type = type;
        this.videourl = videourl;
        this.imageurl = imageurl;
        this.username = username;
        this.date = date;
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
