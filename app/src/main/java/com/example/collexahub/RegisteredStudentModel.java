package com.example.collexahub;


public class RegisteredStudentModel {

    public String uid;
    public String name;
    public String phone;
    public String enrollmentNo;
    public String semester;
    public String paymentStatus;
    public String qrCode;

    public RegisteredStudentModel() {
        // Required empty constructor
    }

    public RegisteredStudentModel(String uid,
                                  String name,
                                  String phone,
                                  String enrollmentNo,
                                  String semester,
                                  String paymentStatus,
                                  String qrCode) {

        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.enrollmentNo = enrollmentNo;
        this.semester = semester;
        this.paymentStatus = paymentStatus;
        this.qrCode = qrCode;
    }
}
