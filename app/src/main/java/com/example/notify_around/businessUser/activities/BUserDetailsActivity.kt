package com.example.notify_around.businessUser.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notify_around.databinding.ActivityBuserDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class BUserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuserDetailsBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var docRef: DocumentReference
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        docRef = db.collection("users").document(auth.currentUser?.uid!!)
        binding.btnSave.setOnClickListener {
            docRef.update(
                mapOf(
                    "UserType" to "Business",
                    "CompanyName" to binding.etCompanyName.text,
                    "BusinessContact" to binding.etContactNum,
                    "BusinessEmail" to binding.etEmail.text,
                    "Address" to binding.etLocation.text
                )
            )
            startActivity(Intent(applicationContext, BUserDashboard::class.java))
        }
    }

    private fun getCurrentLocation(

    ) {

    }
}