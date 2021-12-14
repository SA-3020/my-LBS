package com.example.notify_around.fragments

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.Adapters.SkillAdapter
import com.example.notify_around.ProblemDetailsActivity
import com.example.notify_around.UserManager
import com.example.notify_around.databinding.FragmentSkillsBinding
import com.example.notify_around.models.EventModel
import com.example.notify_around.models.ProblemModel
import com.example.notify_around.models.SkillModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore



class SkillsFragment : Fragment(), SkillAdapter.OnSkillItemClickListener {
    private var _binding: FragmentSkillsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SkillAdapter
    private var skillsList= mutableListOf<SkillModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSkillsBinding.inflate(inflater, container, false)


        UserManager.user?.interests?.let {
            
            FirebaseFirestore.getInstance()
                .collection("skills").whereArrayContainsAny("interests", it)
                .orderBy("postedOn").get().addOnCompleteListener { documentSnapshot ->

                    for (document in documentSnapshot.result) {

                        val model = document.toObject(SkillModel::class.java)


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
                            skillsList.add(model)
                        }



                    }

                    initAdapter()
                }




        }


        return binding.root

    }

    private fun initAdapter(){

        if(activity!=null&&activity?.isFinishing != true) {
            adapter = SkillAdapter(skillsList)
            binding.skillsRecview.layoutManager = LinearLayoutManager(context)
            binding.skillsRecview.adapter = adapter
            adapter.setOnSkillItemClickListener(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSkillItemClick(position:Int) {
        val model= skillsList[position]

        val intent = Intent(requireActivity(), ProblemDetailsActivity::class.java)
        intent.putExtra("skillId", model.id)
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