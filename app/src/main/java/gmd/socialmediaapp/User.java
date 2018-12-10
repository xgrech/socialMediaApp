package gmd.socialmediaapp;

import com.google.firebase.Timestamp;

public class User {
    private String username;
    private Timestamp date;
    private int numberOfPosts;

    public User(){}

    public User(String username, Timestamp date, int numberOfPosts) {
        this.username = username;
        this.date = date;
        this.numberOfPosts = numberOfPosts;
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

    public int getNumberOfPosts() {
        return numberOfPosts;
    }

    public void setNumberOfPosts(int numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
    }
}
