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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VolunteerHomeFragment extends Fragment {

    private RecyclerView rvEvents;
    private EventAdapter adapter;
    private List<EventModel> eventList;
    private DatabaseReference eventRef;
    private String currentUid;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_volunteer_home,
                container,
                false
        );

        rvEvents = view.findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEvents.setHasFixedSize(false);

        eventList = new ArrayList<>();
        currentUid = FirebaseAuth.getInstance().getUid();

        // ✅ UPDATED ADAPTER (4 arguments)
        adapter = new EventAdapter(
                eventList,
                "volunteer",
                currentUid,
                null   // volunteers don't register
        );
        rvEvents.setAdapter(adapter);

        eventRef = FirebaseDatabase.getInstance(
                "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("events");

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

                        // Latest first
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
}
