    package com.example.collexahub;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

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

    public class StudentManagementFragment extends Fragment {

        private RecyclerView rvStudents;
        private StudentAdapter adapter;
        private List<StudentModel> studentList;
        private DatabaseReference usersRef;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_student_management, container, false);

            requireActivity().getOnBackPressedDispatcher().addCallback(
                    getViewLifecycleOwner(),
                    new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {
                            getParentFragmentManager().popBackStack();
                        }
                    });

            rvStudents = view.findViewById(R.id.rvStudents);
            rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));

            studentList = new ArrayList<>();
            adapter = new StudentAdapter(studentList);
            rvStudents.setAdapter(adapter);

            FirebaseDatabase database = FirebaseDatabase.getInstance(
                    "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
            );

            usersRef = database.getReference("users");

            loadStudents();

            return view;
        }

        private void loadStudents() {
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    studentList.clear();

                    for (DataSnapshot userSnap : snapshot.getChildren()) {

                        String role = userSnap.child("role").getValue(String.class);

                        if ("student".equalsIgnoreCase(role)) {
                            StudentModel student = userSnap.getValue(StudentModel.class);

                            if (student != null) {
                                student.setUid(userSnap.getKey());
                                studentList.add(student);
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
