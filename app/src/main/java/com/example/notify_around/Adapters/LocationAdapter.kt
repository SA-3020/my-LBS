package com.example.notify_around.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.notify_around.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.example.notify_around.models.Locations

class LocationAdapter(var mContext: Context, var data_list: List<Locations>) :
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var location_textView: TextView
        var sub_location_textView: TextView
        var item: Locations? = null
        override fun onClick(v: View) {
            clickListener!!.onItemClick(adapterPosition, v, 1)
        }

        init {
            location_textView = v.findViewById(R.id.location_textView)
            sub_location_textView = v.findViewById(R.id.sub_location_textView)
            itemView.setOnClickListener(this)
        }
    }

    fun setOnItemClickListener(clickListener: ClickListener?) {
        Companion.clickListener = clickListener
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View?, id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_locations_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.location_textView.text = data_list[position].location_name
        holder.sub_location_textView.text = data_list[position].location_sub_name
        holder.item = data_list[position]
    }

    override fun getItemCount(): Int {
        return data_list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    companion object {
        private var clickListener: ClickListener? = null
    }
}