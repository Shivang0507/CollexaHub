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

    public EventManagementFragment() {
    }

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

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_management, container, false);

        recyclerView = view.findViewById(R.id.recyclerEvents);
        fabAddEvent = view.findViewById(R.id.fabAddEvent);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList, userRole, currentUid);
        recyclerView.setAdapter(adapter);

        if ("admin".equalsIgnoreCase(userRole) || "teacher".equalsIgnoreCase(userRole)) {
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
