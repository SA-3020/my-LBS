package com.example.notify_around.businessUser.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.MapActivity
import com.example.notify_around.UserDashboard
import com.example.notify_around.UserManager
import com.example.notify_around.databinding.ActivityBuserDetailsBinding
import com.example.notify_around.models.BusinessUser
import com.example.notify_around.models.GeneralUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint


class BUserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuserDetailsBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var docRef: DocumentReference
    private val db = FirebaseFirestore.getInstance()
    private var selectedLatLng:GeoPoint?=null
    private lateinit var currentUser: GeneralUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        docRef = db.collection("users").document(auth.currentUser?.uid!!)

        docRef.get().addOnSuccessListener {

            currentUser = it.toObject(GeneralUser::class.java)!!
        }

        binding.tv1.setNavigationOnClickListener {
            startActivity(Intent(this, UserDashboard::class.java))
            this.finish()
        }

        binding.btnSave.setOnClickListener {

            val businessUser = BusinessUser(
                binding.etCompanyName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etContactNum.text.toString(),
                selectedLatLng
            )

            val userType = "Business"

            val user: MutableMap<String, Any> = HashMap()
            user["businessUser"] = businessUser
            user["userType"] = userType




            docRef.update(user).addOnSuccessListener {
                finish()

                currentUser.businessUser=businessUser
                currentUser.userType=userType
                UserManager.user=currentUser
            }


        }

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if(intent!=null){

                    binding.etLocation.setText(intent.getStringExtra("location"))
                    val lat=intent.getStringExtra("lat")
                    val lng=intent.getStringExtra("lng")
                    val latLng=GeoPoint(lat!!.toDouble(), lng!!.toDouble())
                    selectedLatLng=latLng


                }

            }
        }

        binding.etLocation.setOnClickListener {

            startForResult.launch(Intent(this,MapActivity::class.java))
        }


    }


}