package com.example.mdic.data.model;

import android.content.ContentValues;
import androidx.annotation.NonNull;

public class Card {

    private long id;
    private long folderId;
    private int like;//like count
    private int disLike;//dislike count
    private int show;//boolean
    private String key;//word
    private String value;//meaning
    private String description;
    private String pronunciation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDisLike() {
        return disLike;
    }

    public void setDisLike(int disLike) {
        this.disLike = disLike;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }


    public ContentValues getContentValues(){
        ContentValues contentValues = new ContentValues();
        //contentValues.put("id", getId());
        contentValues.put("folderId", getFolderId());
        contentValues.put("like", getLike());
        contentValues.put("disLike", getDisLike());
        contentValues.put("show", getShow());
        contentValues.put("key", getKey());
        contentValues.put("value", getValue());
        contentValues.put("description", getDescription());
        contentValues.put("pronunciation", getPronunciation());
        return contentValues;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getKey();
    }
}
