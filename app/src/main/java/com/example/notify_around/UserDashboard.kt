package com.example.notify_around

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_user_dashboard.*

class UserDashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        /*user = intent.getParcelableExtra<FirebaseUser>("user")!!
        user?.let {
            // Name, email address, and profile photo Url
             name = user.displayName.toString()
             email = user.email.toString()*/


//             photoUrl = user.photoUrl!!

            // Check if user's email is verified
            //val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            //val uid = user.getIdToken(true)
//        }

        //tv_userInfo.text = "name: $name"

        auth = FirebaseAuth.getInstance()

        button.setOnClickListener {
            auth.signOut()
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        btn_Interests.setOnClickListener {
            startActivity(Intent(applicationContext, Interests::class.java ))
        }

        btn_addNew.setOnClickListener{
            startActivity(Intent(applicationContext, CreateEvent::class.java ))
        }

        btn_Events.setOnClickListener {
            startActivity(Intent(applicationContext, Events::class.java ))
        }
        btn_profile.setOnClickListener{
            startActivity(Intent(applicationContext, UserProfile::class.java ))//.putExtra("user", user))
        }
    }
}