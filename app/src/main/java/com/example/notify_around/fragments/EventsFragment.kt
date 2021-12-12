package com.example.notify_around.fragments

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.Adapters.EventAdapter
import com.example.notify_around.EventDetailsActivity
import com.example.notify_around.UserManager
import com.example.notify_around.databinding.FragmentEventsBinding
import com.example.notify_around.models.EventModel
import com.google.firebase.firestore.FirebaseFirestore


class EventsFragment : Fragment(), EventAdapter.OnEventItemClickListener {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterMy: EventAdapter
    private var eventsList= mutableListOf<EventModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEventsBinding.inflate(inflater, container, false)


        UserManager.user?.interests?.let {
            FirebaseFirestore.getInstance()
                .collection("events").whereArrayContainsAny("interests", it)
                .orderBy("dateAt").get().addOnCompleteListener { documentSnapshot ->

                    for (document in documentSnapshot.result) {

                        val model = document.toObject(EventModel::class.java)


                        val adLocation = model.geoPoints
                        val userLocation = UserManager.user?.location

                        var distance = getDistanceBetweenTwoPoints(
                            adLocation?.latitude,
                            adLocation?.longitude,
                            userLocation?.latitude,
                            userLocation?.longitude
                        )

                        distance /= 1000

                        if (distance <= 10) {
                            eventsList.add(model)
                        }



                    }
                    initAdapter()
                }




        }

        return binding.root
    }

     private fun initAdapter() {

         adapterMy =
             EventAdapter(eventsList, requireActivity().applicationContext)//requireContext())
         adapterMy.setOnEventItemClickListener(this)
         binding.eventsRecview.layoutManager = LinearLayoutManager(context)
         binding.eventsRecview.adapter = adapterMy


     }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    override fun onEventItemClick(position:Int) {

        val model= eventsList[position]

        val intent= Intent(requireActivity(), EventDetailsActivity::class.java)
        intent.putExtra("eventId",model.id)
        startActivity(intent)
    }


    private fun getDistanceBetweenTwoPoints(
        lat1: Double?,
        lon1: Double?,
        lat2: Double?,
        lon2: Double?
    ): Float {
        val distance = FloatArray(2)
        if (lat1 != null) {
            if (lon1 != null) {
                if (lat2 != null) {
                    if (lon2 != null) {
                        Location.distanceBetween(
                            lat1, lon1,
                            lat2, lon2, distance
                        )
                    }
                }
            }
        }
        return distance[0]
    }

}