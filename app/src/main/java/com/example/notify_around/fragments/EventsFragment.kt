package com.example.notify_around.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.Adapters.EventAdapter
import com.example.notify_around.Models.EventModel
import com.example.notify_around.databinding.FragmentEventsBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EventsFragment : Fragment() {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EventAdapter

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEventsBinding.inflate(inflater, container, false)

        val query = FirebaseFirestore.getInstance()
            .collection("events")
            .orderBy("dateAt")

        val options: FirestoreRecyclerOptions<EventModel> =
            FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel::class.java).build()
        adapter = EventAdapter(options)
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}