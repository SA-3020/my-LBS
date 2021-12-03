package com.example.notify_around.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notify_around.Models.EventModel;
import com.example.notify_around.Models.InterestsModel;
import com.example.notify_around.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.MessageFormat;

public class EventAdapter extends FirestoreRecyclerAdapter<EventModel, EventAdapter.eViewHolder> {
    private EventAdapter.OnEventItemClickListener listener;

    public EventAdapter(@NonNull FirestoreRecyclerOptions<EventModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventAdapter.eViewHolder holder, int position, @NonNull EventModel model) {
        Log.d("Adapter", "onBindViewHolder");
        holder.tvEventTitle.setText(model.getTitle());
        holder.tvEventDatenTime.setText(MessageFormat.format("{0}  {1}", model.getDateAt(), model.getTimeAt()));
        holder.tvEventLocation.setText((CharSequence) model.getLocationAt());

    }

    @NonNull
    @Override
    public eViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_event, parent, false);
        return new EventAdapter.eViewHolder(view);
    }

    public void setOnEventItemClickListener(EventAdapter.OnEventItemClickListener listener) {
        this.listener = listener;
    }


    public class eViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle;
        TextView tvEventLocation;
        TextView tvEventDatenTime;

        public eViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEventTitle = itemView.findViewById(R.id.tv_eventtitle);
            tvEventLocation = itemView.findViewById(R.id.tv_event_location);
            tvEventDatenTime = itemView.findViewById(R.id.tv_event_dnt);

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

    public interface OnEventItemClickListener {
        void onEventItemClick(DocumentSnapshot ds);
    }

}
