package com.example.mdic.data.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;

public class Folder {

    public static final String FOLDER_COLUMN_ID = "id";
    public static final String FOLDER_COLUMN_PARENT_ID = "id";
    public static final String FOLDER_COLUMN_TITLE = "title";
    public static final String FOLDER_COLUMN_DESCRIPTION = "description";
    public static final String FOLDER_COLUMN_IS_FINAL = "isFinal";


    private long id;
    private Long parentId;
    private String title;
    private String description;
    private int isFinal;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(int isFinal) {
        this.isFinal = isFinal;
    }

    public ContentValues getContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", getTitle());
        contentValues.put("description", getDescription());
        contentValues.put("isFinal", getIsFinal());
        //contentValues.put("parentId", getParentId());
        return contentValues;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getTitle();
    }
}
