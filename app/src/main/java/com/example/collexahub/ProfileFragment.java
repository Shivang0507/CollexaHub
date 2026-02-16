package com.example.collexahub;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    EditText etFullName, etEmail, etPhone, etOldPassword, etPassword;
    Button btnUpdate;
    ImageView imgLogo;
    TextView tvTitle;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference userRef;
    SessionManager sessionManager;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[@#$%^&+=!])" +
                    "(?=\\S+$)" +
                    ".{8,}" +
                    "$");

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etPassword = view.findViewById(R.id.etPassword);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        imgLogo = view.findViewById(R.id.imgLogo);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        sessionManager = new SessionManager(requireContext());

        if (user == null) return view;

        String uid = user.getUid();
        userRef = FirebaseDatabase.getInstance(
                "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("users").child(uid);

        etFullName.setText(sessionManager.getName());
        etEmail.setText(user.getEmail());

        applyRoleRules(sessionManager.getRole());

        btnUpdate.setOnClickListener(v -> validateAndUpdate());

        return view;
    }

    private void applyRoleRules(String role) {

        if ("admin".equals(role)) {
        }

        if ("volunteer".equals(role)) {
        }
    }

    private void validateAndUpdate() {

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            return;
        }

        if (!TextUtils.isEmpty(phone) && !phone.matches("\\d{10}")) {
            etPhone.setError("Phone must be 10 digits");
            return;
        }

        boolean wantsSensitiveUpdate =
                !email.equals(user.getEmail()) ||
                        !TextUtils.isEmpty(newPassword);

        if (wantsSensitiveUpdate && TextUtils.isEmpty(oldPassword)) {
            etOldPassword.setError("Old password required");
            return;
        }

        if (!TextUtils.isEmpty(newPassword) &&
                !PASSWORD_PATTERN.matcher(newPassword).matches()) {

            etPassword.setError(
                    "Password must contain:\n" +
                            "• 8+ characters\n" +
                            "• Upper & lower case\n" +
                            "• Number\n" +
                            "• Special character"
            );
            return;
        }

        userRef.child("fullName").setValue(fullName);
        userRef.child("mobile").setValue(phone);
        sessionManager.createSession(fullName, sessionManager.getRole());

        if (wantsSensitiveUpdate) {

            AuthCredential credential =
                    EmailAuthProvider.getCredential(
                            user.getEmail(),
                            oldPassword
                    );

            user.reauthenticate(credential)
                    .addOnSuccessListener(unused ->
                            updateSensitiveFields(email, newPassword))
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(),
                                    "Old password incorrect",
                                    Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(getContext(),
                    "Profile updated successfully",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSensitiveFields(String email, String newPassword) {

        // EMAIL
        if (!email.equals(user.getEmail())) {
            user.updateEmail(email)
                    .addOnSuccessListener(unused ->
                            userRef.child("email").setValue(email))
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(),
                                    "Email update failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
        }

        if (!TextUtils.isEmpty(newPassword)) {
            user.updatePassword(newPassword)
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(),
                                    "Password update failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
        }

        etOldPassword.setText("");
        etPassword.setText("");

        Toast.makeText(getContext(),
                "Profile updated successfully",
                Toast.LENGTH_SHORT).show();
    }
}
