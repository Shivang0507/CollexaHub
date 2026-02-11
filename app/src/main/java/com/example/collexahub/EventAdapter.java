package com.example.collexahub;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<EventModel> list;
    private final String userRole;
    private final String currentUid;
    private final OnEventRegisterClickListener registerListener;

    private static final String DB_URL =
            "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app";

    public EventAdapter(
            List<EventModel> list,
            String userRole,
            String currentUid,
            OnEventRegisterClickListener registerListener
    ) {
        this.list = list;
        this.userRole = userRole;
        this.currentUid = currentUid;
        this.registerListener = registerListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        EventModel event = list.get(position);

        holder.tvTitle.setText(event.title);
        holder.tvDate.setText(event.date + " | " + event.time);
        holder.tvVenue.setText(event.venue);

        // -------- ADMIN / TEACHER --------
        boolean canManage =
                "admin".equalsIgnoreCase(userRole) ||
                        "teacher".equalsIgnoreCase(userRole);

        holder.layoutAdminActions.setVisibility(
                canManage ? View.VISIBLE : View.GONE
        );

        holder.btnEdit.setOnClickListener(v ->
                AddEventDialogFragment
                        .newInstance(userRole, event)
                        .show(
                                ((MainDashboardActivity) v.getContext())
                                        .getSupportFragmentManager(),
                                "edit_event"
                        )
        );

        holder.btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Event")
                        .setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton("Delete", (d, w) ->
                                FirebaseDatabase.getInstance(DB_URL)
                                        .getReference("events")
                                        .child(event.eventId)
                                        .removeValue()
                        )
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        // -------- STUDENT REGISTER --------
        if ("student".equalsIgnoreCase(userRole)) {
            holder.btnRegister.setVisibility(View.VISIBLE);
            checkAlreadyRegistered(holder, event);
        } else {
            holder.btnRegister.setVisibility(View.GONE);
        }

        holder.btnRegister.setOnClickListener(v -> {
            if (registerListener != null) {
                registerListener.onRegisterClick(event.eventId);
            }
        });
    }

    private void checkAlreadyRegistered(EventViewHolder holder, EventModel event) {

        if (currentUid == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL)
                .getReference("events")
                .child(event.eventId)
                .child("registrations")
                .child(currentUid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.btnRegister.setText("Registered");
                    holder.btnRegister.setEnabled(false);
                } else {
                    holder.btnRegister.setText("Register");
                    holder.btnRegister.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // -------- VIEW HOLDER --------
    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvVenue;
        LinearLayout layoutAdminActions;
        ImageButton btnEdit, btnDelete;
        Button btnRegister;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvVenue = itemView.findViewById(R.id.tvEventVenue);
            layoutAdminActions = itemView.findViewById(R.id.layoutAdminActions);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnRegister = itemView.findViewById(R.id.btnRegister);
        }
    }
}
