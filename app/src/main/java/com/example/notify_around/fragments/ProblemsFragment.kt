package com.example.notify_around.fragments

import android.content.ContentValues.TAG
import android.content.Intent
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
import com.example.notify_around.databinding.FragmentProblemsBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProblemsFragment : Fragment(), ProblemAdapter.OnProblemItemClickListener {
    private var _binding: FragmentProblemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProblemAdapter

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
        _binding = FragmentProblemsBinding.inflate(inflater, container, false)

        val query = FirebaseFirestore.getInstance()
            .collection("problems")
            .orderBy("dateAt")

        val options: FirestoreRecyclerOptions<ProblemModel?> =
            FirestoreRecyclerOptions.Builder<ProblemModel>()
                .setQuery(query, ProblemModel::class.java).build()
        adapter = ProblemAdapter(options)
        binding.problemsRecview.layoutManager = LinearLayoutManager(context)
        binding.problemsRecview.adapter = adapter

        Thread {
            adapter.setOnProblemItemClickListener(this)
        }.start()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


    override fun onProblemItemClick(ds: DocumentSnapshot?) {
        //Log.d(TAG, "this is called")
        val model = ds?.toObject(ProblemModel::class.java)

        val intent = Intent(requireActivity(), ProblemDetailsActivity::class.java)
        intent.putExtra("problemId", model?.id)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProblemsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}