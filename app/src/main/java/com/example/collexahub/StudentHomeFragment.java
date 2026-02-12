package com.example.collexahub;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StudentHomeFragment extends Fragment
        implements OnEventRegisterClickListener {

    private RecyclerView rvEvents;
    private EventAdapter adapter;
    private List<EventModel> eventList;
    private DatabaseReference eventRef;
    private String currentUid;

    private static final String DB_URL =
            "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_student_home,
                container,
                false
        );

        rvEvents = view.findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        currentUid = FirebaseAuth.getInstance().getUid();

        adapter = new EventAdapter(
                eventList,
                "student",
                currentUid,
                (OnEventRegisterClickListener) requireActivity()
        );


        rvEvents.setAdapter(adapter);

        eventRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("events");

        loadEvents();

        return view;
    }

    private void loadEvents() {
        eventRef.orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        eventList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            EventModel event = ds.getValue(EventModel.class);
                            if (event != null) {
                                eventList.add(event);
                            }
                        }

                        Collections.reverse(eventList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Failed to load events",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 🔵 Register Click
    @Override
    public void onRegisterClick(String eventId) {

        if (currentUid == null) return;

        String qrCode = UUID.randomUUID().toString();

        DatabaseReference regRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("events")
                .child(eventId)
                .child("registrations")
                .child(currentUid);

        regRef.child("qrCode").setValue(qrCode)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(),
                            "Registered Successfully",
                            Toast.LENGTH_SHORT).show();
                });
    }

    // 🟢 My QR Click
    @Override
    public void onMyQRClick(String eventId) {

        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL)
                .getReference("events")
                .child(eventId)
                .child("registrations")
                .child(currentUid)
                .child("qrCode");

        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String qrCode = snapshot.getValue(String.class);

                QRDisplayFragment fragment =
                        QRDisplayFragment.newInstance(qrCode);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
