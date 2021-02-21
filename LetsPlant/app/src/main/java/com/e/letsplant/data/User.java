package com.e.letsplant.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String email;
    private String location;
    private String phone;
    private String profileImage = "";
    private String username;

    public User() {}

    public User(String location, String phone, String profileImage, String username) {
        this.location = location;
        this.phone = phone;
        this.profileImage = profileImage;
        this.username = username;
    }

    public User(String email, String location, String phone, String profileImage, String username) {
        this.email = email;
        this.location = location;
        this.phone = phone;
        this.profileImage = profileImage;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("location", location);
        result.put("phone", phone);
        result.put("profileImage", profileImage);
        result.put("username", username);

        return result;
    }

}
