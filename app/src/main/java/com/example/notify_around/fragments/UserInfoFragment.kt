package com.example.notify_around.fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notify_around.models.GeneralUser
import com.example.notify_around.databinding.FragmentUserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserInfoFragment : Fragment() {
    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var docRef: DocumentReference
    private val db = FirebaseFirestore.getInstance()

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
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser!!

        docRef = db.collection("users").document(user.uid)
        docRef.get()
            .addOnSuccessListener {
                var user = it.toObject(GeneralUser::class.java)
                binding.tvEmail.text = user?.Email
                binding.tvContact.text = user?.PhoneNo

            }
        /*val credential = PhoneAuthProvider.getCredential("+923348562181", "123807")
        user.reauthenticate(credential)*/

        binding.btnEdit.setOnClickListener {
            val nextFrag = EditUserInfoFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(((view as ViewGroup).parent as View).id, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }

        binding.btnDeleteProfile.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to permanently delete your ID?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener() { dialog, which ->
                    user!!
                        .delete()
                        .addOnCompleteListener {
                            /*startActivity(Intent(requireActivity().baseContext, MainActivity::class.java))
                            this.requireActivity().finish()*/
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "$it ${auth.currentUser!!.uid}")
                        }
                })
                .setNegativeButton("Cancel", null).show()

        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}