package com.example.notify_around.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notify_around.models.AdModel
import com.example.notify_around.adapters.AdsAdapter
import com.example.notify_around.businessUser.activities.AdDetailsActivity
import com.example.notify_around.databinding.FragmentAdsBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class AdsFragment : Fragment(),AdsAdapter.OnEventItemClickListener {
    private var _binding: FragmentAdsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdsAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdsBinding.inflate(inflater, container, false)


        val query = FirebaseFirestore.getInstance()
            .collection("Business Ad")
            .orderBy("postedOn")

        val options: FirestoreRecyclerOptions<AdModel?> =
            FirestoreRecyclerOptions.Builder<AdModel>()
                .setQuery(query, AdModel::class.java).build()
        adapter = AdsAdapter(options,requireContext())
        adapter.setOnEventItemClickListener(this)
        binding.adsRecyclerView.adapter = adapter




        return binding.root

    }



    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAdItemClick(ds: DocumentSnapshot?) {

        val model=ds?.toObject(AdModel::class.java)

        val intent=Intent(requireActivity(),AdDetailsActivity::class.java)
        intent.putExtra("adId",model?.id)
        startActivity(intent)

    }


}