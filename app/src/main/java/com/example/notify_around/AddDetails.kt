package com.example.notify_around

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.models.GeneralUser
import com.example.notify_around.databinding.ActivityAddDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class AddDetails : AppCompatActivity() {

    private lateinit var binding: ActivityAddDetailsBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var phoneNumber: String
    private lateinit var docRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userId = mAuth.currentUser?.uid.toString()
        phoneNumber = intent.getStringExtra("mobileno")!!

        Log.d("TAG", "uid: $userId and phoneNo: $phoneNumber ")

        docRef = db.collection("users").document(userId)

        /*supportFragmentManager.beginTransaction()
            .replace(R.id.details_container, DetailsFragment())
            .commit()*/

    }

    fun saveUser(v: View) {
        //val docRef = db.collection("users").document("userID")

        when {
            !(binding.etFirstName1.text.isBlank() && binding.etLastName1.text.isBlank() && binding.etEmail.text.isBlank()) -> {
                val first = binding.etFirstName1.text.toString()
                val last = binding.etLastName1.text.toString()
                val userEmail = binding.etEmail.text.toString()

                // Create a new user with a first and last name
                val user = GeneralUser(
                    KEY_USERTYPE,
                    phoneNumber,
                    first,
                    last,
                    userEmail
                )

                docRef.set(user)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully written!")
                        startActivity(
                            Intent(
                                applicationContext,
                                FollowInterestsActivity::class.java
                            )
                        )
                        //startActivity(Intent(applicationContext, UserDashboard::class.java))
                        //finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error writing document", e)
                    }

            }
            else -> {
                Toast.makeText(applicationContext, "All Fields are Required", Toast.LENGTH_SHORT)
                    .show()
            }


        }

    }

    companion object {
        private const val KEY_USERTYPE = "General"
        private const val KEY_FIRSTNAME = "First Name"
        private const val KEY_LASTNAME = "Last Name"
        private const val KEY_PHONENUMBER = "Phone"
        private const val KEY_EMAIL = "Email"
    }
}