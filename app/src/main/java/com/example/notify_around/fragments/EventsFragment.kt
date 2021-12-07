package com.example.notify_around.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.EventDetailsActivity
import com.example.notify_around.adapters.EventAdapter
import com.example.notify_around.businessUser.activities.AdDetailsActivity
import com.example.notify_around.models.EventModel
import com.example.notify_around.databinding.FragmentEventsBinding
import com.example.notify_around.models.AdModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore



class EventsFragment : Fragment(),EventAdapter.OnEventItemClickListener {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EventAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEventsBinding.inflate(inflater, container, false)

        val query = FirebaseFirestore.getInstance()
            .collection("events")
            .orderBy("dateAt")

        val options: FirestoreRecyclerOptions<EventModel?> =
            FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel::class.java).build()
        adapter = EventAdapter(options,requireContext())
        adapter.setOnEventItemClickListener(this)
        binding.eventsRecview.layoutManager = LinearLayoutManager(context)
        binding.eventsRecview.adapter = adapter



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    override fun onEventItemClick(ds: DocumentSnapshot?) {

        val model=ds?.toObject(EventModel::class.java)

        val intent= Intent(requireActivity(), EventDetailsActivity::class.java)
        intent.putExtra("eventId",model?.id)
        startActivity(intent)
    }


}