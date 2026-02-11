package com.example.collexahub;

public class RegistrationFormModel {

    public String name;
    public String email;
    public String phone;
    public String enrollment;
    public String department;
    public String semester;
    public long timestamp;

    public RegistrationFormModel() {}

    public RegistrationFormModel(
            String name,
            String email,
            String phone,
            String enrollment,
            String department,
            String semester,
            long timestamp) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.enrollment = enrollment;
        this.department = department;
        this.semester = semester;
        this.timestamp = timestamp;
    }
}
