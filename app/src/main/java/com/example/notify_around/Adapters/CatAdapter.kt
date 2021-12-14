package com.example.notify_around.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notify_around.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CatAdapter(private val dataholder: MutableList<String>) :
    RecyclerView.Adapter<CatAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.single_row_categories, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        //viewHolder.img.setImageResource(dataholder[position].img)
        viewHolder.catName.setText(dataholder[position])
    }

    override fun getItemCount(): Int {
        return dataholder.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var img: ImageView
        var catName: TextView

        init {
            img = itemView.findViewById(R.id.btn_del)
            catName = itemView.findViewById(R.id.tv_interest_title)
            img.setOnClickListener {
                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .update(
                        "interests",
                        FieldValue.arrayRemove(catName.text)
                    )
            }
        }

        override fun onClick(p0: View?) {

        }
    }

}