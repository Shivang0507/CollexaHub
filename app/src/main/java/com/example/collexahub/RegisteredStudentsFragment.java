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
    private boolean isPaidEvent = false; // ✅ ADDED

    private RecyclerView recyclerView;
    private TextView tvTotalCount;

    private List<RegisteredStudentModel> studentList;
    private RegisteredStudentsAdapter adapter;

    private DatabaseReference registrationRef;
    private DatabaseReference usersRef;

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

        usersRef = database.getReference("users");

        // ✅ ADDED: Fetch paid status from event node
        database.getReference("events")
                .child(eventId)
                .child("paid")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean paid = snapshot.getValue(Boolean.class);
                        isPaidEvent = paid != null && paid;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
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

        loadRegistrations();

        return view;
    }

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
                            Boolean verified = ds.child("verified").getValue(Boolean.class);

                            // ✅ UPDATED PAYMENT LOGIC
                            String paymentStatus = null;

                            if (isPaidEvent) {
                                paymentStatus = "Paid";
                            } else {
                                paymentStatus = "Free";
                            }

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
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                }
        );
    }

    private void fetchUserDetails(String uid,
                                  String qrCode,
                                  String paymentStatus) {

        usersRef.child(uid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String name = snapshot.child("name").getValue(String.class);
                                String phone = snapshot.child("phone").getValue(String.class);
                                String enrollment = snapshot.child("enrollmentNo").getValue(String.class);
                                String semester = snapshot.child("semester").getValue(String.class);

                                RegisteredStudentModel model =
                                        new RegisteredStudentModel(
                                                uid,
                                                name != null ? name : "N/A",
                                                phone != null ? phone : "N/A",
                                                enrollment != null ? enrollment : "N/A",
                                                semester != null ? semester : "N/A",
                                                paymentStatus != null ? paymentStatus : "Unpaid",
                                                qrCode
                                        );

                                studentList.add(model);

                                tvTotalCount.setText("Total Registered: " + studentList.size());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
    }
}
