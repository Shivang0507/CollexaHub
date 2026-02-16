package com.example.collexahub;

public class UserModel {

    public String fullName;
    public String email;
    public String mobile;
    public String gender;
    public String role;

    public UserModel() {
    }

    public UserModel(String fullName, String email, String mobile, String gender ,String role) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.role = role;
    }
}
