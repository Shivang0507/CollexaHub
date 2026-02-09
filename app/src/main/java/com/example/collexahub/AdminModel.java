package com.example.collexahub;

public class AdminModel {

    private String fullName;
    private String email;
    private String mobile;
    private String gender;
    private String role;
    private String uid;

    public AdminModel() {}

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getGender() { return gender; }
    public String getRole() { return role; }
    public String getUid() { return uid; }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
