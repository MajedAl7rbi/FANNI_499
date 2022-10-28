package com.app.fanni;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class WorkerDetailsForAdminActivity extends AppCompatActivity {

    private ImageView workerImage;
    private EditText username;
    private EditText nationalID;
    private EditText phone;
    private EditText jobType;
    private EditText email;
    private TextView textView3;
    private ImageView certification;
    private Button accept,reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details_for_admin);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         User user = (User) getIntent().getSerializableExtra("user");


        workerImage = findViewById(R.id.workerImage);
        username = findViewById(R.id.username);
        nationalID = findViewById(R.id.nationalID);
        phone = findViewById(R.id.phone);
        jobType = findViewById(R.id.jobType);
        email = findViewById(R.id.email);
        textView3 = findViewById(R.id.textView3);
        certification = findViewById(R.id.certification);
        accept = findViewById(R.id.accept);
        reject = findViewById(R.id.reject);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(WorkerDetailsForAdminActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                FirebaseDatabase.getInstance().getReference().child("users").child(user.UID).child("status").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            new AlertDialog.Builder(WorkerDetailsForAdminActivity.this).setTitle("Alert").setMessage("Account Accepted Successfully").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                        }else{
                            Toast.makeText(WorkerDetailsForAdminActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(WorkerDetailsForAdminActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                FirebaseDatabase.getInstance().getReference().child("users").child(user.UID).child("status").setValue("2").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            new AlertDialog.Builder(WorkerDetailsForAdminActivity.this).setTitle("Alert").setMessage("Account Accepted Successfully").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                        }else{
                            Toast.makeText(WorkerDetailsForAdminActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        // set Data
        username.setText(user.username);
        nationalID.setText(user.nationalID);
        phone.setText(user.phone);
        jobType.setText(user.jobType);
        email.setText(user.email);

        FirebaseStorage.getInstance().getReference().child("users").child(user.UID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(WorkerDetailsForAdminActivity.this)
                        .load(uri)
                        .into(workerImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        FirebaseStorage.getInstance().getReference().child("users").child(user.UID+"_certification").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(WorkerDetailsForAdminActivity.this)
                        .load(uri)
                        .into(certification);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}