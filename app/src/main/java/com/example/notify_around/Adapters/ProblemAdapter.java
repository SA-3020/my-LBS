package com.example.notify_around.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notify_around.Models.ProblemModel;
import com.example.notify_around.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.MessageFormat;

public class ProblemAdapter extends FirestoreRecyclerAdapter<ProblemModel, ProblemAdapter.pViewHolder> {
    private OnProblemItemClickListener listener;

    public ProblemAdapter(@NonNull FirestoreRecyclerOptions<ProblemModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProblemAdapter.pViewHolder holder, int position, @NonNull ProblemModel model) {
        Log.d("Adapter", "onBindViewHolder");
        holder.tvProblemTitle.setText(model.getTitle());
        holder.tvProblemDatenTime.setText(MessageFormat.format("{0}  {1}", model.getDateAt(), model.getTimeAt()));
        holder.tvProblemLocation.setText((CharSequence) model.getLocationAt());
//        holder.tvEmergencyLevel.setText(model.getLevelOfEmergency());

    }

    @NonNull
    @Override
    public ProblemAdapter.pViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_problem, parent, false);
        return new ProblemAdapter.pViewHolder(view);
    }

    public void setOnProblemItemClickListener(OnProblemItemClickListener listener) {
        this.listener = listener;
    }


    public class pViewHolder extends RecyclerView.ViewHolder {
        TextView tvProblemTitle;
        TextView tvProblemLocation;
        TextView tvProblemDatenTime;
        TextView tvEmergencyLevel;

        public pViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProblemTitle = itemView.findViewById(R.id.tv_problemtitle);
            tvProblemLocation = itemView.findViewById(R.id.tv_problem_location);
            tvProblemDatenTime = itemView.findViewById(R.id.tv_problem_dnt);
            tvEmergencyLevel = itemView.findViewById(R.id.tv_emergencyLevel);

/*
            btnFollow.setOnClickListener(view -> {
                String action = (String) btnFollow.getText();
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEventItemClick(getSnapshots().getSnapshot(position), position, action);
                }
                if (action.equalsIgnoreCase("follow")) btnFollow.setText("unfollow");
                else if (action.equalsIgnoreCase("unfollow")) btnFollow.setText("follow");
            });*/
        }

    }

    public interface OnProblemItemClickListener {
        void onProblemItemClick(DocumentSnapshot ds);
    }

}

