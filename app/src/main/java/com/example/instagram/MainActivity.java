package com.example.instagram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagram.Fragment.HomeFragment;
import com.example.instagram.Fragment.NotificationFragment;
import com.example.instagram.Fragment.ProfileFragment;
import com.example.instagram.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment;

    public static String close_meesage = "close";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    bottomNavigationView = findViewById(R.id.bottom_navigation);
    bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

    if (close_meesage.equals("profile"))
    {
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        close_meesage = "close";
    }
    else
    {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
    }

    private  BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


            close_meesage = "close";
            switch (menuItem.getItemId())
            {
                case R.id.nav_home:
                    selectedFragment =  new HomeFragment();
                    break;

                case R.id.nav_search:
                    selectedFragment = new SearchFragment();
                    break;

                case R.id.nav_add:
                    selectedFragment = null;
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    finish();
                    break;

                case R.id.nav_favorite:
                        selectedFragment = new NotificationFragment();
                    break;

                case R.id.nav_profile:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectedFragment = new ProfileFragment();
                    break;


            }
                if(selectedFragment != null)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }



            return true;
        }
    };

    @Override
    public void onBackPressed() {

        if(close_meesage.equals("notification"))
        {
            bottomNavigationView.setSelectedItemId(R.id.nav_favorite);
            close_meesage = "close";
            return;
        }

        if (close_meesage.equals("profile"))
        {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            close_meesage = "close";
        return;
        }

        if (close_meesage.equals("profile_b"))
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            close_meesage = "search";
        return;
        }
if (close_meesage.equals("profile_c"))
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            close_meesage = "notification";
        return;
        }


        if (close_meesage.equals("search"))
        {
            bottomNavigationView.setSelectedItemId(R.id.nav_search);
            close_meesage = "close";
        return;
        }



        super.onBackPressed();
    }
}
