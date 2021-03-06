package com.fisheradelakin.prophet.model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by temidayo on 2/20/16.
 */
public class Poem extends RealmObject implements Serializable {

    @Required // title cannot be null
    private String title;
    @Required // poem cannot be null
    private String poem;
    @Required
    private String timestamp;
    private String author; // author is optional

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoem() {
        return poem;
    }

    public void setPoem(String poem) {
        this.poem = poem;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
