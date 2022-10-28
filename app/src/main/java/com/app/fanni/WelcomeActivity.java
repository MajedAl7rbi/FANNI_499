package com.app.fanni;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.app.fanni.citizen.HomeCitizenActivity;
import com.app.fanni.worker.HomeWorkerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            String userType = PreferenceManager.getDefaultSharedPreferences(this).getString("userType","");
            if (userType.equals("Admin")){
                //move to admin page
                Intent intent = new Intent(WelcomeActivity.this,HomeAdminActivity.class);
                startActivity(intent);
            }else if (userType.equals("Worker")){
                //check status if is accepted

                    Intent intent = new Intent(WelcomeActivity.this, HomeWorkerActivity.class);
                    startActivity(intent);


            }else if (userType.equals("Citizen")){
                //move to citizen page
                Intent intent = new Intent(WelcomeActivity.this, HomeCitizenActivity.class);
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportActionBar().hide();
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.worker);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WelcomeActivity.this,SelectUserTypeActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}