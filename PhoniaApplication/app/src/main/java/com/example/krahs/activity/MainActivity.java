package com.example.krahs.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.krahs.fragments.ToggleFragment;
import com.example.krahs.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.example.krahs.fragments.HomeFragment;
import com.example.krahs.fragments.NotificationFragment;
import com.example.krahs.fragments.ProfileFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottom_navigation;
    Fragment selectedfragment = null;

    @SuppressLint({"WrongViewCast", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedfragment = new HomeFragment();
                            item.setIcon(R.color.selector_home);
                            break;
                        /*case R.id.nav_search:
                            selectedfragment = new SearchFragment();
                            break;*/
                        case R.id.nav_toggle:
                            item.setIcon(R.color.selector_toggle);
                            selectedfragment = new ToggleFragment();
                            break;
                        case R.id.nav_heart:
                            selectedfragment = new NotificationFragment();
                            item.setIcon(R.color.selector_notify);
                            break;
                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                            editor.apply();
                            selectedfragment = new ProfileFragment();
                            item.setIcon(R.color.selector_profile);
                            break;
                    }
                    if (selectedfragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedfragment).commit();
                    }
                    return true;
                }
            };


}
