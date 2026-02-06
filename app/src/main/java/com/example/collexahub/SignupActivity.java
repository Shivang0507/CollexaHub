package com.example.collexahub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etMobile, etPassword;
    RadioGroup rgGender;
    Button btnSignup;
    TextView etLogin;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        rgGender = findViewById(R.id.rgGender);
        btnSignup = findViewById(R.id.btnSignup);
        etLogin = findViewById(R.id.etLogin);

        mAuth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(v -> signupUser());

        etLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, Login.class));
            finish();
        });
    }

    private void signupUser() {

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
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbGender = findViewById(selectedGenderId);
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
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = mAuth.getCurrentUser().getUid();

                    UserModel user = new UserModel(
                            fullName,
                            email,
                            mobile,
                            gender,
                            "student"
                    );

                    FirebaseDatabase
                            .getInstance("https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(unused -> {
                                  Toast.makeText(this,
                                        "Signup successful. Please login.",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(SignupActivity.this, Login.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                });
    }
}
