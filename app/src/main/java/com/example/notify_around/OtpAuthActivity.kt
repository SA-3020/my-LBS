package com.example.notify_around

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_get_phone_no.*
import kotlinx.android.synthetic.main.activity_otp_auth.*
import java.util.concurrent.TimeUnit


class OtpAuthActivity : AppCompatActivity() {

//    var t2: EditText? = null
//    var b2: Button? = null
    private lateinit var phoneNumber: String
    private lateinit var otpId: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var callbacks: OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_auth)

        phoneNumber = intent.getStringExtra("mobileno").toString()

        tv_phoneno.text = phoneNumber

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                otpId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        }
        initiateOtp()

        btn_cancel.setOnClickListener {
            when {
                et_otp.text.toString().isEmpty() -> Toast.makeText(
                    applicationContext,
                    "Blank Field can not be processed",
                    Toast.LENGTH_LONG
                ).show()
                et_otp.text.toString().length != 6 -> Toast.makeText(
                    applicationContext, "Invalid OTP", Toast.LENGTH_LONG
                ).show()
                else -> {
                    val credential = PhoneAuthProvider.getCredential(otpId, et_otp!!.text.toString())
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }
    }

    private fun initiateOtp() {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser!=null)
            checkUserProfile()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    checkUserProfile();

                    /*val user = task.result?.user
                    updateUI(user)
                    startActivity(Intent(this@OtpAuthActivity, AddDetails::class.java))
                    finish()*/
                } else {
                    Toast.makeText(applicationContext, "Signin Code Error", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun checkUserProfile() {
        val docRef = mAuth.currentUser?.let { db.collection("users").document(it.uid) }
        docRef?.get()?.addOnSuccessListener{ documentSnaphot ->

            if(documentSnaphot.exists()){
                updateUI(mAuth.currentUser)
            }
            else
                startActivity(Intent(this@OtpAuthActivity, AddDetails::class.java))

        }
    }

    private fun updateUI(user: FirebaseUser? = mAuth.currentUser){
        startActivity(Intent(this@OtpAuthActivity, UserDashboard::class.java))
        finish()
    }
}