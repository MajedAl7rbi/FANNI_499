package com.app.fanni;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

public class RegisterAsWorkerActivity extends AppCompatActivity {

    private ImageView workerImage;
    private EditText username;
    private EditText nationalID;
    private EditText phone;
    private Spinner jobType;
    private EditText email;
    private EditText password;
    private TextView textView3;
    private ImageView certification;
    private Button register;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_worker);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        workerImage = findViewById(R.id.workerImage);
        username = findViewById(R.id.username);
        nationalID = findViewById(R.id.nationalID);
        phone = findViewById(R.id.phone);
        jobType = findViewById(R.id.jobType);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        textView3 = findViewById(R.id.textView3);
        certification = findViewById(R.id.certification);
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(username.getText())){
                    username.setError("Enter Username");
                }else if(TextUtils.isEmpty(nationalID.getText())){
                    nationalID.setError("Enter nationalID");
                }else if(TextUtils.isEmpty(phone.getText())){
                    phone.setError("Enter phone");
                }else if(jobType.getSelectedItem().toString().equals("Select Your Job Type")) {
                    Toast.makeText(RegisterAsWorkerActivity.this, "Select Your Job Type", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(email.getText())) {
                    email.setError("Enter email");
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                    email.setError("Enter Valid email");
                }else if(TextUtils.isEmpty(password.getText())){
                    password.setError("Enter password");
                }else if(!isValidPassword(password.getText().toString())){
                    password.setError("Password Not Strong");
                }else {
                    ProgressDialog progressDialog = new ProgressDialog(RegisterAsWorkerActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(RegisterAsWorkerActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        workerImage.setDrawingCacheEnabled(true);
                                        workerImage.buildDrawingCache();
                                        Bitmap bitmap = ((BitmapDrawable) workerImage.getDrawable()).getBitmap();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] data = baos.toByteArray();

                                        UploadTask uploadTask = storageRef.child(mAuth.getUid()).putBytes(data);
                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle unsuccessful uploads
                                                progressDialog.dismiss();
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                // ...

                                                certification.setDrawingCacheEnabled(true);
                                                certification.buildDrawingCache();
                                                Bitmap bitmap = ((BitmapDrawable) certification.getDrawable()).getBitmap();
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                byte[] data = baos.toByteArray();

                                                UploadTask uploadTask = storageRef.child(mAuth.getUid()+"_certification").putBytes(data);
                                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Handle unsuccessful uploads
                                                        progressDialog.dismiss();
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                        // ...

                                                        User user = new User(mAuth.getCurrentUser().getUid(),username.getText().toString(),email.getText().toString(),phone.getText().toString(),nationalID.getText().toString(),"Worker","0",jobType.getSelectedItem().toString());
                                                        mDatabase.child("users").child(user.UID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                progressDialog.dismiss();
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(RegisterAsWorkerActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();

                                                                }else{
                                                                    Toast.makeText(RegisterAsWorkerActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });

                                            }
                                        });





                                        // Sign in success, update UI with the signed-in user's information
                                    } else {
                                        progressDialog.dismiss();
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterAsWorkerActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        workerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,12);
            }
        });
        certification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,123);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            workerImage.setImageURI(uri);
        }
        if(requestCode == 123 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            certification.setImageURI(uri);
        }
    }

    public static boolean isValidPassword(String s) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");

        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}