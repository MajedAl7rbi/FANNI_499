package com.app.fanni;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

public class RegisterAsCustomerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ImageView citizenImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_customer);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        EditText email = findViewById(R.id.email);
         citizenImage = findViewById(R.id.citizenImage);
        EditText username = findViewById(R.id.username);
        EditText nationalID = findViewById(R.id.nationalID);
        EditText phone = findViewById(R.id.phone);
        EditText password = findViewById(R.id.password);
        Button register = findViewById(R.id.register);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("users");


        citizenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,12);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(username.getText())){
                    username.setError("Enter Username");
                }else if(TextUtils.isEmpty(nationalID.getText())){
                    nationalID.setError("Enter nationalID");
                }else if(TextUtils.isEmpty(phone.getText())){
                    phone.setError("Enter phone");
                }else if(TextUtils.isEmpty(email.getText())) {
                    email.setError("Enter email");
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                    email.setError("Enter Valid email");
                }else if(TextUtils.isEmpty(password.getText())){
                    password.setError("Enter password");
                }else if(!isValidPassword(password.getText().toString())){
                    password.setError("Password Not Strong");
                }else {
                    ProgressDialog progressDialog = new ProgressDialog(RegisterAsCustomerActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(RegisterAsCustomerActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        citizenImage.setDrawingCacheEnabled(true);
                                        citizenImage.buildDrawingCache();
                                        Bitmap bitmap = ((BitmapDrawable) citizenImage.getDrawable()).getBitmap();
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

                                                User user = new User(mAuth.getCurrentUser().getUid(),username.getText().toString(),email.getText().toString(),phone.getText().toString(),nationalID.getText().toString(),"Citizen");
                                                mDatabase.child("users").child(user.UID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.dismiss();
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(RegisterAsCustomerActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();

                                                        }else{
                                                            Toast.makeText(RegisterAsCustomerActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                            }
                                        });


                                        // Sign in success, update UI with the signed-in user's information
                                    } else {
                                        progressDialog.dismiss();
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterAsCustomerActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public static boolean isValidPassword(String s) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");

        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches();
    }

    //URL
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            citizenImage.setImageURI(uri);
        }
    }
}