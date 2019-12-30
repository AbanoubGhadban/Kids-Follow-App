package com.golden.kidsfollow.models;

public class Kid {
    private String name;
    private String note;
    private String thumbnailUrl;
    private String photoUrl;

    public Kid() {}

    public Kid(String name, String note) {
        this.name = name;
        this.note = note;
    }

    public Kid(String name, String note, String thumbnailUrl, String photoUrl) {
        this.name = name;
        this.note = note;
        this.thumbnailUrl = thumbnailUrl;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
