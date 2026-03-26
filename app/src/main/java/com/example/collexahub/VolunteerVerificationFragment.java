package com.example.collexahub;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.*;

public class VolunteerVerificationFragment extends Fragment {

    private static final String DB_URL =
            "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app";

    private String scannedQR;

    private TextView tvEventTitle, tvStatus, tvIndividualDetails;
    private Button btnVerify;

    private LinearLayout layoutTeamSection, layoutTeamDetails;
    private TextView tvTeamName, tvLeaderName,
            tvCoLeaderDetails, tvMembersDetails,
            tvTeamPaymentStatus;
    private Button btnSeeMore;

    public static VolunteerVerificationFragment newInstance(String qrCode) {
        VolunteerVerificationFragment fragment =
                new VolunteerVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("qrCode", qrCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_volunteer_verification,
                container,
                false);

        tvEventTitle = view.findViewById(R.id.tvEventTitle);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvIndividualDetails = view.findViewById(R.id.tvIndividualDetails);
        btnVerify = view.findViewById(R.id.btnVerify);

        layoutTeamSection = view.findViewById(R.id.layoutTeamSection);
        layoutTeamDetails = view.findViewById(R.id.layoutTeamDetails);

        tvTeamName = view.findViewById(R.id.tvTeamName);
        tvLeaderName = view.findViewById(R.id.tvLeaderName);
        tvCoLeaderDetails = view.findViewById(R.id.tvCoLeaderDetails);
        tvMembersDetails = view.findViewById(R.id.tvMembersDetails);
        tvTeamPaymentStatus = view.findViewById(R.id.tvTeamPaymentStatus);

        btnSeeMore = view.findViewById(R.id.btnSeeMore);

        if (getArguments() != null) {
            scannedQR = getArguments().getString("qrCode");
        }

        searchRegistration();

        return view;
    }

