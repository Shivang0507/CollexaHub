package com.example.collexahub;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    private static final String DEFAULT_ADMIN_UID =
            "RaXDwxvPTrXHIXvyAW0d2cIalOD2";

    private List<AdminModel> adminList;

    public AdminAdapter(List<AdminModel> adminList) {
        this.adminList = adminList;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {

        AdminModel admin = adminList.get(position);

        holder.tvName.setText(admin.getFullName());
        holder.tvEmail.setText(admin.getEmail());
        holder.tvMobile.setText(admin.getMobile());

        if (DEFAULT_ADMIN_UID.equals(admin.getUid())) {
            holder.btnDeleteAdmin.setVisibility(View.INVISIBLE);
            return;
        }

        holder.btnDeleteAdmin.setOnClickListener(v -> {

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Admin")
                    .setMessage("Are you sure you want to delete this admin?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        FirebaseDatabase
                                .getInstance("https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("users")
                                .child(admin.getUid())
                                .removeValue()
                                .addOnSuccessListener(unused ->
                                        Toast.makeText(
                                                v.getContext(),
                                                "Admin deleted",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                );

                        int pos = holder.getAdapterPosition();
                        adminList.remove(pos);
                        notifyItemRemoved(pos);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail, tvMobile;
        ImageButton btnDeleteAdmin;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            btnDeleteAdmin = itemView.findViewById(R.id.btnDeleteAdmin);
        }
    }
}
