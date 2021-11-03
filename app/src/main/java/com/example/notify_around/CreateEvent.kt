package com.example.notify_around

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.databinding.ActivityCreateEventBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class CreateEvent : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventBinding

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = FirebaseFirestore.getInstance()
        binding.btnNext1.setOnClickListener {
            startActivity(Intent(applicationContext, UserDashboard::class.java))
            finish()

        }

        val docRef = db.collection("events").document("userID")

        binding.btnSave.setOnClickListener {

        }
    }
}