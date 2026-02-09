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

public class AdminManagementFragment extends Fragment {

    private RecyclerView rvAdmins;
    private AdminAdapter adapter;
    private List<AdminModel> adminList;
    private DatabaseReference usersRef;
    private Button btnAddAdmin;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_admin_management,
                container,
                false
        );

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getParentFragmentManager().popBackStack();
                    }
                });

        rvAdmins = view.findViewById(R.id.rvAdmins);
        rvAdmins.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddAdmin = view.findViewById(R.id.btnAddAdmin); // ✅ added

        adminList = new ArrayList<>();
        adapter = new AdminAdapter(adminList);
        rvAdmins.setAdapter(adapter);

        usersRef = FirebaseDatabase
                .getInstance("https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");

        loadAdmins();

        btnAddAdmin.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AddAdminFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadAdmins() {

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                adminList.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {

                    String role = userSnap.child("role").getValue(String.class);

                    if ("admin".equalsIgnoreCase(role)) {

                        AdminModel admin = userSnap.getValue(AdminModel.class);

                        if (admin != null) {
                            admin.setUid(userSnap.getKey());
                            adminList.add(admin);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
