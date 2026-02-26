package com.example.collexahub;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // ================= OLD CODE (UNCHANGED) =================

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

        if (model.semester != null &&
                !model.semester.equals("-") &&
                model.semester.contains("•")) {

            holder.tvTeamMembers.setVisibility(View.VISIBLE);
            holder.tvTeamMembers.setText("Members:\n" + model.semester);

            holder.tvEnrollment.setVisibility(View.GONE);
            holder.tvSemester.setVisibility(View.GONE);

        } else {

            holder.tvTeamMembers.setVisibility(View.GONE);
            holder.tvEnrollment.setVisibility(View.VISIBLE);
            holder.tvSemester.setVisibility(View.VISIBLE);
        }

        holder.btnViewQR.setOnClickListener(v -> {
            if (listener != null && model.qrCode != null) {
                listener.onQRClick(model.qrCode);
            }
        });

        // ==========================================================
        // ================= NEW TEAM SECTION (ADDED ONLY) ==========
        // ==========================================================

        if ("Team".equalsIgnoreCase(model.eventType)) {

            holder.layoutTeamSection.setVisibility(View.VISIBLE);

            // 🔴 Hide old individual views
            holder.tvName.setVisibility(View.GONE);
            holder.tvEnrollment.setVisibility(View.GONE);
            holder.tvSemester.setVisibility(View.GONE);
            holder.tvPhone.setVisibility(View.GONE);
            holder.tvTeamMembers.setVisibility(View.GONE);

            // 🔴 Hide old payment
            holder.tvPaymentStatus.setVisibility(View.GONE);

            // 🟢 Show Team Payment
            holder.tvTeamPaymentStatus.setVisibility(View.VISIBLE);
            holder.tvTeamPaymentStatus.setText("Payment: " + model.paymentStatus);

            holder.tvTeamPaymentStatus.setTextColor(Color.parseColor("#2E7D32"));

            holder.tvTeamName.setText("Team: " + model.teamName);
            String leaderText = "⭐ Leader\n" +
                    "Name: " + model.name + "\n" +
                    "Enrollment: " + model.enrollmentNo + "\n" +
                    "Semester: " + model.leaderSemester + "\n" +
                    "Contact: " + model.phone;

            SpannableString leaderSpannable = new SpannableString(leaderText);

            int leaderEnd = leaderText.indexOf("\n");

            leaderSpannable.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0,
                    leaderEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            leaderSpannable.setSpan(
                    new RelativeSizeSpan(1.1f),
                    0,
                    leaderEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            holder.tvLeaderName.setText(leaderSpannable);

// Hide unused fields
            holder.tvLeaderEnrollment.setVisibility(View.GONE);
            holder.tvLeaderSemester.setVisibility(View.GONE);
            holder.tvLeaderPhone.setVisibility(View.GONE);

            holder.layoutTeamDetails.setVisibility(View.GONE);
            holder.btnSeeMore.setText("See More Details");

            holder.btnSeeMore.setOnClickListener(v -> {

                if (holder.layoutTeamDetails.getVisibility() == View.GONE) {

                    holder.layoutTeamDetails.setVisibility(View.VISIBLE);
                    holder.btnSeeMore.setText("Hide Details");

                    // -------- CO-LEADER --------
                    String coText = "⭐ Co-Leader\n" + model.coLeaderDetails;

                    SpannableString coSpannable = new SpannableString(coText);

// Make entire first line (emoji + Co-Leader) bold & bigger
                    int endIndex = coText.indexOf("\n");

                    coSpannable.setSpan(
                            new StyleSpan(Typeface.BOLD),
                            0,
                            endIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );

                    coSpannable.setSpan(
                            new RelativeSizeSpan(1.0f),
                            0,
                            endIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );

                    holder.tvCoLeaderDetails.setText(coSpannable);

                    // -------- MEMBERS --------
                    String fullText = model.membersDetails;

                    SpannableString spannable = new SpannableString(fullText);


                    Pattern pattern = Pattern.compile("Member \\d+");
                    Matcher matcher = pattern.matcher(fullText);

                    while (matcher.find()) {
                        spannable.setSpan(
                                new StyleSpan(Typeface.BOLD),
                                matcher.start(),
                                matcher.end(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }

                    holder.tvMembersDetails.setText(spannable);


                } else {

                    holder.layoutTeamDetails.setVisibility(View.GONE);
                    holder.btnSeeMore.setText("See More Details");
                }

            });

        } else {

            holder.layoutTeamSection.setVisibility(View.GONE);

            // 🟢 Show old views again
            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvEnrollment.setVisibility(View.VISIBLE);
            holder.tvSemester.setVisibility(View.VISIBLE);
            holder.tvPhone.setVisibility(View.VISIBLE);

            // 🟢 Show old payment
            holder.tvPaymentStatus.setVisibility(View.VISIBLE);

            // 🔴 Hide team payment
            holder.tvTeamPaymentStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        // OLD VIEWS
        TextView tvName, tvEnrollment, tvSemester,
                tvPhone, tvPaymentStatus, tvTeamMembers;

        ImageButton btnViewQR;

        // NEW TEAM VIEWS
        LinearLayout layoutTeamSection, layoutTeamDetails;
        TextView tvTeamName, tvLeaderName, tvLeaderEnrollment,
                tvLeaderSemester, tvLeaderPhone,
                tvCoLeaderDetails, tvMembersDetails;

        // NEW TEAM PAYMENT
        TextView tvTeamPaymentStatus;

        Button btnSeeMore;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            // OLD
            tvName = itemView.findViewById(R.id.tvName);
            tvEnrollment = itemView.findViewById(R.id.tvEnrollment);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            tvTeamMembers = itemView.findViewById(R.id.tvTeamMembers);
            btnViewQR = itemView.findViewById(R.id.btnViewQR);

            // NEW
            layoutTeamSection = itemView.findViewById(R.id.layoutTeamSection);
            layoutTeamDetails = itemView.findViewById(R.id.layoutTeamDetails);
            tvTeamName = itemView.findViewById(R.id.tvTeamName);
            tvLeaderName = itemView.findViewById(R.id.tvLeaderName);
            tvLeaderEnrollment = itemView.findViewById(R.id.tvLeaderEnrollment);
            tvLeaderSemester = itemView.findViewById(R.id.tvLeaderSemester);
            tvLeaderPhone = itemView.findViewById(R.id.tvLeaderPhone);
            tvCoLeaderDetails = itemView.findViewById(R.id.tvCoLeaderDetails);
            tvMembersDetails = itemView.findViewById(R.id.tvMembersDetails);

            tvTeamPaymentStatus = itemView.findViewById(R.id.tvTeamPaymentStatus);

            btnSeeMore = itemView.findViewById(R.id.btnSeeMore);
        }
    }
}