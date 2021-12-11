package com.example.notify_around.Adapters

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.example.notify_around.R
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.notify_around.models.AdModel

class AdsAdapter(val list:List<AdModel>,val context: Context) :
   RecyclerView.Adapter<AdsAdapter.eViewHolder>() {
    private var listener: OnEventItemClickListener? = null



    override fun onBindViewHolder(holder: eViewHolder, position: Int) {
        Log.d("Adapter", "onBindViewHolder")

        val model= list[position]

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


            itemView.setOnClickListener { listener?.onAdItemClick(absoluteAdapterPosition) }

        }
    }

    interface OnEventItemClickListener {
        fun onAdItemClick(position: Int)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}