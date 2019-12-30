package com.golden.kidsfollow.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Classroom {
    private String name;
    private String description;

    public Classroom() {}

    public Classroom(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
