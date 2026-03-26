package com.example.collexahub;

public class RegisteredStudentModel {


    public String uid;
    public String name;
    public String phone;
    public String enrollmentNo;
    public String semester;
    public String paymentStatus;
    public String qrCode;

    public String eventTitle;

    // ================= NEW TEAM FIELDS =================
    public String eventType;
    public String teamName;

    public String leaderSemester;

    public String coLeaderDetails;
    public String membersDetails;


    public RegisteredStudentModel() {
    }

    public RegisteredStudentModel(String uid,
                                  String name,
                                  String phone,
                                  String enrollmentNo,
                                  String semester,
                                  String paymentStatus,
                                  String qrCode,
                                  String eventTitle) {

        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.enrollmentNo = enrollmentNo;
        this.semester = semester;
        this.paymentStatus = paymentStatus;
        this.qrCode = qrCode;
        this.eventTitle = eventTitle;
    }
}