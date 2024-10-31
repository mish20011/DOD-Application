package com.catignascabela.dodapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.catignascabela.dodapplication.databinding.ActivityHomepageBinding;

public class HomepageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityHomepageBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in and load the appropriate fragment
        if (savedInstanceState == null) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            boolean isTeacher = getIntent().getBooleanExtra("isTeacher", false);

            // Load the appropriate fragment based on user type
            if (currentUser != null) {
                loadInitialFragment(isTeacher);
            } else {
                // If not logged in, navigate to LoginActivity
                startLoginActivity();
            }
        }
    }

    private void loadInitialFragment(boolean isTeacher) {
        Fragment initialFragment;
        if (isTeacher) {
            initialFragment = new TeacherHomeFragment();
        } else {
            initialFragment = new StudentHomeFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, initialFragment)
                .commit();
        binding.navigationView.setCheckedItem(R.id.nav_home); // Highlight the home item
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFragment = null;

        if (id == R.id.nav_home) {
            // Check if the user is logged in
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            boolean isTeacher = getIntent().getBooleanExtra("isTeacher", false);

            // Load the appropriate fragment based on user type
            if (currentUser != null) {
                loadInitialFragment(isTeacher);
            } else {
                startLoginActivity();
            }
        } else if (id == R.id.nav_violations) {
            selectedFragment = new ViolationsFragment();
        } else if (id == R.id.nav_schedule) {
            selectedFragment = new ScheduleFragment();
        } else if (id == R.id.nav_logout) {
            logout(); // Call the logout method
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        // Log out from Firebase
        firebaseAuth.signOut();

        // Clear shared preferences to log out the user
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate back to the login activity
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
