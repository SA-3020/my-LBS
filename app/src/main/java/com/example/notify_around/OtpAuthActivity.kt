package com.example.notify_around

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.businessUser.activities.BUserDashboard
import com.example.notify_around.databinding.ActivityOtpAuthBinding
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class OtpAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpAuthBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var vfId: String
    private lateinit var credential: PhoneAuthCredential
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mAuth = Firebase.auth
        /* unpacking the intent and getting verificationId*/
        vfId = intent.getStringExtra("verificationId").toString()
        binding.tvPhoneno.text = mAuth.currentUser?.phoneNumber
    }

    fun nextBtnOnClick(view: View) {
        when {
            binding.etOtp.text.toString().isEmpty() ->
                showMessage("Blank Field can not be processed")
            binding.etOtp.text.toString().length != 6 ->
                showMessage("Invalid OTP")
            else -> {
                credential = PhoneAuthProvider.getCredential(vfId, binding.etOtp!!.text.toString())
                signInWithPhoneAuthCredential(credential)
                //in here FirebaseAuthException is raised
                //when the user enters the code after the set timeout
            }
        }
    }

    // [START on_start_check_user]
    /*override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if(mAuth.currentUser!=null){
            startActivity(Intent(applicationContext, UserDashboard::class.java))
        }

    }*/
    // [END on_start_check_user]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    updateUI(user)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        //resendVerificationToken code
                    }

                }
            }
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun updateUI(user: FirebaseUser? = mAuth.currentUser) {
        Log.d(TAG, "-----------------------${user?.phoneNumber.toString()}")

        //check from database if user already exists
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(user?.uid.toString())
        //Log.d(TAG, "Inside usr exists ${docRef.path}")
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                Log.d(TAG, "doc ref . get ")
                if (documentSnapshot.exists()) {
//                    Log.d(TAG, "Inside usr exists if ")
//                    Log.d(TAG,"DocumentSnapshot data: ${documentSnapshot.getString(KEY_FIRSTNAME)}")
//                    Log.d(TAG,"DocumentSnapshot data: ${documentSnapshot.getString("UserType")}")
//                    //change from here to open business user dashboard
                    if (documentSnapshot.getString("UserType").equals("Business")) {
                        Log.d(TAG, "busssssss")
                        startActivity(Intent(applicationContext, BUserDashboard::class.java))
                        finish()
                    } else
                        startActivity(Intent(applicationContext, UserDashboard::class.java))
                    finish()
                } else {
//                    Log.d(TAG, "Inside usr exists else")
//                    Log.d(TAG, "No such document")
                    startActivity(
                        Intent(
                            applicationContext,
                            AddDetails::class.java
                        ).putExtra("mobileno", user?.phoneNumber)
                    )
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
    // [END sign_in_with_phone]

    companion object {
        private const val TAG = "PhoneAuthActivity"
        private const val KEY_FIRSTNAME = "First Name"
        private const val KEY_LASTNAME = "Last Name"
        private const val KEY_PHONENUMBER = "Phone No"
        private const val KEY_EMAIL = "Email"
    }


}
