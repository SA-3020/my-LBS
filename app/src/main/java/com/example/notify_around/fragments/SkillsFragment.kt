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


        return binding.root

    }


}