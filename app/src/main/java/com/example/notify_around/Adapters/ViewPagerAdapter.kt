package com.example.notify_around.adapters

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notify_around.R


class ViewPagerAdapter(val mContext: Context, val list: List<String>) : RecyclerView.Adapter<ViewPagerAdapter.SliderViewHolder?>() {


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): SliderViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_images, parent, false)
        return SliderViewHolder(itemView)
    }


    inner class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image:ImageView = view.findViewById(R.id.image)

    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {

        Glide.with(mContext).load(list[position]).override(1600)
                .into(holder.image)

    }

    override fun getItemCount(): Int {
       return list.size
    }
}