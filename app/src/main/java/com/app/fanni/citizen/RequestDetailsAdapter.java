package com.app.fanni.citizen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fanni.Chat;
import com.app.fanni.ChatActivity;
import com.app.fanni.HomeAdminActivity;
import com.app.fanni.LoginActivity;
import com.app.fanni.Offer;
import com.app.fanni.R;
import com.app.fanni.Request;
import com.app.fanni.User;
import com.app.fanni.WelcomeActivity;
import com.app.fanni.worker.HomeWorkerActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class RequestDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Request request;
    ArrayList<User> workers;

    private final static int REQUEST_VIEW = 1;
    private final static int OFFER_VIEW = 2;

    private String userType;
    public RequestDetailsAdapter(Context context, Request request,ArrayList<User> workers) {
        this.context = context;
        this.request = request;
        this.workers = workers;
         userType = PreferenceManager.getDefaultSharedPreferences(context).getString("userType","");

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == REQUEST_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_request_header, parent, false);
            return new RequestViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_worker_offers, parent, false);
            return new OfferViewHolder(view);
        }
    }
    RequestViewHolder requestViewHolder;
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof RequestViewHolder){
             requestViewHolder = (RequestViewHolder) holder;
            requestViewHolder.title.setText(request.title);
            requestViewHolder.phone.setText(request.phone);
            requestViewHolder.jobType.setText(request.jobType);
            requestViewHolder.details.setText(request.details);
            FirebaseStorage.getInstance().getReference().child("requests").child(request.id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Glide.with(context)
                            .load(uri)
                            .into(requestViewHolder.requestImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

           if (userType.equals("Worker")){
               requestViewHolder.delete.setVisibility(View.GONE);
               requestViewHolder.apply.setVisibility(View.VISIBLE);
            }else if (userType.equals("Citizen")){
               requestViewHolder.delete.setVisibility(View.VISIBLE);
               requestViewHolder.apply.setVisibility(View.GONE);
            }
            requestViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context).setTitle("Alert").setMessage("Do you want to delete this request?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("request").child(request.id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, "Request Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        ((AppCompatActivity)context).finish();
                                    }
                                }
                            });
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });

            requestViewHolder.apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            FirebaseDatabase.getInstance().getReference().child("offers").child(request.id).child(user.UID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    requestViewHolder.apply.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                        }
                    });

                }
            });
        }else{
            OfferViewHolder offerViewHolder = (OfferViewHolder) holder;

            // todo hide the offer button

            if(workers.get(position-1).UID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                requestViewHolder.apply.setVisibility(View.GONE);
            }

            if (userType.equals("Worker")){
                offerViewHolder.chat.setVisibility(View.GONE);
            }else if (userType.equals("Citizen")){
                offerViewHolder.chat.setVisibility(View.VISIBLE);
            }
            offerViewHolder.name.setText(workers.get(position - 1).username);
            offerViewHolder.jobType.setText(workers.get(position - 1).email);

            FirebaseStorage.getInstance().getReference().child("users").child(workers.get(position - 1).UID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Glide.with(context)
                            .load(uri)
                            .into(offerViewHolder.workerImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            offerViewHolder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    Chat chat = new Chat(id,FirebaseAuth.getInstance().getCurrentUser().getUid(),workers.get(position-1).UID,request.id);
                    FirebaseDatabase.getInstance().getReference().child("Chat").child(id).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("chat",chat);
                            context.startActivity(intent);
                        }
                    });

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return workers.size() + 1;
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        ImageView requestImage;
        TextView title, phone, jobType,details;
        Button delete,apply;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestImage = itemView.findViewById(R.id.requestImage);
            title = itemView.findViewById(R.id.title);
            phone = itemView.findViewById(R.id.phone);
            jobType = itemView.findViewById(R.id.jobType);
            details = itemView.findViewById(R.id.details);
            delete = itemView.findViewById(R.id.delete);
            apply = itemView.findViewById(R.id.apply);
        }
    }


    class OfferViewHolder extends RecyclerView.ViewHolder {

        ImageView workerImage,chat;
        TextView name,jobType;
        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            workerImage = itemView.findViewById(R.id.workerImage);
            name = itemView.findViewById(R.id.name);
            jobType = itemView.findViewById(R.id.jobType);
            chat = itemView.findViewById(R.id.chat);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return REQUEST_VIEW;
        } else {
            return OFFER_VIEW;
        }
    }
}
