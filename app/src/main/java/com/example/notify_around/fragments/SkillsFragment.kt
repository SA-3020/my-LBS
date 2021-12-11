package com.example.notify_around.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.Adapters.SkillAdapter
import com.example.notify_around.SkillDetailsActivity
import com.example.notify_around.databinding.FragmentSkillsBinding
import com.example.notify_around.models.SkillModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SkillsFragment : Fragment(), SkillAdapter.OnSkillItemClickListener {
    private var _binding: FragmentSkillsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SkillAdapter

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
        _binding = FragmentSkillsBinding.inflate(inflater, container, false)

        val query = FirebaseFirestore.getInstance()
            .collection("skills")
            .orderBy("postedOn")

        val options: FirestoreRecyclerOptions<SkillModel?> =
            FirestoreRecyclerOptions.Builder<SkillModel>()
                .setQuery(query, SkillModel::class.java).build()
        adapter = SkillAdapter(options)//SkillAdapter(options)
        binding.skillsRecview.layoutManager = LinearLayoutManager(context)
        binding.skillsRecview.adapter = adapter

        Thread {
            adapter.setOnSkillItemClickListener(this)
        }.start()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSkillItemClick(ds: DocumentSnapshot?) {
        val model = ds?.toObject(SkillModel::class.java)

        val intent = Intent(requireActivity(), SkillDetailsActivity::class.java)
        intent.putExtra("skillId", model?.id)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SkillsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}