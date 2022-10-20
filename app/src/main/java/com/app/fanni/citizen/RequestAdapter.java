package com.app.fanni.citizen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fanni.R;
import com.app.fanni.Request;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHoder> {


    Context context;
    ArrayList<Request> requests;

    public RequestAdapter(Context context, ArrayList<Request> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request,parent,false);
        return new RequestViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHoder holder, int position) {
        holder.title.setText(requests.get(position).title);
        holder.phone.setText(requests.get(position).phone);
        holder.jobType.setText(requests.get(position).jobType);
        FirebaseStorage.getInstance().getReference().child("requests").child(requests.get(position).id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(context)
                        .load(uri)
                        .into(holder.requestImage);
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
        return requests.size();
    }

    class RequestViewHoder extends RecyclerView.ViewHolder{
        ImageView requestImage;
        TextView title,phone,jobType;
        public RequestViewHoder(@NonNull View itemView) {
            super(itemView);
            requestImage = itemView.findViewById(R.id.requestImage);
            title = itemView.findViewById(R.id.title);
            phone = itemView.findViewById(R.id.phone);
            jobType = itemView.findViewById(R.id.jobType);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),RequestDetiasActivity.class);
                    intent.putExtra("request",requests.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
