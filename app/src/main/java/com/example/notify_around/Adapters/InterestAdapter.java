package com.example.notify_around.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notify_around.Models.InterestsModel;
import com.example.notify_around.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InterestAdapter extends FirestoreRecyclerAdapter<InterestsModel, InterestAdapter.iViewHolder> {
    private OnInterestItemClickListener listener;

    public InterestAdapter(@NonNull FirestoreRecyclerOptions<InterestsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull iViewHolder holder, int position, @NonNull InterestsModel model) {
        Log.d("Adapter", "onBindViewHolder");
        Glide.with(holder.img.getContext()).load(model.getIcon()).into(holder.img);
        holder.tvInterTitle.setText(model.getTitle());
    }

    @NonNull
    @Override
    public iViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_interest, parent, false);
        return new iViewHolder(view);
    }

    public void setOnInterestItemClickListener(OnInterestItemClickListener listener) {
        this.listener = listener;
    }

    public class iViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvInterTitle;
        Button btnFollow;

        public iViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            tvInterTitle = itemView.findViewById(R.id.tv_inter_title);
            btnFollow = itemView.findViewById(R.id.btn_follow);

            btnFollow.setOnClickListener(view -> {
                String action = (String) btnFollow.getText();
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onInterestItemClick(getSnapshots().getSnapshot(position), position, action);
                }
                if (action.equalsIgnoreCase("follow")) btnFollow.setText("unfollow");
                else if (action.equalsIgnoreCase("unfollow")) btnFollow.setText("follow");
            });
        }

    }

    public interface OnInterestItemClickListener {
        void onInterestItemClick(DocumentSnapshot ds, int position, String action);
    }

}
