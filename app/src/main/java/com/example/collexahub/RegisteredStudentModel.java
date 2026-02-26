package com.example.collexahub;

public class RegisteredStudentModel {

    // ================= OLD FIELDS (UNCHANGED) =================
    public String uid;
    public String name;
    public String phone;
    public String enrollmentNo;
    public String semester;
    public String paymentStatus;
    public String qrCode;

    public String eventTitle;

    // ================= NEW TEAM FIELDS (ADDED ONLY) =================
    public String eventType;           // "Individual" or "Team"
    public String teamName;

    public String leaderSemester;      // Leader semester (for team display)

    public String coLeaderDetails;     // Full formatted co-leader details
    public String membersDetails;      // Full formatted members list

    // ================= EMPTY CONSTRUCTOR =================
    public RegisteredStudentModel() {
        // Required empty constructor
    }

    // ================= OLD CONSTRUCTOR (UNCHANGED) =================
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