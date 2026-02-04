package com.example.collexahub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView etSignup;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        etSignup = findViewById(R.id.etSignup);

        mAuth = FirebaseAuth.getInstance();

        etSignup.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, SignupActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = mAuth.getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance(
                            "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app/"
                    ).getReference("users").child(uid);

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (!snapshot.exists()) {
                                Toast.makeText(Login.this,
                                        "User data not found",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            String fullName = snapshot.child("fullName").getValue(String.class);
                            String role = snapshot.child("role").getValue(String.class);

                            if (fullName == null || fullName.isEmpty()) {
                                fullName = "User";
                            }

                            if (role == null || role.isEmpty()) {
                                Toast.makeText(Login.this,
                                        "User role missing. Contact admin.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            SessionManager sessionManager =
                                    new SessionManager(Login.this);
                            sessionManager.createSession(fullName, role);

                            Toast.makeText(Login.this,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent =
                                    new Intent(Login.this, SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Login.this,
                                    error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                });
    }
}
