package com.example.notify_around

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.notify_around.Utils.MethodsUtils
import com.example.notify_around.databinding.ActivityPostSkillBinding
import com.example.notify_around.models.EventModel
import com.example.notify_around.models.SkillModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

class PostSkillActivity : AppCompatActivity() {
    private lateinit var b: ActivityPostSkillBinding
    private var selectedLatLng: GeoPoint? = null
    private var db = FirebaseFirestore.getInstance()
    lateinit var interestsArray: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPostSkillBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.tvInterests.inputType = 0; b.etLocation.inputType = 0

        b.toolbar.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, AddNewItem::class.java)); finish()
        }

        Thread {
            interestsArray = ArrayList()
            db.collection("interests")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
//                        Log.d("dialoglog","inside for")
//                        Log.d(TAG, "${document.get("Title")} => ${document.data}")
                        interestsArray.add(document.get("Title").toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.start()

        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {

                        b.etLocation.setText(intent.getStringExtra("location"))
                        val lat = intent.getStringExtra("lat")
                        val lng = intent.getStringExtra("lng")
                        val latLng = GeoPoint(lat!!.toDouble(), lng!!.toDouble())
                        selectedLatLng = latLng
                    }

                }
            }

        b.etLocation.setOnClickListener {
            startForResult.launch(Intent(this, MapActivity::class.java))
        }
    }

    /*fun getDateFromUser(view: android.view.View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this, R.style.my_dialog_theme,
            { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                b.etDate.setText("$dayOfMonth/ $monthOfYear/ $year")

            }, year, month, day
        )

        dpd.show()
    }*/

    fun postNewSkill(v: View) {
        if (b.etTitle.text.isBlank() || b.etDescription.text.isBlank() || b.tvInterests.text.isBlank() || b.etLocation.text.isBlank())
            MethodsUtils.makeShortToast(this, "All fields are required")
        if (!b.etTitle.text.isBlank() && !b.etDescription.text.isBlank() && !b.etLocation.text.isBlank()) {
            Thread {
                var skillid = db.collection("skills").document().id
                val newSkillRef = db.collection("skills").document(skillid)
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                val event = SkillModel(
                    skillid,
                    b.etTitle.text.toString(),
                    b.etDescription.text.toString(),
                    MultiselectDialog.selectedInterestsArray,
                    Timestamp.now(),
                    uid!!,
                    b.etLocation.text.toString(),
                )
                newSkillRef.set(event)
                    .addOnSuccessListener {
                        runOnUiThread {
                            MethodsUtils.makeShortToast(this, "Skill Uploaded successfully")
                        }
                    }
            }.start()

            startActivity(Intent(this, UserDashboard::class.java))
            finish()
        }
    }

    fun showDialog(view: android.view.View) {
        MultiselectDialog(interestsArray, b.tvInterests, "OK").show(
            supportFragmentManager,
            "interestDialog"
        )
    }
}