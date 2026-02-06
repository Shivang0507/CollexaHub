package com.example.collexahub;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<TeacherModel> teacherList;

    public TeacherAdapter(List<TeacherModel> teacherList) {
        this.teacherList = teacherList;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {

        TeacherModel teacher = teacherList.get(position);

        holder.tvName.setText(teacher.getFullName());
        holder.tvEmail.setText(teacher.getEmail());
        holder.tvMobile.setText(teacher.getMobile());

        holder.btnDeleteTeacher.setOnClickListener(v -> {

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Teacher")
                    .setMessage("Are you sure you want to delete this teacher?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        FirebaseDatabase
                                .getInstance("https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("users")
                                .child(teacher.getUid())
                                .removeValue();

                        int pos = holder.getAdapterPosition();
                        teacherList.remove(pos);
                        notifyItemRemoved(pos);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail, tvMobile;
        ImageButton btnDeleteTeacher;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            btnDeleteTeacher = itemView.findViewById(R.id.btnDeleteTeacher);
        }
    }
}
