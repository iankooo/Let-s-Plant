package com.e.letsplant.data;

import com.e.letsplant.PostModel;

import java.util.ArrayList;

public class Post {
    private String ownerName;

    public Post(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return this.ownerName;
    }
}
