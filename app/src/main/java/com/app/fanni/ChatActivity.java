package com.app.fanni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ArrayList<Message> messages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Chat chat = (Chat) getIntent().getSerializableExtra("chat");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageButton send = findViewById(R.id.send);
        EditText message = findViewById(R.id.message);
        MessageAdapter adapter = new MessageAdapter(this,messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Chat").child(chat.id).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                    Message message1 = dataSnapshot.getValue(Message.class);
                    messages.add(message1);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(message.getText())) {
                    String id = FirebaseDatabase.getInstance().getReference().child("Chat").child(chat.id).child("messages").push().getKey();
                    Message message1 = new Message(id, FirebaseAuth.getInstance().getCurrentUser().getUid(), message.getText().toString());
                    FirebaseDatabase.getInstance().getReference().child("Chat").child(chat.id).child("messages").child(id).setValue(message1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                message.setText("");
                            }
                        }
                    });
                }
            }
        });
    }
}