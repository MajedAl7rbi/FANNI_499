package com.app.fanni.citizen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fanni.LoginActivity;
import com.app.fanni.R;
import com.app.fanni.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class AddRequestActivity extends AppCompatActivity {

    private ImageView requestImage;
    private EditText title;
    private EditText details;
    private EditText phone;
    private Spinner jobType;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);
        getSupportActionBar().setTitle("Add Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        requestImage = findViewById(R.id.requestImage);
        title = findViewById(R.id.title);
        details = findViewById(R.id.details);
        phone = findViewById(R.id.phone);
        jobType = findViewById(R.id.jobType);
        add = findViewById(R.id.add);
        requestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,12);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri == null){
                    Toast.makeText(AddRequestActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(title.getText())){
                    title.setError("Enter Title");
                }else if(TextUtils.isEmpty(details.getText())){
                    title.setError("Enter Details");
                }else if(jobType.getSelectedItem().toString().equals("Select worker Type")){
                    Toast.makeText(AddRequestActivity.this, "Select Job Type", Toast.LENGTH_SHORT).show();
                }else{
                    ProgressDialog progressDialog = new ProgressDialog(AddRequestActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    String id = FirebaseDatabase.getInstance().getReference().child("request").push().getKey();
                    Request request = new Request(id,title.getText().toString(),details.getText().toString(),phone.getText().toString(),jobType.getSelectedItem().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());

                    // Get the data from an ImageView as bytes
                    requestImage.setDrawingCacheEnabled(true);
                    requestImage.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) requestImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("requests").child(id);
                    UploadTask uploadTask = storageReference.putBytes(data);
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
                            FirebaseDatabase.getInstance().getReference().child("request").child(id).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        Toast.makeText(AddRequestActivity.this, "Request Added Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else{
                                        Toast.makeText(AddRequestActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });



                }
            }
        });
    }
    Uri uri;
    //URL
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12 && resultCode == RESULT_OK){
             uri = data.getData();
            requestImage.setImageURI(uri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}