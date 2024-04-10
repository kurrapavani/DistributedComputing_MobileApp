package com.example.translator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    static int selector;
    int[][] states = new int[][]{
            new int[]{-android.R.attr.state_enabled}, // disabled
            new int[]{android.R.attr.state_checked}, // enabled
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_pressed},// pressed
    };

    int[] colors1 = new int[]{
            Color.WHITE,
            Color.parseColor("#BB86FC"),
            Color.WHITE,
            Color.WHITE
    };

    BottomNavigationView bottomNav;
    ColorStateList ColorStateList1 = new ColorStateList(states, colors1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomBar);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setItemTextColor(ColorStateList1);
        bottomNav.setItemIconTintList(ColorStateList1);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new fragmentVoice()).commit();
//            bottomNav.setItemIconTintList(ColorStateList1);
//            bottomNav.setItemTextColor(ColorStateList1);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.Image:
                            if (selector == R.id.Image)
                                return true;
//                            bottomNav.setItemIconTintList(ColorStateList1);
//                            bottomNav.setItemTextColor(ColorStateList1);
                            selector = R.id.Image;
                            selectedFragment = new fragmentImage();
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}