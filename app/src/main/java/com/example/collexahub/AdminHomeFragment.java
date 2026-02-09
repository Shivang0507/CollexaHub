package com.example.collexahub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomeFragment extends Fragment {

    private TextView tvTotalStudents, tvTotalTeachers;
    private Button btnmanageStudent, btnmanageTeachers,btnmanageAdmin,btnmanageEvents;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        tvTotalStudents = view.findViewById(R.id.tvTotalStudents);
        tvTotalTeachers = view.findViewById(R.id.tvTotalTeachers);
        btnmanageStudent = view.findViewById(R.id.btnmanageStudent);
        btnmanageTeachers = view.findViewById(R.id.btnmanageTeachers);
        btnmanageAdmin = view.findViewById(R.id.btnmanageAdmin);
        btnmanageEvents = view.findViewById(R.id.btnmanageEvents);


        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
        );

        usersRef = database.getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int students = 0;
                int teachers = 0;

                for (DataSnapshot user : snapshot.getChildren()) {
                    String role = user.child("role").getValue(String.class);

                    if ("student".equalsIgnoreCase(role)) students++;
                    if ("teacher".equalsIgnoreCase(role)) teachers++;
                }

                tvTotalStudents.setText(String.valueOf(students));
                tvTotalTeachers.setText(String.valueOf(teachers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvTotalStudents.setText("0");
                tvTotalTeachers.setText("0");
            }
        });

        btnmanageStudent.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new StudentManagementFragment())
                    .addToBackStack(null)
                    .commit();
        });
        btnmanageTeachers.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TeacherManagementFragment())
                    .addToBackStack(null)
                    .commit();
        });
        btnmanageAdmin.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AdminManagementFragment())
                    .addToBackStack(null)
                    .commit();
        });
        btnmanageEvents.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            EventManagementFragment.newInstance("admin")
                    )
                    .addToBackStack(null)
                    .commit();
        });



        return view;
    }
}
