package com.example.krahs.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.krahs.fragments.ToggleFragment;
import com.example.krahs.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.example.krahs.fragments.HomeFragment;
import com.example.krahs.fragments.NotificationFragment;
import com.example.krahs.fragments.ProfileFragment;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottom_navigation;
    SpaceNavigationView bottomNavigation;
    Fragment selectedfragment = null;

    @SuppressLint({"WrongViewCast", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);*/

        bottomNavigation = findViewById(R.id.bottomnavigation);
        bottomNavigation.initWithSaveInstanceState(savedInstanceState);
        bottomNavigation.changeCenterButtonIcon(R.drawable.white_ripple);
        bottomNavigation.addSpaceItem(new SpaceItem(getResources().getString(R.id.nav_home),R.drawable.ic_home));
        bottomNavigation.addSpaceItem(new SpaceItem(getResources().getString(R.id.nav_heart),R.drawable.ic_notify));
        bottomNavigation.addSpaceItem(new SpaceItem(getResources().getString(R.id.nav_profile),R.drawable.ic_profile));
        bottomNavigation.addSpaceItem(new SpaceItem(getResources().getString(R.id.nav_toggle),R.drawable.ic_nav));
        bottomNavigation.setCentreButtonColor(getResources().getColor(R.color.green_natural));
        bottomNavigation.showIconOnly();
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

        bottomNavigation.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                bottomNavigation(itemIndex);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                bottomNavigation(itemIndex);
            }
        });

    }

    private void bottomNavigation(int index){
        switch (index){
            case 0:
                selectedfragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(),getResources().getString(R.string.home)).commitAllowingStateLoss();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NotificationFragment(),getResources().getString(R.string.notification)).commitAllowingStateLoss();
                break;
            case 2:
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                editor.apply();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment(),getResources().getString(R.string.profile)).commitAllowingStateLoss();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ToggleFragment(),getResources().getString(R.string.setting)).commitAllowingStateLoss();
                break;
        }
    }

    /*private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedfragment = new HomeFragment();
                            item.setIcon(R.color.selector_home);
                            break;
                        *//*case R.id.nav_search:
                            selectedfragment = new SearchFragment();
                            break;*//*
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
            };*/


}
