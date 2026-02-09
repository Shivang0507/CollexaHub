package com.example.collexahub;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AddAdminFragment extends Fragment {

    EditText etFullName, etEmail, etMobile, etPassword;
    RadioGroup rgGender;
    Button btnSignup;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_admin, container, false);

        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etMobile = view.findViewById(R.id.etMobile);
        etPassword = view.findViewById(R.id.etPassword);
        rgGender = view.findViewById(R.id.rgGender);
        btnSignup = view.findViewById(R.id.btnSignup);

        mAuth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(v -> addAdmin());

        return view;
    }

    private void addAdmin() {

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Required");
            return;
        }

        if (fullName.length() < 3) {
            etFullName.setError("Minimum 3 characters");
            return;
        }

        if (!fullName.matches("^[a-zA-Z ]+$")) {
            etFullName.setError("Only letters allowed");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError("Required");
            return;
        }

        if (mobile.length() != 10) {
            etMobile.setError("Mobile number must be 10 digits");
            return;
        }

        if (!mobile.matches("^[6-9][0-9]{9}$")) {
            etMobile.setError("Invalid Indian number");
            return;
        }

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(getContext(), "Select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbGender = requireView().findViewById(selectedGenderId);
        String gender = rbGender.getText().toString();

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required");
            return;
        }

        if (password.length() < 8) {
            etPassword.setError("Min 8 characters");
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            etPassword.setError("1 uppercase required");
            return;
        }

        if (!password.matches(".*[a-z].*")) {
            etPassword.setError("1 lowercase required");
            return;
        }

        if (!password.matches(".*[0-9].*")) {
            etPassword.setError("1 digit required");
            return;
        }

        if (!password.matches(".*[@#$%^&+=!].*")) {
            etPassword.setError("1 special character required");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(
                                getContext(),
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    String uid = mAuth.getCurrentUser().getUid();

                    UserModel admin = new UserModel(
                            fullName,
                            email,
                            mobile,
                            gender,
                            "admin"
                    );

                    FirebaseDatabase
                            .getInstance("https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("users")
                            .child(uid)
                            .setValue(admin)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        getContext(),
                                        "Admin added successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.fragment_container,
                                                new AdminManagementFragment()
                                        )
                                        .commit();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            getContext(),
                                            e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
                });
    }
}
