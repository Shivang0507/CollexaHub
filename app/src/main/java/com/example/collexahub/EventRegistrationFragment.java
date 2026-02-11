package com.example.collexahub;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class EventRegistrationFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";

    private String eventId;
    private String currentUid;

    private EditText etName, etEmail, etPhone,
            etEnrollment, etDepartment, etSem;
    private Button btnSubmit;

    public static EventRegistrationFragment newInstance(String eventId) {
        EventRegistrationFragment fragment = new EventRegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_event_registration,
                container,
                false
        );

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }

        currentUid = FirebaseAuth.getInstance().getUid();

        // Bind views
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etEnrollment = view.findViewById(R.id.etEnrollment);
        etDepartment = view.findViewById(R.id.etDepartment);
        etSem = view.findViewById(R.id.etSem);
        btnSubmit = view.findViewById(R.id.btnSubmitRegistration);

        btnSubmit.setOnClickListener(v -> submitForm());

        return view;
    }

    private void submitForm() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String enrollment = etEnrollment.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String sem = etSem.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(name)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(enrollment)
                || TextUtils.isEmpty(sem)) {

            Toast.makeText(
                    getContext(),
                    "Please fill all required fields",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        RegistrationFormModel model = new RegistrationFormModel(
                name,
                email,
                phone,
                enrollment,
                department,
                sem,
                System.currentTimeMillis()
        );

        FirebaseDatabase.getInstance(
                        "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
                )
                .getReference("events")
                .child(eventId)
                .child("registrations")
                .child(currentUid)
                .setValue(model)
                .addOnSuccessListener(a -> {
                    Toast.makeText(
                            getContext(),
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Go back to previous fragment
                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Registration failed",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}
