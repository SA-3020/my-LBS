package com.example.notify_around.adapters

import android.content.Context
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.example.notify_around.R
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.notify_around.models.AdModel
import com.google.firebase.firestore.DocumentSnapshot

class AdsAdapter(options: FirestoreRecyclerOptions<AdModel?>,val context: Context) :
    FirestoreRecyclerAdapter<AdModel, AdsAdapter.eViewHolder>(options) {
    private var listener: OnEventItemClickListener? = null



    override fun onBindViewHolder(holder: eViewHolder, position: Int, model: AdModel) {
        Log.d("Adapter", "onBindViewHolder")
        holder.tvAdTitle.text = model.title
        holder.tvAdLocation.text = model.address
        holder.tvAdcontact.text = model.contact

        if(model.images.isNotEmpty()) {
            Glide.with(context).load(model.images[0]).into(holder.adImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): eViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.bussiness_ad_design, parent, false)
        return eViewHolder(view)
    }

    fun setOnEventItemClickListener(listener: OnEventItemClickListener?) {
        this.listener = listener
    }

    inner class eViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAdTitle: TextView
        var tvAdLocation: TextView
        var tvAdcontact: TextView
        var adImageView: ImageView


        init {
            tvAdTitle = itemView.findViewById(R.id.tv_ad_title)
            tvAdLocation = itemView.findViewById(R.id.tv_ad_location)
            tvAdcontact = itemView.findViewById(R.id.tv_contact)
            adImageView = itemView.findViewById(R.id.img_view)


            itemView.setOnClickListener { listener?.onAdItemClick(snapshots.getSnapshot(bindingAdapterPosition)) }

        }
    }

    interface OnEventItemClickListener {
        fun onAdItemClick(ds: DocumentSnapshot?)
    }


}