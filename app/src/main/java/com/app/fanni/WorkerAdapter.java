package com.app.fanni;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.CustomViewHolder> {

    Context context;
    ArrayList<User> workers;


    public WorkerAdapter(Context context, ArrayList<User> workers) {
        this.context = context;
        this.workers = workers;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_worker,parent,false);
        return new CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.name.setText(workers.get(position).username);
        holder.jobType.setText(workers.get(position).email);
        if(workers.get(position).status.equals("1")){
            holder.status.setText("Accepted");
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        }else if(workers.get(position).status.equals("0")){
            holder.status.setText("Under Review");
            holder.status.setTextColor(context.getResources().getColor(R.color.under_review));
        }else if(workers.get(position).status.equals("2")){
            holder.status.setText("Rejected");
            holder.status.setTextColor(context.getResources().getColor(R.color.red));
        }
        FirebaseStorage.getInstance().getReference().child("users").child(workers.get(position).UID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(context)
                        .load(uri)
                        .into(holder.workerImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        ImageView workerImage;
        TextView name, jobType,status;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            workerImage = itemView.findViewById(R.id.workerImage);
            name = itemView.findViewById(R.id.name);
            jobType = itemView.findViewById(R.id.jobType);
            status = itemView.findViewById(R.id.status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,WorkerDetailsForAdminActivity.class);
                    intent.putExtra("user",workers.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });


        }
    }


}
