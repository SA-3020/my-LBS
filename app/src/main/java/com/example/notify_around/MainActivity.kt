package com.example.notify_around

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin = btn_login_facebook
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

        sign_up_using_phone_btn.setOnClickListener {
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
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
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
        startActivity(Intent(applicationContext, UserDashboard::class.java ).putExtra("user", user))
        finish()
    }

}