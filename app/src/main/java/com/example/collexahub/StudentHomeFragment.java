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

public class StudentHomeFragment extends Fragment {

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

        // 🔥 IMPORTANT — Activity handles click
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
}
