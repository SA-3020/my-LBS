package com.example.notify_around.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notify_around.Adapters.MyEventAdapter.eViewHolder
import com.example.notify_around.R
import com.example.notify_around.models.EventModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import java.text.MessageFormat

class MyEventAdapter(options: FirestoreRecyclerOptions<EventModel?>, val context:Context) :
    FirestoreRecyclerAdapter<EventModel, eViewHolder>(options) {

    private var listener: OnEventItemClickListener? = null
    override fun onBindViewHolder(holder: eViewHolder, position: Int, model: EventModel) {
        Log.d("Adapter", "onBindViewHolder")
        holder.tvEventTitle.text = model.title
        holder.tvEventDatenTime.text = MessageFormat.format("{0}  {1}", model.dateAt, model.timeAt)
        holder.tvEventLocation.text = model.locationAt as CharSequence?

        if(model.images.isNotEmpty()) {
            Glide.with(context).load(model.images[0]).into(holder.adImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): eViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_row_event, parent, false)
        return eViewHolder(view)
    }

    fun setOnEventItemClickListener(listener: OnEventItemClickListener?) {
        this.listener = listener
    }

    inner class eViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvEventTitle: TextView
        var tvEventLocation: TextView
        var tvEventDatenTime: TextView
        var adImageView: ImageView

        init {
            tvEventTitle = itemView.findViewById(R.id.tv_eventtitle)
            tvEventLocation = itemView.findViewById(R.id.tv_event_location)
            tvEventDatenTime = itemView.findViewById(R.id.tv_event_dnt)
            adImageView = itemView.findViewById(R.id.img_view)


            itemView.setOnClickListener { listener?.onEventItemClick(snapshots.getSnapshot(bindingAdapterPosition)) }


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

    interface OnEventItemClickListener {
        fun onEventItemClick(ds: DocumentSnapshot?)
    }
}