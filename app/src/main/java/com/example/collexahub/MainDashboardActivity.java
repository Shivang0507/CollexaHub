package com.example.collexahub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnEventRegisterClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private ImageView headerImage;
    private TextView headerName, headerRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        // ---------------- SESSION CHECK ----------------
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedin()) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // ---------------- UI INIT ----------------
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // ---------------- NAV HEADER ----------------
        View headerView = navigationView.getHeaderView(0);
        headerImage = headerView.findViewById(R.id.imageView);
        headerName = headerView.findViewById(R.id.textViewName);
        headerRole = headerView.findViewById(R.id.textViewRole);

        headerName.setText(sessionManager.getName());
        headerRole.setText(sessionManager.getRole().toUpperCase());
        headerImage.setImageResource(R.drawable.ic_admin);

        // ---------------- LOAD MENU + HOME ----------------
        loadMenuAndHome();

        // ---------------- BACK PRESS HANDLING ----------------
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                            return;
                        }

                        if (getSupportFragmentManager()
                                .getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack();
                        } else {
                            finish();
                        }
                    }
                });
    }

    // ---------------- LOAD HOME BASED ON ROLE ----------------
    private void loadMenuAndHome() {

        SessionManager sessionManager = new SessionManager(this);
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

    // ---------------- FRAGMENT LOADER ----------------
    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // ---------------- NAVIGATION MENU ----------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        SessionManager sessionManager = new SessionManager(this);
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

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        }

        else if (id == R.id.nav_create_event) {

            if ("admin".equalsIgnoreCase(role) ||
                    "teacher".equalsIgnoreCase(role)) {

                AddEventDialogFragment
                        .newInstance(role)
                        .show(getSupportFragmentManager(), "add_event");
            }
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

    // ---------------- REGISTER BUTTON CALLBACK ----------------
    @Override
    public void onRegisterClick(String eventId) {

        EventRegistrationFragment fragment =
                EventRegistrationFragment.newInstance(eventId);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
