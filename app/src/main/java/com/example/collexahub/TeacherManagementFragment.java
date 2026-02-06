package com.example.collexahub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherManagementFragment extends Fragment {

    private RecyclerView rvTeachers;
    private TeacherAdapter adapter;
    private List<TeacherModel> teacherList;
    private DatabaseReference usersRef;
    private Button btnAddTeachcer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_teacher_management, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getParentFragmentManager().popBackStack();
                    }
                });

        rvTeachers = view.findViewById(R.id.rvTeachers);
        btnAddTeachcer = view.findViewById(R.id.btnAddTeachcer);

        rvTeachers.setLayoutManager(new LinearLayoutManager(getContext()));

        teacherList = new ArrayList<>();
        adapter = new TeacherAdapter(teacherList);
        rvTeachers.setAdapter(adapter);

        usersRef = FirebaseDatabase
                .getInstance("https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");

        loadTeachers();

        btnAddTeachcer.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AddTeacherFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadTeachers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                teacherList.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {

                    String role = userSnap.child("role").getValue(String.class);

                    if ("teacher".equalsIgnoreCase(role)) {

                        TeacherModel teacher = userSnap.getValue(TeacherModel.class);

                        if (teacher != null) {
                            teacher.setUid(userSnap.getKey());
                            teacherList.add(teacher);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
