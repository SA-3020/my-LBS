package com.example.notify_around.adapters

import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.example.notify_around.models.InterestsModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.example.notify_around.adapters.InterestAdapter.iViewHolder
import com.bumptech.glide.Glide
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.example.notify_around.R
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.google.firebase.firestore.DocumentSnapshot

class InterestAdapter(options: FirestoreRecyclerOptions<InterestsModel?>) :
    FirestoreRecyclerAdapter<InterestsModel, iViewHolder>(options) {
    private var listener: OnInterestItemClickListener? = null
    override fun onBindViewHolder(holder: iViewHolder, position: Int, model: InterestsModel) {
        Log.d("Adapter", "onBindViewHolder")
        Glide.with(holder.img.context).load(model.icon).into(holder.img)
        holder.tvInterTitle.text = model.Title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): iViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_row_interest, parent, false)
        return iViewHolder(view)
    }

    fun setOnInterestItemClickListener(listener: OnInterestItemClickListener?) {
        this.listener = listener
    }

    inner class iViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView
        var tvInterTitle: TextView
        var btnFollow: Button

        init {
            img = itemView.findViewById(R.id.img)
            tvInterTitle = itemView.findViewById(R.id.tv_inter_title)
            btnFollow = itemView.findViewById(R.id.btn_follow)
            btnFollow.setOnClickListener { view: View? ->
                val action = btnFollow.text as String
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    //action is string that is either follow or unfollow
                    listener!!.onInterestItemClick(
                        snapshots.getSnapshot(position),
                        position,
                        action
                    )
                }
                if (action.equals("follow", ignoreCase = true)) btnFollow.text =
                    "unfollow" else if (action.equals(
                        "unfollow",
                        ignoreCase = true
                    )
                ) btnFollow.text = "follow"
            }
        }
    }

    interface OnInterestItemClickListener {
        fun onInterestItemClick(ds: DocumentSnapshot?, position: Int, action: String?)
    }
}