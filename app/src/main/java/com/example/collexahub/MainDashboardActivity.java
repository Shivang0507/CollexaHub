package com.example.collexahub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

public class MainDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnEventRegisterClickListener,
        PaymentResultListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private ImageView headerImage;
    private TextView headerName, headerRole;

    private SessionManager sessionManager;
    private EventRegistrationFragment currentRegistrationFragment;

    private static final String DB_URL =
            "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        Checkout.preload(getApplicationContext());

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedin()) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // ✅ ADDED: Delete expired events whenever dashboard opens
        deleteExpiredEvents();

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        var headerView = navigationView.getHeaderView(0);
        headerImage = headerView.findViewById(R.id.imageView);
        headerName = headerView.findViewById(R.id.textViewName);
        headerRole = headerView.findViewById(R.id.textViewRole);

        setupHeader();
        loadMenuAndHome();

        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack();
                        } else {
                            finish();
                        }
                    }
                });
    }

    // ✅ ADDED METHOD (Nothing else changed)
    private void deleteExpiredEvents() {

        FirebaseDatabase.getInstance(DB_URL)
                .getReference("events")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        long currentTime = System.currentTimeMillis();

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            EventModel event = ds.getValue(EventModel.class);

                            if (event != null &&
                                    event.endTimestamp > 0 &&
                                    currentTime >= event.endTimestamp) {

                                ds.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void setupHeader() {

        String fullName = sessionManager.getName();
        String role = sessionManager.getRole();

        headerName.setText(fullName);
        headerRole.setText(role.toUpperCase());

        headerImage.setImageResource(R.drawable.ic_admin);
    }

    private void loadMenuAndHome() {

        String role = sessionManager.getRole();
        navigationView.getMenu().clear();

        switch (role) {
            case "admin":
                navigationView.inflateMenu(R.menu.menu_admin);
                loadFragment(new AdminHomeFragment());
                break;

            case "teacher":
                navigationView.inflateMenu(R.menu.menu_teacher);
                loadFragment(new TeacherHomeFragment());
                break;

            case "volunteer":
                navigationView.inflateMenu(R.menu.menu_volunteer);
                loadFragment(new VolunteerHomeFragment());
                break;

            default:
                navigationView.inflateMenu(R.menu.menu_student);
                loadFragment(new StudentHomeFragment());
                break;
        }
    }

    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        String role = sessionManager.getRole();

        if (id == R.id.nav_home) {

            getSupportFragmentManager().popBackStack(null,
                    getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);

            switch (role) {
                case "admin":
                    loadFragment(new AdminHomeFragment());
                    break;
                case "teacher":
                    loadFragment(new TeacherHomeFragment());
                    break;
                case "volunteer":
                    loadFragment(new VolunteerHomeFragment());
                    break;
                default:
                    loadFragment(new StudentHomeFragment());
                    break;
            }
        }

        else if (id == R.id.nav_profile) {
            loadFragment(new ProfileFragment());
        }

        else if (id == R.id.nav_create_event) {

            if ("admin".equalsIgnoreCase(role) ||
                    "teacher".equalsIgnoreCase(role)) {

                AddEventDialogFragment
                        .newInstance(role)
                        .show(getSupportFragmentManager(), "add_event");
            }
        }
        else if (id == R.id.nav_scan_qr) {

            loadFragment(new QRScannerFragment());
        }
        else if (id == R.id.nav_logout) {
            sessionManager.logout();
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRegisterClick(EventModel event) {

        currentRegistrationFragment =
                EventRegistrationFragment.newInstance(
                        event.eventId,
                        event.title,
                        event.paid,
                        event.entryFee,
                        event.teamEvent,
                        event.maxTeamSize
                );

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, currentRegistrationFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMyQRClick(String qrCode) {

        QRDisplayFragment fragment =
                QRDisplayFragment.newInstance(qrCode);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onEditClick(EventModel event) {

        AddEventDialogFragment
                .newInstance(sessionManager.getRole(), event)
                .show(getSupportFragmentManager(), "edit_event");
    }

    @Override
    public void onDeleteClick(EventModel event) {

        FirebaseDatabase.getInstance(DB_URL)
                .getReference("events")
                .child(event.eventId)
                .removeValue();

        Toast.makeText(this, "Event Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccess(String paymentId) {

        if (currentRegistrationFragment != null) {
            currentRegistrationFragment.handlePaymentSuccess(paymentId);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onViewRegistrationsClick(EventModel event) {

        RegisteredStudentsFragment fragment =
                RegisteredStudentsFragment.newInstance(event.eventId);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}

