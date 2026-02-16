package com.example.collexahub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

        View headerView = navigationView.getHeaderView(0);
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


    private void setupHeader() {

        String fullName = sessionManager.getName();
        String role = sessionManager.getRole();

        headerName.setText(fullName);
        headerRole.setText(role.toUpperCase());

        switch (role) {
            case "admin":
                headerImage.setImageResource(R.drawable.ic_admin);
                break;
            case "teacher":
                headerImage.setImageResource(R.drawable.ic_admin);
                break;
            case "volunteer":
                headerImage.setImageResource(R.drawable.ic_admin);
                break;
            default:
                headerImage.setImageResource(R.drawable.ic_admin);
                break;
        }
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
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        String role = sessionManager.getRole();

        if (id == R.id.nav_home) {

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
                        event.entryFee
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
    public void onPaymentSuccess(String paymentId) {

        if (currentRegistrationFragment != null) {
            currentRegistrationFragment.handlePaymentSuccess(paymentId);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
}
