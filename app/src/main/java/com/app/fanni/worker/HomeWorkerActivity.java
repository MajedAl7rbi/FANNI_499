package com.app.fanni.worker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.app.fanni.R;
import com.app.fanni.citizen.ChatFragment;
import com.app.fanni.citizen.PostFragment;
import com.app.fanni.citizen.ProfileCitizenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeWorkerActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_worker);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.post:
                        setTitle("Requests");
                        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new PostFragment()).commit();
                        return true;
                    case R.id.chat:
                        setTitle("Chat");
                        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new ChatFragment()).commit();
                        return true;
                    case R.id.profile:
                        setTitle("Profile");
                        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new ProfileCitizenFragment()).commit();
                        return true;

                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.post);

    }
}