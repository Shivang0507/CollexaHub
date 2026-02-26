package com.example.collexahub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class RegisteredStudentsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";

    private String eventId;
    private boolean isPaidEvent = false;
    private boolean isTeamEvent = false;

    private RecyclerView recyclerView;
    private TextView tvTotalCount;

    private List<RegisteredStudentModel> studentList;
    private RegisteredStudentsAdapter adapter;

    private DatabaseReference registrationRef;
    private DatabaseReference usersRef;
    private DatabaseReference teamRef;

    public RegisteredStudentsFragment() {}

    public static RegisteredStudentsFragment newInstance(String eventId) {
        RegisteredStudentsFragment fragment = new RegisteredStudentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
        );

        registrationRef = database
                .getReference("events")
                .child(eventId)
                .child("registrations");

        teamRef = database
                .getReference("events")
                .child(eventId)
                .child("teams");

        usersRef = database.getReference("users");

        database.getReference("events")
                .child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Boolean paid = snapshot.child("paid").getValue(Boolean.class);
                        isPaidEvent = paid != null && paid;

                        Boolean team = snapshot.child("teamEvent").getValue(Boolean.class);
                        isTeamEvent = team != null && team;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_registered_students,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.recyclerRegisteredStudents);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        studentList = new ArrayList<>();

        adapter = new RegisteredStudentsAdapter(
                studentList,
                qrCode -> {
                    QRDisplayFragment fragment =
                            QRDisplayFragment.newInstance(qrCode);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
        );

        recyclerView.setAdapter(adapter);

        loadData();

        return view;
    }

    private void loadData() {

        FirebaseDatabase.getInstance(
                        "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
                )
                .getReference("events")
                .child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Boolean team = snapshot.child("teamEvent").getValue(Boolean.class);
                        isTeamEvent = team != null && team;

                        if (isTeamEvent) {
                            loadTeams();
                        } else {
                            loadRegistrations();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // ✅ YOUR OLD LOGIC — UNTOUCHED
    private void loadRegistrations() {

        registrationRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        studentList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String uid = ds.child("uid").getValue(String.class);
                            String name = ds.child("name").getValue(String.class);
                            String phone = ds.child("phone").getValue(String.class);
                            String enrollment = ds.child("enrollment").getValue(String.class);
                            String semester = ds.child("semester").getValue(String.class);
                            String qrCode = ds.child("qrCode").getValue(String.class);

                            String paymentStatus = isPaidEvent ? "Paid" : "Free";

                            RegisteredStudentModel model =
                                    new RegisteredStudentModel(
                                            uid,
                                            name != null ? name : "N/A",
                                            phone != null ? phone : "N/A",
                                            enrollment != null ? enrollment : "N/A",
                                            semester != null ? semester : "N/A",
                                            paymentStatus,
                                            qrCode
                                    );

                            studentList.add(model);
                        }

                        tvTotalCount.setText("Total Registered: " + studentList.size());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                }
        );
    }

    // ✅ FIXED TEAM LOADER (STRUCTURED DB)
    private void loadTeams() {

        teamRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        studentList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String teamName = ds.child("teamName").getValue(String.class);
                            String leaderName = ds.child("leader").child("name").getValue(String.class);
                            String leaderPhone = ds.child("leader").child("phone").getValue(String.class);
                            String leaderEnrollment = ds.child("leader").child("enrollment").getValue(String.class);
                            String leaderSemester = ds.child("leader").child("semester").getValue(String.class);
                            String qrCode = ds.child("qrCode").getValue(String.class);

                            String paymentStatus = isPaidEvent ? "Paid" : "Free";

                            RegisteredStudentModel model =
                                    new RegisteredStudentModel(
                                            ds.getKey(),
                                            leaderName != null ? leaderName : "Leader",   // ✅ FIXED
                                            leaderPhone != null ? leaderPhone : "N/A",
                                            leaderEnrollment != null ? leaderEnrollment : "N/A",
                                            leaderSemester != null ? leaderSemester : "N/A",
                                            paymentStatus,
                                            qrCode
                                    );
                            // ================= NEW TEAM DATA (ADDED ONLY) =================

                            model.eventType = "Team";
                            model.teamName = teamName != null ? teamName : "Team";
                            model.leaderSemester = leaderSemester != null ? leaderSemester : "N/A";

// ---- Co-Leader ----
                            String coName = ds.child("coLeader").child("name").getValue(String.class);
                            String coPhone = ds.child("coLeader").child("phone").getValue(String.class);
                            String coEnroll = ds.child("coLeader").child("enrollment").getValue(String.class);
                            String coSem = ds.child("coLeader").child("semester").getValue(String.class);

                            model.coLeaderDetails =
                                    "\n" + "Name: " + (coName != null ? coName : "N/A") + "\n" +
                                            "Enrollment: " + (coEnroll != null ? coEnroll : "N/A") + "\n" +
                                            "Semester: " + (coSem != null ? coSem : "N/A") + "\n" +
                                            "Contact: " + (coPhone != null ? coPhone : "N/A") ;

// ---- Members ----
                            StringBuilder membersBuilder = new StringBuilder();
                            int count = 1;

                            for (DataSnapshot memberSnap : ds.child("members").getChildren()) {

                                String mName = memberSnap.child("name").getValue(String.class);
                                String mPhone = memberSnap.child("phone").getValue(String.class);
                                String mEnroll = memberSnap.child("enrollment").getValue(String.class);
                                String mSem = memberSnap.child("semester").getValue(String.class);

                                membersBuilder.append("\n👥Member ").append(count).append("\n\n")
                                        .append("Name: ").append(mName != null ? mName : "N/A").append("\n")
                                        .append("Enrollment: ").append(mEnroll != null ? mEnroll : "N/A").append("\n")
                                        .append("Contact: ").append(mPhone != null ? mPhone : "N/A").append("\n")
                                        .append("Semester: ").append(mSem != null ? mSem : "N/A").append("\n");

                                count++;
                            }

                            model.membersDetails = membersBuilder.toString();

                            studentList.add(model);
                        }

                        tvTotalCount.setText("Total Teams: " + studentList.size());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                }
        );
    }
}