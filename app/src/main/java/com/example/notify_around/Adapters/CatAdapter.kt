package com.example.notify_around.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notify_around.Models.CategoriesModel
import com.example.notify_around.R

class CatAdapter(private val dataholder: ArrayList<CategoriesModel>) :
    RecyclerView.Adapter<CatAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.single_row_categories, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        viewHolder.img.setImageResource(dataholder[position].img)
        viewHolder.catName.setText(dataholder[position].catName)
    }

    override fun getItemCount(): Int {
        return dataholder.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var img: ImageView = itemView.findViewById(R.id.cat_img)
        var catName: TextView = itemView.findViewById(R.id.tv_cat_name)


        override fun onClick(p0: View?) {

        }
    }

}