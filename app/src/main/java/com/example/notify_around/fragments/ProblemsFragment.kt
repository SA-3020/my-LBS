package com.example.notify_around.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.Adapters.ProblemAdapter
import com.example.notify_around.models.ProblemModel
import com.example.notify_around.ProblemDetailsActivity
import com.example.notify_around.UserManager
import com.example.notify_around.databinding.FragmentProblemsBinding
import com.example.notify_around.models.EventModel
import com.example.notify_around.models.SkillModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class ProblemsFragment : Fragment(), ProblemAdapter.OnProblemItemClickListener {
    private var _binding: FragmentProblemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProblemAdapter
    private var problemsList= mutableListOf<ProblemModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProblemsBinding.inflate(inflater, container, false)


        FirebaseFirestore.getInstance()
            .collection("problems")
            .orderBy("dateAt").get().addOnCompleteListener { documentSnapshot ->

                for (document in documentSnapshot.result) {

                    val model = document.toObject(ProblemModel::class.java)


                    val adLocation = model.geoPoints
                    val userLocation = UserManager.user?.location

                    var distance = getDistanceBetweenTwoPoints(
                        adLocation?.latitude,
                        adLocation?.longitude,
                        userLocation?.latitude,
                        userLocation?.longitude
                    )

                    distance /= 1000

                    if (distance>0.0&&distance <= 10) {
                        problemsList.add(model)
                    }



                }

                initAdapter()
            }


        return binding.root
    }


    private fun initAdapter(){

        adapter = ProblemAdapter(problemsList)
        binding.problemsRecview.layoutManager = LinearLayoutManager(context)
        binding.problemsRecview.adapter = adapter
        adapter.setOnProblemItemClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


    override fun onProblemItemClick(position:Int) {
        val model= problemsList[position]
        val intent = Intent(requireActivity(), ProblemDetailsActivity::class.java)
        intent.putExtra("problemId", model.id)
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