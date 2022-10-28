package com.app.fanni;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ChatViewHolder> {


    Context context;
    ArrayList<Message> messages;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        if(messages.get(position).senderID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.citizen.setVisibility(View.VISIBLE);
            holder.worker.setVisibility(View.GONE);
            holder.citizen.setText(messages.get(position).message);
        }else{
            holder.citizen.setVisibility(View.GONE);
            holder.worker.setVisibility(View.VISIBLE);
            holder.worker.setText(messages.get(position).message);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView citizen,worker;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            citizen = itemView.findViewById(R.id.citizen);
            worker = itemView.findViewById(R.id.worker);
        }
    }
}
