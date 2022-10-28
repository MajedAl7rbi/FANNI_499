package com.app.fanni.citizen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.app.fanni.Offer;
import com.app.fanni.R;
import com.app.fanni.Request;
import com.app.fanni.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestDetiasActivity extends AppCompatActivity {
    ArrayList<User> workers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detias);
        getSupportActionBar().setTitle("RequestDetails");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Request request = (Request) getIntent().getSerializableExtra("request");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        RequestDetailsAdapter adapter = new RequestDetailsAdapter(this,request,workers);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("offers").child(request.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workers.clear();
                for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                    User worker = dataSnapshot.getValue(User.class);
                    workers.add(worker);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}