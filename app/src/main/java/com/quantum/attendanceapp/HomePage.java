package com.quantum.attendanceapp;

import static com.quantum.attendanceapp.SplashScreen.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.quantum.attendanceapp.admin.AdminHomeFragment;
import com.quantum.attendanceapp.employee.EmpHomeFragment;

public class HomePage extends AppCompatActivity {

    private TextView navUserName;
    private ImageView navUserPic;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        findViews();
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
            }
            return true;
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        userType = userData.getUserType();
        if(userType.equals("Admin")){
            AdminHomeFragment admHomeFrag = new AdminHomeFragment();
            transaction.add(R.id.container, admHomeFrag, "admHomeFrag");
            transaction.show(admHomeFrag);
            transaction.commit();
        }else if(userType.equals("Employee")){
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
        navUserName = headerView.findViewById(R.id.nav_user_name);
        navUserPic = headerView.findViewById(R.id.nav_user_pic);
        toolbar = findViewById(R.id.toolbar);

    }
}