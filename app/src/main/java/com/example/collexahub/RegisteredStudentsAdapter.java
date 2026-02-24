package com.example.collexahub;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegisteredStudentsAdapter
        extends RecyclerView.Adapter<RegisteredStudentsAdapter.ViewHolder> {

    private final List<RegisteredStudentModel> list;
    private final OnQRClickListener listener;

    public interface OnQRClickListener {
        void onQRClick(String qrCode);
    }

    public RegisteredStudentsAdapter(List<RegisteredStudentModel> list,
                                     OnQRClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registered_student, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RegisteredStudentModel model = list.get(position);

        holder.tvName.setText(model.name);
        holder.tvEnrollment.setText("Enrollment No: " + model.enrollmentNo);
        holder.tvSemester.setText("Semester: " + model.semester);
        holder.tvPhone.setText("Contact: " + model.phone);

        holder.tvPaymentStatus.setText("Payment: " + model.paymentStatus);

        if ("Paid".equalsIgnoreCase(model.paymentStatus)) {
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#2E7D32"));
        }

        holder.btnViewQR.setOnClickListener(v -> {
            if (listener != null && model.qrCode != null) {
                listener.onQRClick(model.qrCode);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEnrollment, tvSemester, tvPhone, tvPaymentStatus;
        ImageButton btnViewQR;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvEnrollment = itemView.findViewById(R.id.tvEnrollment);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            btnViewQR = itemView.findViewById(R.id.btnViewQR);
        }
    }
}
