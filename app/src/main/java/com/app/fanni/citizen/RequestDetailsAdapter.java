package com.app.fanni.citizen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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

import com.app.fanni.Offer;
import com.app.fanni.R;
import com.app.fanni.Request;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class RequestDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    Request request;
    ArrayList<Offer> offers;

    private final static int REQUEST_VIEW = 1;
    private final static int OFFER_VIEW = 2;


    public RequestDetailsAdapter(Context context, Request request,ArrayList<Offer> offers) {
        this.context = context;
        this.request = request;
        this.offers = offers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == REQUEST_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_request_header, parent, false);
            return new RequestViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_offer, parent, false);
            return new OfferViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof RequestViewHolder){
            RequestViewHolder requestViewHolder = (RequestViewHolder) holder;
            requestViewHolder.title.setText(request.title);
            requestViewHolder.phone.setText(request.phone);
            requestViewHolder.jobType.setText(request.jobType);
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
        }else{
            OfferViewHolder offerViewHolder = (OfferViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        return offers.size() + 1;
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        ImageView requestImage;
        TextView title, phone, jobType,details;
        Button delete;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestImage = itemView.findViewById(R.id.requestImage);
            title = itemView.findViewById(R.id.title);
            phone = itemView.findViewById(R.id.phone);
            jobType = itemView.findViewById(R.id.jobType);
            details = itemView.findViewById(R.id.details);
            delete = itemView.findViewById(R.id.delete);
        }
    }


    class OfferViewHolder extends RecyclerView.ViewHolder {

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);

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