    private void searchRegistration() {

        if (scannedQR == null || scannedQR.trim().isEmpty()) {
            Toast.makeText(getContext(),
                    "Invalid QR",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String cleanQR = scannedQR.trim();

        int first = cleanQR.indexOf("_");
        int last = cleanQR.lastIndexOf("_");

        if (first == -1 || last == -1 || first == last) {
            Toast.makeText(getContext(),
                    "Invalid QR Format",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = cleanQR.substring(0, first);
        String uid = cleanQR.substring(first + 1, last);

        FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);

        DatabaseReference eventRef = db
                .getReference("events")
                .child(eventId);

        eventRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot eventSnap) {

                        if (!eventSnap.exists()) {
                            Toast.makeText(getContext(),
                                    "Event not found",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String eventTitle =
                                eventSnap.child("title")
                                        .getValue(String.class);

                        Boolean paid =
                                eventSnap.child("paid")
                                        .getValue(Boolean.class);

                        // ===== TEAM =====
                        for (DataSnapshot teamSnap :
                                eventSnap.child("teams").getChildren()) {

                            String qr =
                                    teamSnap.child("qrCode")
                                            .getValue(String.class);

                            if (qr != null &&
                                    cleanQR.equalsIgnoreCase(qr.trim())) {

                                showTeamDetails(teamSnap, eventTitle, paid);
                                return;
                            }
                        }

                        // ===== INDIVIDUAL =====

                        DataSnapshot regSnap = eventSnap
                                .child("registrations")
                                .child(uid);

                        if (regSnap.exists()) {
                            showIndividualDetails(regSnap, eventTitle, paid);
                            return;
                        }

                        Toast.makeText(getContext(),
                                "QR not found in this event",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void showIndividualDetails(DataSnapshot snapshot,
                                       String eventTitle,
                                       Boolean paid) {

        layoutTeamSection.setVisibility(View.GONE);
        tvIndividualDetails.setVisibility(View.VISIBLE);

        tvEventTitle.setText("Event: " + eventTitle);

        String paymentStatus = (paid != null && paid) ? "Paid" : "Free";

        tvIndividualDetails.setText(
                "👤 Participant\n\n" +
                        "Name: " + snapshot.child("name").getValue(String.class) + "\n" +
                        "Enrollment: " + snapshot.child("enrollment").getValue(String.class) + "\n" +
                        "Semester: " + snapshot.child("semester").getValue(String.class) + "\n" +
                        "Contact: " + snapshot.child("phone").getValue(String.class) + "\n\n" +
                        "Payment: " + paymentStatus
        );

        Boolean verified = snapshot.child("verified").getValue(Boolean.class);
        if (verified == null) verified = false;

        handleVerification(snapshot.getRef(), verified);
    }

    private void showTeamDetails(DataSnapshot snapshot,
                                 String eventTitle,
                                 Boolean paid) {

        tvIndividualDetails.setVisibility(View.GONE);
        layoutTeamSection.setVisibility(View.VISIBLE);
        layoutTeamDetails.setVisibility(View.GONE);
        btnSeeMore.setText("See More Details");

        tvEventTitle.setText("Event: " + eventTitle);

        tvTeamName.setText("Team: " +
                snapshot.child("teamName").getValue(String.class));

        tvLeaderName.setText(
                "⭐ Leader\n" +
                        "Name: " + snapshot.child("leader").child("name").getValue(String.class) + "\n" +
                        "Enrollment: " + snapshot.child("leader").child("enrollment").getValue(String.class) + "\n" +
                        "Semester: " + snapshot.child("leader").child("semester").getValue(String.class) + "\n" +
                        "Contact: " + snapshot.child("leader").child("phone").getValue(String.class)
        );

        tvCoLeaderDetails.setText(
                "⭐ Co-Leader\n" +
                        "Name: " + snapshot.child("coLeader").child("name").getValue(String.class) + "\n" +
                        "Enrollment: " + snapshot.child("coLeader").child("enrollment").getValue(String.class) + "\n" +
                        "Semester: " + snapshot.child("coLeader").child("semester").getValue(String.class) + "\n" +
                        "Contact: " + snapshot.child("coLeader").child("phone").getValue(String.class)
        );

        StringBuilder members = new StringBuilder();
        int count = 1;

        for (DataSnapshot m :
                snapshot.child("members").getChildren()) {

            members.append("\nMember ").append(count).append("\n")
                    .append("Name: ").append(m.child("name").getValue(String.class)).append("\n")
                    .append("Enrollment: ").append(m.child("enrollment").getValue(String.class)).append("\n")
                    .append("Semester: ").append(m.child("semester").getValue(String.class)).append("\n")
                    .append("Contact: ").append(m.child("phone").getValue(String.class)).append("\n");

            count++;
        }

        tvMembersDetails.setText(members.toString());

        String paymentStatus = (paid != null && paid) ? "Paid" : "Free";
        tvTeamPaymentStatus.setText("Payment: " + paymentStatus);
        tvTeamPaymentStatus.setTextColor(Color.parseColor("#2E7D32"));

        btnSeeMore.setOnClickListener(v -> {
            if (layoutTeamDetails.getVisibility() == View.GONE) {
                layoutTeamDetails.setVisibility(View.VISIBLE);
                btnSeeMore.setText("Hide Details");
            } else {
                layoutTeamDetails.setVisibility(View.GONE);
                btnSeeMore.setText("See More Details");
            }
        });

        Boolean verified = snapshot.child("verified").getValue(Boolean.class);
        if (verified == null) verified = false;

        handleVerification(snapshot.getRef(), verified);
    }

    private void handleVerification(DatabaseReference ref,
                                    Boolean verified) {

        if (verified) {
            tvStatus.setText("Status: VERIFIED");
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            btnVerify.setEnabled(false);
        } else {
            tvStatus.setText("Status: NOT VERIFIED");
            tvStatus.setTextColor(Color.parseColor("#D32F2F"));
            btnVerify.setEnabled(true);

            btnVerify.setOnClickListener(v -> {
                ref.child("verified").setValue(true);
                ref.child("verifiedAt")
                        .setValue(System.currentTimeMillis());

                tvStatus.setText("Status: VERIFIED");
                tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                btnVerify.setEnabled(false);

                Toast.makeText(getContext(),
                        "Verified Successfully",
                        Toast.LENGTH_SHORT).show();
            });
        }
    }
}