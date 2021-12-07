package com.example.notify_around.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notify_around.Adapters.ProblemAdapter.pViewHolder
import com.example.notify_around.Models.ProblemModel
import com.example.notify_around.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import java.text.MessageFormat

class ProblemAdapter(options: FirestoreRecyclerOptions<ProblemModel?>) :
    FirestoreRecyclerAdapter<ProblemModel, pViewHolder>(options) {
    private var listener: OnProblemItemClickListener? = null
    override fun onBindViewHolder(holder: pViewHolder, position: Int, model: ProblemModel) {
        Log.d("Adapter", "onBindViewHolder")
        holder.tvProblemTitle.text = model.title
        holder.tvProblemDatenTime.text =
            MessageFormat.format("{0}  {1}", model.dateAt, model.timeAt)
        holder.tvProblemLocation.text = model.locationAt
        holder.tvEmergencyLevel.setText(model.levelOfEmergency);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_row_problem, parent, false)
        return pViewHolder(view)
    }

    fun setOnProblemItemClickListener(listener: OnProblemItemClickListener?) {
        this.listener = listener
    }

    inner class pViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvProblemTitle: TextView
        var tvProblemLocation: TextView
        var tvProblemDatenTime: TextView
        var tvEmergencyLevel: TextView

        init {
            tvProblemTitle = itemView.findViewById(R.id.tv_problemtitle)
            tvProblemLocation = itemView.findViewById(R.id.tv_problem_location)
            tvProblemDatenTime = itemView.findViewById(R.id.tv_problem_dnt)
            tvEmergencyLevel = itemView.findViewById(R.id.tv_emergency_level)

            itemView.setOnClickListener {
                listener?.onProblemItemClick(
                    snapshots.getSnapshot(
                        bindingAdapterPosition
                    )
                )
            }

        }
    }

    interface OnProblemItemClickListener {
        fun onProblemItemClick(ds: DocumentSnapshot?)
    }
}