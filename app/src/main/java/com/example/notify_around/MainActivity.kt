package com.example.notify_around

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.businessUser.activities.BUserDashboard
import com.example.notify_around.databinding.ActivityMainBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        //redirect()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin = binding.btnLoginFacebook
        buttonFacebookLogin.setPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }
            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }
            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })

        binding.signUpUsingPhoneBtn.setOnClickListener {
            val m_intent = Intent(this@MainActivity, GetPhoneNoActivity::class.java)
            startActivity(m_intent)
        }

        /*btn_login_facebook.setOnClickListener {
            val m_intent = Intent(this@MainActivity, FacebookSignUpActivity::class.java)
            startActivity(m_intent)
        }*/
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth?.currentUser

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }



    // ...
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(applicationContext, UserDashboard::class.java).putExtra("user", user))
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (auth?.currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(auth!!.currentUser?.uid.toString())
            Log.d(TAG, "Inside usr exists ${docRef.path}")
            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    Log.d(TAG, "doc ref . get ")
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Inside usr exists if ")

                        Log.d(
                            TAG,
                            "DocumentSnapshot data: ${documentSnapshot.getString("UserType")}"
                        )
                        //change from here to open business user dashboard
                        if (documentSnapshot.getString("UserType").equals("Business")) {
                            Log.d(TAG, "busssssss")
                            startActivity(Intent(applicationContext, BUserDashboard::class.java))
                            finish()
                        } else
                            startActivity(Intent(applicationContext, UserDashboard::class.java))

                    }

                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
            startActivity(Intent(applicationContext, UserDashboard::class.java))
            finish()
        }
    }
}