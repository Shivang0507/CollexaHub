package com.example.collexahub;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<EventModel> list;
    private final String userRole;
    private final String currentUid;

    public EventAdapter(List<EventModel> list, String userRole, String currentUid) {
        this.list = list;
        this.userRole = userRole;
        this.currentUid = currentUid;
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
        holder.tvDate.setText(event.date + " • " + event.time);
        holder.tvVenue.setText(event.venue);

        boolean canManage =
                "admin".equalsIgnoreCase(userRole)
                        || event.createdByUid.equals(currentUid);

        holder.layoutAdminActions.setVisibility(
                canManage ? View.VISIBLE : View.GONE
        );

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Delete", (d, w) ->
                            FirebaseDatabase.getInstance(
                                            "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
                                    )
                                    .getReference("events")
                                    .child(event.eventId)
                                    .removeValue()
                    )
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.btnEdit.setOnClickListener(v -> {
            AddEventDialogFragment
                    .newInstance(userRole, event)
                    .show(
                            ((FragmentActivity) v.getContext())
                                    .getSupportFragmentManager(),
                            "EditEvent"
                    );
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvVenue;
        ImageButton btnEdit, btnDelete;
        LinearLayout layoutAdminActions;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvVenue = itemView.findViewById(R.id.tvEventVenue);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            layoutAdminActions = itemView.findViewById(R.id.layoutAdminActions);
        }
    }
}
