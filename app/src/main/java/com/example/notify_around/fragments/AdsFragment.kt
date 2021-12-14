package com.example.notify_around.fragments

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notify_around.Adapters.AdsAdapter
import com.example.notify_around.UserManager
import com.example.notify_around.businessUser.activities.AdDetailsActivity
import com.example.notify_around.databinding.FragmentAdsBinding
import com.example.notify_around.models.AdModel
import com.google.firebase.firestore.FirebaseFirestore


class AdsFragment : Fragment(), AdsAdapter.OnEventItemClickListener {
    private var _binding: FragmentAdsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdsAdapter
    private var adsList= mutableListOf<AdModel>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdsBinding.inflate(inflater, container, false)


        com.example.notify_around.UserManager.user?.interests?.let {
            FirebaseFirestore.getInstance()
                .collection("Business Ad").whereArrayContainsAny("interests", it)
                .orderBy("postedOn").get().addOnCompleteListener{documentSnapshot ->

                    for(document in documentSnapshot.result){

                        val model = document.toObject(AdModel::class.java)


                        val adLocation=model.geoPoints
                        val userLocation= UserManager.user?.location

                        var distance=getDistanceBetweenTwoPoints(adLocation?.latitude,adLocation?.longitude,userLocation?.latitude,userLocation?.longitude)

                        distance /= 1000

                        if(distance>0.0&&distance<=10){
                            adsList.add(model)
                        }

                        Log.v("Ads",distance.toString())

                    }

                    initAdapter()
                }
        }




        return binding.root

    }



}