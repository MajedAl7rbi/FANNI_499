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

import java.util.ArrayList;

public class RequestDetiasActivity extends AppCompatActivity {
    ArrayList<Offer> offers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detias);
        getSupportActionBar().setTitle("RequestDetails");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Request request = (Request) getIntent().getSerializableExtra("request");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        RequestDetailsAdapter adapter = new RequestDetailsAdapter(this,request,offers);
        recyclerView.setAdapter(adapter);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}