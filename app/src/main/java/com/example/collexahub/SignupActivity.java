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

        if (TextUtils.isEmpty(fullName) || fullName.length() < 3 || !fullName.matches("^[a-zA-Z ]+$")) {
            etFullName.setError("Enter valid full name");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            return;
        }

        if (!mobile.matches("^[6-9][0-9]{9}$")) {
            etMobile.setError("Invalid Indian mobile number");
            return;
        }

        int genderId = rgGender.getCheckedRadioButtonId();
        if (genderId == -1) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbGender = findViewById(genderId);
        String gender = rbGender.getText().toString();

        if (password.length() < 8 ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*[0-9].*") ||
                !password.matches(".*[@#$%^&+=!].*")) {
            etPassword.setError("Password not strong enough");
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

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    UserModel user = new UserModel(
                            fullName,
                            email,
                            mobile,
                            gender,
                            "student"
                    );

                    FirebaseDatabase
                            .getInstance(
                                    "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
                            )
                            .getReference("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this,
                                        "Signup successful. Please login.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, Login.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "DB Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show()
                            );
                });
    }
}
