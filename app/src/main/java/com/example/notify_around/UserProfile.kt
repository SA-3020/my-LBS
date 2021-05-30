package com.example.notify_around

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfile : AppCompatActivity() {

    private lateinit var user : FirebaseUser
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var name: String
    private lateinit var email: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        //intent.getParcelableExtra<FirebaseUser>("user")!!

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth.currentUser!!

        name = ""
        email = ""

        val docRef =  db.collection("users").document(user.uid)

        docRef
            .get()
            .addOnSuccessListener { document ->
                name = document.get("firstName").toString()
                name.plus(document.get("lastName").toString())
                email = document.get("emailAddress").toString()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Data not found", Toast.LENGTH_SHORT).show()
            }


        /*tv_name.setText(name)
        tv_contact.setText(email)*/

        Log.d(TAG,"name from db: $name")
        Log.d(TAG,"email from db: $email")

        /*user?.let {
            // Name, email address, and profile photo Url
            name = user.displayName.toString()
            email = user.email.toString()
            var photoUrl: Uri? = user.photoUrl
        }

        tv_name.text = name
        tv_contact.text = email.toString()*/

        btn_logout.setOnClickListener {
            LoginManager.getInstance().logOut()
            startActivity(Intent( applicationContext, MainActivity::class.java))
            finish()
        }
    }
}