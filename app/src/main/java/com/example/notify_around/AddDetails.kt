package com.example.notify_around

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_details.*

class AddDetails : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userId = auth.currentUser?.uid.toString()

        val docRef = db.collection("users").document("userID")

        btn_save.setOnClickListener {
            when {
                !(et_FirstName.text.isBlank() && et_LastName.text.isBlank() && et_email.text.isBlank()) -> {
                    val first = et_FirstName.text.toString()
                    val last = et_LastName.text.toString()
                    val userEmail = et_email.text.toString()

                    // Create a new user with a first and last name
                    val user = hashMapOf<String, Any>(
                        "firstName" to first,
                        "lastName" to last,
                        "emailAddress" to userEmail
                    )

                    //add a new document with a generated ID
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { docRef ->
                            startActivity(Intent(applicationContext,UserDashboard::class.java));
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "Data is not inserted", Toast.LENGTH_SHORT).show()
                        }

                }
                else -> {
                    Toast.makeText(applicationContext, "All Fields are Required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}