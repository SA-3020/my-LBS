package com.example.notify_around.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.notify_around.databinding.FragmentEditUserInfoBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditUserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditUserInfoFragment : Fragment() {
    private var _binding: FragmentEditUserInfoBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var docRef: DocumentReference

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditUserInfoBinding.inflate(layoutInflater, container, false)
        docRef = FirebaseAuth.getInstance().currentUser?.let {
            db.collection("users").document(it.uid)
        }!!

        var newPhoneNumber = binding.etPhone.text
        var newFirstName = binding.etFirstName1.text.toString()
        var newLastName = binding.etLastName1.text.toString()
        var newEmail = binding.etEmail.text.toString()
        Log.d(TAG, "UPDATE USERNAME --- $newFirstName $newLastName")

        binding.btnSave.setOnClickListener {
            if (!binding.etFirstName1.text.isBlank() && binding.etEmail.text.isBlank()) {
                if (!binding.etLastName1.text.isBlank()) {
                    updateUserName(newFirstName, newLastName)
                } else
                    showMessage("Please enter full username")

            } else if (!binding.etEmail.text.isBlank() && binding.etFirstName1.text.isBlank() && binding.etLastName1.text.isBlank())
                updateEmail(newEmail)
            else
                updateAll(newFirstName, newLastName, newEmail)
        }

        return binding.root
    }

    private fun updateAll(fn: String, ln: String, em: String) {
        docRef.update(
            mapOf(
                "firstName" to fn,
                "lastName" to ln,
                "email" to em
            )
        )
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    private fun reauthenticateUser() {
        val phoneNum = "+16505554567"
        val testVerificationCode = "123456"

        // Whenever verification is triggered with the whitelisted number,
        // provided it is not set for auto-retrieval, onCodeSent will be triggered.
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNum)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(this.requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Save the verification id somewhere
                    // ...

                    // The corresponding whitelisted code above should be used to complete sign-in.
                    enableUserManuallyInputCode()
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    auth.signInWithCredential(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // ...
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun enableUserManuallyInputCode() {
        TODO("Not yet implemented")
    }

    fun updateUserName(fn: String, ln: String) {
        Log.d(TAG, "UPDATE USERNAME $fn $ln")
        docRef.update(
            mapOf(
                "firstName" to fn,
                "lastName" to ln
            )
        )
    }

    fun updateEmail(newEmail: String) {
        docRef.update(
            mapOf(
                "email" to newEmail,
            )
        )
    }

    fun updatePhone() {}

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditUserInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}