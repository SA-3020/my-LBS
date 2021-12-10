package com.example.notify_around.drawerActivities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.fragments.UserInfoFragment
import com.example.notify_around.models.GeneralUser
import com.example.notify_around.R
import com.example.notify_around.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class UserProfile : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var docRef: DocumentReference
    private var name: String = ""
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.user_info_frame, UserInfoFragment())
            .commit()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = FirebaseFirestore.getInstance()
        docRef = db.collection("users").document(user.uid)
        GlobalScope.launch {
            showUser()
        }
        setContentView(view)
        Log.d(TAG, "current user ${docRef}")

    }

    fun showUser() {
        //delay(500L)
        docRef
            .get()
            .addOnSuccessListener {
                val currentUser = it.toObject(GeneralUser::class.java)!!
                var username = "${currentUser.FirstName} ${currentUser.LastName}"
                binding.tvName.text = username
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Data not found", Toast.LENGTH_SHORT).show()
            }
    }
}