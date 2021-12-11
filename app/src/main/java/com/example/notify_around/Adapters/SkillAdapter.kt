package com.example.notify_around.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notify_around.R
import com.example.notify_around.models.SkillModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot

class SkillAdapter(options: FirestoreRecyclerOptions<SkillModel?>) :
    FirestoreRecyclerAdapter<SkillModel, SkillAdapter.sViewHolder>(options) {
    private var listener: OnSkillItemClickListener? = null
    override fun onBindViewHolder(holder: sViewHolder, position: Int, model: SkillModel) {
        Log.d("Adapter", "onBindViewHolder")
        holder.tvSkillTitle.text = model.title
        holder.tvSkillDate.text = model.postedOn.toString()
        holder.tvSkillLocation.text = model.locationAt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): sViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_row_skill, parent, false)
        return sViewHolder(view)
    }

    fun setOnSkillItemClickListener(listener: OnSkillItemClickListener?) {
        this.listener = listener
    }

    inner class sViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSkillTitle: TextView
        var tvSkillLocation: TextView
        var tvSkillDate: TextView

        init {
            tvSkillTitle = itemView.findViewById(R.id.tv_skilltitle)
            tvSkillLocation = itemView.findViewById(R.id.tv_skill_location)
            tvSkillDate = itemView.findViewById(R.id.tv_skill_dnt)

            itemView.setOnClickListener {
                listener?.onSkillItemClick(
                    snapshots.getSnapshot(
                        bindingAdapterPosition
                    )
                )
            }

        }
    }

    interface OnSkillItemClickListener {
        fun onSkillItemClick(ds: DocumentSnapshot?)
    }
}