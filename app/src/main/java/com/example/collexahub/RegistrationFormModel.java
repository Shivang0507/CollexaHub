package com.example.collexahub;

public class RegistrationFormModel {

    public String name;
    public String email;
    public String phone;
    public String enrollment;
    public String department;
    public String semester;

    public String eventId;
    public String uid;
    public String qrCode;

    public boolean verified;
    public long timestamp;

    public RegistrationFormModel() {
        // Required empty constructor for Firebase
    }

    public RegistrationFormModel(
            String name,
            String email,
            String phone,
            String enrollment,
            String department,
            String semester,
            String eventId,
            String uid,
            String qrCode,
            boolean verified,
            long timestamp
    ) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.enrollment = enrollment;
        this.department = department;
        this.semester = semester;
        this.eventId = eventId;
        this.uid = uid;
        this.qrCode = qrCode;
        this.verified = verified;
        this.timestamp = timestamp;
    }
}
