package com.quantum.attendanceapp;

import static com.quantum.attendanceapp.SplashScreen.userData;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.quantum.attendanceapp.admin.AdminHomeFragment;
import com.quantum.attendanceapp.admin.SettingsActivity;
import com.quantum.attendanceapp.employee.EmpHomeFragment;

public class HomePage extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        findViews();
        userData = getIntent().getParcelableExtra("UserData");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawerLayout.closeDrawers();
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;
            if (id == R.id.nav_log_out) {
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(HomePage.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_profile) {
                intent = new Intent(HomePage.this, ProfileActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_settings) {
                if (userData.getUserType().equals("Admin")) {
                    intent = new Intent(HomePage.this, SettingsActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "You don't have access to settings", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String userType = userData.getUserType();
        if (userType.equals("Admin")) {
            AdminHomeFragment admHomeFrag = new AdminHomeFragment();
            transaction.add(R.id.container, admHomeFrag, "admHomeFrag");
            transaction.show(admHomeFrag);
            transaction.commit();
        } else if (userType.equals("Employee")) {
            EmpHomeFragment empHomeFrag = new EmpHomeFragment();
            transaction.add(R.id.container, empHomeFrag, "empHomeFrag");
            transaction.show(empHomeFrag);
            transaction.commit();
        }
    }

    private void findViews() {
        drawerLayout = findViewById(R.id.drawer);

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        toolbar = findViewById(R.id.toolbar);

    }
}