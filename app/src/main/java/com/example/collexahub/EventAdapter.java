package com.example.collexahub;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import com.example.collexahub.OnEventRegisterClickListener;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<EventModel> list;
    private final String userRole;
    private final String currentUid;
    private final OnEventRegisterClickListener listener;

    private static final String DB_URL =
            "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app";

    public EventAdapter(
            List<EventModel> list,
            String userRole,
            String currentUid,
            OnEventRegisterClickListener listener
    ) {
        this.list = list;
        this.userRole = userRole;
        this.currentUid = currentUid;
        this.listener = listener;
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

        if (event.paid) {
            holder.tvEntryFee.setText("Entry Fee: ₹" + event.entryFee);
            holder.tvEntryFee.setTextColor(Color.RED);
        } else {
            holder.tvEntryFee.setText("Free Event");
            holder.tvEntryFee.setTextColor(Color.parseColor("#2E7D32"));
        }

        holder.btnRegister.setVisibility(View.GONE);
        holder.btnMyQR.setVisibility(View.GONE);
        holder.layoutAdminActions.setVisibility(View.GONE);

        // ✅ ADDED (reset visibility)
        holder.btnViewRegistrations.setVisibility(View.GONE);

        // ✅ UPDATED (added volunteer)
        if ("admin".equalsIgnoreCase(userRole) ||
                "teacher".equalsIgnoreCase(userRole) ||
                "volunteer".equalsIgnoreCase(userRole)) {

            holder.layoutAdminActions.setVisibility(View.VISIBLE);

            // ✅ ADDED (show button)
            holder.btnViewRegistrations.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(event);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(event);
                }
            });

            // ✅ ADDED (click logic)
            holder.btnViewRegistrations.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewRegistrationsClick(event);
                }
            });

            return;
        }

        if ("student".equalsIgnoreCase(userRole)) {

            if (event.startTimestamp > 0 &&
                    System.currentTimeMillis() >= event.startTimestamp) {

                holder.btnRegister.setVisibility(View.GONE);
                return;
            }

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

                        holder.btnMyQR.setVisibility(View.VISIBLE);

                        String qrCode =
                                snapshot.child("qrCode").getValue(String.class);

                        holder.btnMyQR.setOnClickListener(v -> {
                            if (listener != null && qrCode != null) {
                                listener.onMyQRClick(qrCode);
                            }
                        });

                    } else {

                        holder.btnRegister.setVisibility(View.VISIBLE);

                        holder.btnRegister.setOnClickListener(v -> {
                            if (listener != null) {
                                listener.onRegisterClick(event);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvVenue, tvEntryFee;
        Button btnRegister, btnMyQR;

        // ✅ ADDED
        Button btnViewRegistrations;

        LinearLayout layoutAdminActions;

        ImageButton btnEdit, btnDelete;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvVenue = itemView.findViewById(R.id.tvEventVenue);
            tvEntryFee = itemView.findViewById(R.id.tvEntryFee);

            btnRegister = itemView.findViewById(R.id.btnRegister);
            btnMyQR = itemView.findViewById(R.id.btnMyQR);

            // ✅ ADDED
            btnViewRegistrations = itemView.findViewById(R.id.btnViewRegistrations);

            layoutAdminActions = itemView.findViewById(R.id.layoutAdminActions);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // ✅ ADDED METHOD (only addition)
//    public interface OnEventRegisterClickListener {
//        void onRegisterClick(EventModel event);
//        void onMyQRClick(String qrCode);
//        void onEditClick(EventModel event);
//        void onDeleteClick(EventModel event);
//        void onViewRegistrationsClick(EventModel event); // NEW
//    }
}
