package com.khdamte.bitcode.khdamte_app.models;

/**
 * Created by Amado on 8/10/2017.
 */

public class CommentModels {

    private String comment_content, username, date;

    public CommentModels(String comment_content, String username, String date) {
        this.comment_content = comment_content;
        this.username = username;
        this.date = date;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
