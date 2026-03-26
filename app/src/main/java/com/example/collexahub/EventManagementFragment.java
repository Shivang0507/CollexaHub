package com.example.collexahub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventManagementFragment extends Fragment {

    private static final String ARG_ROLE = "user_role";

    private String userRole;
    private String currentUid;

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddEvent;
    private EventAdapter adapter;
    private List<EventModel> eventList;

    private DatabaseReference eventRef;

    public EventManagementFragment() {}

    public static EventManagementFragment newInstance(String role) {
        EventManagementFragment fragment = new EventManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userRole = getArguments().getString(ARG_ROLE);
        }

        currentUid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
        );
        eventRef = database.getReference("events");
    }

    @Override
    public void onResume() {
        super.onResume();
        checkExpiredEvents();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_event_management,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.recyclerEvents);
        fabAddEvent = view.findViewById(R.id.fabAddEvent);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);

        eventList = new ArrayList<>();

        adapter = new EventAdapter(
                eventList,
                userRole,
                currentUid,
                new OnEventRegisterClickListener() {

                    @Override
                    public void onRegisterClick(EventModel event) {
                    }

                    @Override
                    public void onMyQRClick(String qrCode) {
                    }

                    @Override
                    public void onEditClick(EventModel event) {

                        AddEventDialogFragment
                                .newInstance(userRole, event)
                                .show(getChildFragmentManager(), "edit_event");
                    }

                    @Override
                    public void onDeleteClick(EventModel event) {

                        eventRef.child(event.eventId).removeValue();

                        Toast.makeText(
                                getContext(),
                                "Event Deleted",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onViewRegistrationsClick(EventModel event) {

                        RegisteredStudentsFragment fragment =
                                RegisteredStudentsFragment.newInstance(event.eventId);

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
        );


        recyclerView.setAdapter(adapter);

        if ("admin".equalsIgnoreCase(userRole)
                || "teacher".equalsIgnoreCase(userRole)) {
            fabAddEvent.setVisibility(View.VISIBLE);
        } else {
            fabAddEvent.setVisibility(View.GONE);
        }

        fabAddEvent.setOnClickListener(v ->
                AddEventDialogFragment
                        .newInstance(userRole)
                        .show(getChildFragmentManager(), "AddEventDialog")
        );

        loadEvents();
        return view;
    }

    private void loadEvents() {
        eventRef.orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        eventList.clear();
                        long currentTime = System.currentTimeMillis();

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            EventModel event = ds.getValue(EventModel.class);

                            if (event != null) {

                                if (event.endTimestamp > 0 &&
                                        currentTime >= event.endTimestamp) {

                                    eventRef.child(event.eventId).removeValue();

                                } else {

                                    eventList.add(event);
                                }
                            }
                        }

                        Collections.reverse(eventList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(
                                getContext(),
                                "Failed to load events",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void checkExpiredEvents() {

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long currentTime = System.currentTimeMillis();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    EventModel event = ds.getValue(EventModel.class);

                    if (event != null &&
                            event.endTimestamp > 0 &&
                            currentTime >= event.endTimestamp) {

                        eventRef.child(event.eventId).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}