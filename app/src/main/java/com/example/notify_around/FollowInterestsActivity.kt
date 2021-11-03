package com.example.notify_around

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.Adapters.InterestAdapter
import com.example.notify_around.Models.InterestsModel
import com.example.notify_around.databinding.ActivityFollowInterestsBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class FollowInterestsActivity : AppCompatActivity(), InterestAdapter.OnInterestItemClickListener {
    private lateinit var binding: ActivityFollowInterestsBinding
    private lateinit var adapter: InterestAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowInterestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val query = FirebaseFirestore.getInstance()
            .collection("interests")
            .orderBy("Title")

        val options: FirestoreRecyclerOptions<InterestsModel> =
            FirestoreRecyclerOptions.Builder<InterestsModel>()
                .setQuery(query, InterestsModel::class.java)
                .build()

        adapter = InterestAdapter(options)
        binding.interestRecview.layoutManager = LinearLayoutManager(baseContext)
        binding.interestRecview.adapter = adapter

        adapter.setOnInterestItemClickListener(this)

        binding.btn.setOnClickListener {
            startActivity(Intent(applicationContext, UserDashboard::class.java))
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onInterestItemClick(ds: DocumentSnapshot?, position: Int, action: String) {
        //for debugging
        //val inter = ds?.toObject(InterestsModel::class.java)
        val interestid = ds?.id

        //actual code needed
        /*
        * check if the action is set to follow or unfollow
        * if action is follow, add interest id in the interests array of the user
        * else if the action is unfollow, remove the id of current interest from the interests array of the user
        */
        val userRef = FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
        if (action.equals("follow", true)) userRef.update(
            "interests",
            FieldValue.arrayUnion(interestid)
        )
        else if (action.equals("unfollow", true)) userRef.update(
            "interests",
            FieldValue.arrayRemove(interestid)
        )
    }
}