package com.example.collexahub;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRegistrationFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_EVENT_TITLE = "event_title";
    private static final String ARG_PAID = "event_paid";
    private static final String ARG_AMOUNT = "event_amount";
    private static final String ARG_TEAM_EVENT = "team_event";
    private static final String ARG_TEAM_SIZE = "team_size";

    private String eventId, eventTitle, entryFee;
    private boolean isPaid;
    private boolean isTeamEvent;
    private int maxTeamSize;

    private String currentUid;

    private EditText etName, etEmail, etPhone,
            etEnrollment, etDepartment, etSem;

    private LinearLayout layoutIndividualSection; // ✅ ADDED
    private LinearLayout layoutTeamSection;       // ✅ ADDED

    private LinearLayout layoutTeamMembers;
    private EditText etTeamName;

    private List<Map<String, EditText>> teamMemberInputs = new ArrayList<>();

    public static EventRegistrationFragment newInstance(
            String eventId, String eventTitle,
            boolean isPaid, String entryFee,
            boolean isTeamEvent, int maxTeamSize) {

        EventRegistrationFragment fragment = new EventRegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_EVENT_TITLE, eventTitle);
        args.putBoolean(ARG_PAID, isPaid);
        args.putString(ARG_AMOUNT, entryFee);
        args.putBoolean(ARG_TEAM_EVENT, isTeamEvent);
        args.putInt(ARG_TEAM_SIZE, maxTeamSize);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_event_registration,
                container, false);

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            eventTitle = getArguments().getString(ARG_EVENT_TITLE);
            isPaid = getArguments().getBoolean(ARG_PAID);
            entryFee = getArguments().getString(ARG_AMOUNT);
            isTeamEvent = getArguments().getBoolean(ARG_TEAM_EVENT, false);
            maxTeamSize = getArguments().getInt(ARG_TEAM_SIZE, 0);
        }

        currentUid = FirebaseAuth.getInstance().getUid();

        // OLD FIELDS (UNCHANGED)
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etEnrollment = view.findViewById(R.id.etEnrollment);
        etDepartment = view.findViewById(R.id.etDepartment);
        etSem = view.findViewById(R.id.etSem);

        // ✅ NEW VISIBILITY LAYOUTS
        layoutIndividualSection = view.findViewById(R.id.layoutIndividualSection);
        layoutTeamSection = view.findViewById(R.id.layoutTeamSection);

        layoutTeamMembers = view.findViewById(R.id.layoutTeamMembers);
        etTeamName = view.findViewById(R.id.etTeamName);

        // ✅ VISIBILITY CONTROL
        if (isTeamEvent) {
            layoutIndividualSection.setVisibility(View.GONE);
            layoutTeamSection.setVisibility(View.VISIBLE);
        } else {
            layoutIndividualSection.setVisibility(View.VISIBLE);
            layoutTeamSection.setVisibility(View.GONE);
        }

        layoutTeamMembers.removeAllViews();
        teamMemberInputs.clear();

        if (isTeamEvent) {

            layoutTeamSection.setVisibility(View.VISIBLE);

            // ==========================
            // TEAM LEADER SECTION
            // ==========================

            TextView leaderTitle = new TextView(getContext());
            leaderTitle.setText("Team Leader Details");
            leaderTitle.setTextSize(16);
            leaderTitle.setPadding(0, 20, 0, 10);
            layoutTeamMembers.addView(leaderTitle);

            Map<String, EditText> leaderMap = new HashMap<>();

            EditText leaderName = createStyledEditText("Leader Name *");
            EditText leaderEmail = createStyledEditText("Leader Email *");
            EditText leaderPhone = createStyledEditText("Leader Phone *");
            EditText leaderEnrollment = createStyledEditText("Leader Enrollment No *");
            EditText leaderSemester = createStyledEditText("Leader Semester *");

            layoutTeamMembers.addView(leaderName);
            layoutTeamMembers.addView(leaderEmail);
            layoutTeamMembers.addView(leaderPhone);
            layoutTeamMembers.addView(leaderEnrollment);
            layoutTeamMembers.addView(leaderSemester);

            leaderMap.put("name", leaderName);
            leaderMap.put("email", leaderEmail);
            leaderMap.put("phone", leaderPhone);
            leaderMap.put("enrollment", leaderEnrollment);
            leaderMap.put("semester", leaderSemester);

            teamMemberInputs.add(leaderMap);


            // ==========================
            // CO-LEADER SECTION
            // ==========================

            TextView coTitle = new TextView(getContext());
            coTitle.setText("Co-Leader Details");
            coTitle.setTextSize(16);
            coTitle.setPadding(0, 20, 0, 10);
            layoutTeamMembers.addView(coTitle);

            Map<String, EditText> coLeaderMap = new HashMap<>();

            EditText coName = createStyledEditText("Co-Leader Name *");
            EditText coPhone = createStyledEditText("Co-Leader Phone *");
            EditText coEnrollment = createStyledEditText("Co-Leader Enrollment No *");
            EditText coSemester = createStyledEditText("Co-Leader Semester *");

            layoutTeamMembers.addView(coName);
            layoutTeamMembers.addView(coPhone);
            layoutTeamMembers.addView(coEnrollment);
            layoutTeamMembers.addView(coSemester);

            coLeaderMap.put("name", coName);
            coLeaderMap.put("phone", coPhone);
            coLeaderMap.put("enrollment", coEnrollment);
            coLeaderMap.put("semester", coSemester);

            teamMemberInputs.add(coLeaderMap);


            // ==========================
// MEMBERS SECTION
// ==========================

            TextView memberTitle = new TextView(getContext());
            memberTitle.setText("Members");
            memberTitle.setTextSize(16);
            memberTitle.setPadding(0, 20, 0, 10);
            layoutTeamMembers.addView(memberTitle);

            int remainingMembers = maxTeamSize - 2;

            for (int i = 1; i <= remainingMembers; i++) {

                // 🔥 ADD TITLE FOR EACH MEMBER
                TextView memberHeader = new TextView(getContext());
                memberHeader.setText("Member " + i + " Details");
                memberHeader.setTextSize(15);
                memberHeader.setPadding(0, 15, 0, 8);
                layoutTeamMembers.addView(memberHeader);

                Map<String, EditText> memberMap = new HashMap<>();

                EditText name = createStyledEditText("Member " + i + " Name *");
                EditText phone = createStyledEditText("Member " + i + " Phone *");
                EditText enrollment = createStyledEditText("Member " + i + " Enrollment No *");
                EditText semester = createStyledEditText("Member " + i + " Semester *");

                layoutTeamMembers.addView(name);
                layoutTeamMembers.addView(phone);
                layoutTeamMembers.addView(enrollment);
                layoutTeamMembers.addView(semester);

                memberMap.put("name", name);
                memberMap.put("phone", phone);
                memberMap.put("enrollment", enrollment);
                memberMap.put("semester", semester);

                teamMemberInputs.add(memberMap);
            }
        }

        view.findViewById(R.id.btnSubmitRegistration)
                .setOnClickListener(v -> submitForm());

        return view;
    }

    private EditText createStyledEditText(String hint) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        EditText editText = (EditText) inflater.inflate(
                R.layout.item_team_member_field,
                layoutTeamMembers,
                false
        );

        editText.setHint(hint);
        return editText;
    }

    // ==============================
    // REST OF YOUR ORIGINAL LOGIC
    // ==============================

    private void submitForm() {

        if (!isTeamEvent) {

            if (TextUtils.isEmpty(etName.getText())
                    || TextUtils.isEmpty(etEmail.getText())
                    || TextUtils.isEmpty(etPhone.getText())
                    || TextUtils.isEmpty(etEnrollment.getText())
                    || TextUtils.isEmpty(etSem.getText())) {

                Toast.makeText(getContext(),
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (isTeamEvent) {

            if (TextUtils.isEmpty(etTeamName.getText())) {
                etTeamName.setError("Enter Team Name");
                return;
            }

            for (Map<String, EditText> member : teamMemberInputs) {
                for (EditText field : member.values()) {
                    if (TextUtils.isEmpty(field.getText())) {
                        field.setError("Required");
                        return;
                    }
                }
            }
        }

        if (isPaid) {
            startPayment();
        } else {
            saveRegistration(null);
        }
    }

    private void startPayment() {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_SFaF2tMseHXUIj");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Collexa Hub");
            options.put("description", eventTitle);
            options.put("currency", "INR");

            int amount = Integer.parseInt(entryFee) * 100;
            options.put("amount", amount);

            checkout.open(requireActivity(), options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePaymentSuccess(String paymentId) {
        Toast.makeText(getContext(),
                "Payment Successful",
                Toast.LENGTH_SHORT).show();
        saveRegistration(paymentId);
    }

    private void saveRegistration(String paymentId) {

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
        );

        if (!isTeamEvent) {

            String qrCode = eventId + "_" + currentUid + "_" + System.currentTimeMillis();

            RegistrationFormModel model =
                    new RegistrationFormModel(
                            etName.getText().toString(),
                            etEmail.getText().toString(),
                            etPhone.getText().toString(),
                            etEnrollment.getText().toString(),
                            etDepartment.getText().toString(),
                            etSem.getText().toString(),
                            eventId,
                            currentUid,
                            qrCode,
                            false,
                            System.currentTimeMillis()
                    );

            DatabaseReference regRef = db.getReference("events")
                    .child(eventId)
                    .child("registrations")
                    .child(currentUid);

            regRef.setValue(model)
                    .addOnSuccessListener(a -> {

                        // 🔥 ONLY NEW ADDITION (nothing else changed)
                        regRef.child("eventTitle").setValue(eventTitle);

                        navigateHome();
                    });

        } else {

            String teamId = db.getReference("events")
                    .child(eventId)
                    .child("teams")
                    .push()
                    .getKey();

            String qrCode = eventId + "_" + teamId + "_" + System.currentTimeMillis();

            Map<String, Object> teamData = new HashMap<>();

            teamData.put("teamName", etTeamName.getText().toString());
            teamData.put("eventTitle", eventTitle);
            teamData.put("qrCode", qrCode);
            teamData.put("leaderUid", currentUid);
            teamData.put("verified", false);
            teamData.put("verifiedAt", 0L);

            Map<String, Object> leader = new HashMap<>();

            leader.put("name", teamMemberInputs.get(0).get("name").getText().toString());
            leader.put("email", teamMemberInputs.get(0).get("email").getText().toString());
            leader.put("phone", teamMemberInputs.get(0).get("phone").getText().toString());
            leader.put("enrollment", teamMemberInputs.get(0).get("enrollment").getText().toString());
            leader.put("semester", teamMemberInputs.get(0).get("semester").getText().toString());

            teamData.put("leader", leader);

            Map<String, Object> coLeader = new HashMap<>();

            coLeader.put("name", teamMemberInputs.get(1).get("name").getText().toString());
            coLeader.put("phone", teamMemberInputs.get(1).get("phone").getText().toString());
            coLeader.put("enrollment", teamMemberInputs.get(1).get("enrollment").getText().toString());
            coLeader.put("semester", teamMemberInputs.get(1).get("semester").getText().toString());

            teamData.put("coLeader", coLeader);

            Map<String, Object> members = new HashMap<>();

            for (int i = 2; i < teamMemberInputs.size(); i++) {

                Map<String, Object> member = new HashMap<>();

                member.put("name", teamMemberInputs.get(i).get("name").getText().toString());
                member.put("phone", teamMemberInputs.get(i).get("phone").getText().toString());
                member.put("enrollment", teamMemberInputs.get(i).get("enrollment").getText().toString());
                member.put("semester", teamMemberInputs.get(i).get("semester").getText().toString());

                members.put("member" + (i - 1), member);
            }

            teamData.put("members", members);

            db.getReference("events")
                    .child(eventId)
                    .child("teams")
                    .child(teamId)
                    .setValue(teamData)
                    .addOnSuccessListener(a -> navigateHome());
        }
    }

    private void navigateHome() {
        requireActivity()
                .getSupportFragmentManager()
                .popBackStack(null,
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new StudentHomeFragment())
                .commit();
    }
}