package com.example.notify_around

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.Models.EventModel
import com.example.notify_around.databinding.ActivityPostEventBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostEventBinding
    private lateinit var db: FirebaseFirestore
    lateinit var interestsArray: MutableList<String>
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etDate.inputType = 0; binding.etTime.inputType =
            0; binding.tvLevelOfEmer.inputType = 0

        binding.etLocation.inputType = 0
        db = FirebaseFirestore.getInstance()
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
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }.start()

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    //handle the data here
                }
            }
    }

    fun getDateFromUser(view: android.view.View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this@PostEventActivity, R.style.my_dialog_theme,
            { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                binding.etDate.setText("$dayOfMonth/ $monthOfYear/ $year")
            }, year, month, day
        )
        dpd.show()
    }

    fun getTimeFromUser(view: android.view.View) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            binding.etTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        TimePickerDialog(
            this,
            R.style.my_dialog_theme,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    fun postNewEvent(v: View) {
        if (binding.etTitle.text.isBlank())
            showMessage("Please enter event title")
        if (binding.etDescription.text.isBlank())
            showMessage("Please enter event description")
        if (binding.etDate.text.isBlank() || binding.etTime.text.isBlank())
            showMessage("Event date and time are required")
        if (binding.etLocation.text.isBlank())
            showMessage("Please select a location for the event")
        if (binding.etTitle.text.isBlank() && binding.etDescription.text.isBlank() && binding.etDate.text.isBlank() && binding.etTime.text.isBlank() && binding.etLocation.text.isBlank())
            showMessage("All fields are required")
        if (!binding.etTitle.text.isBlank() && !binding.etDescription.text.isBlank() && !binding.etDate.text.isBlank() && !binding.etTime.text.isBlank()/* && !binding.etLocation.text.isBlank()*/) {
            Thread {
                val newEventRef = db.collection("events").document()
                var eventid = newEventRef.id

                val event = EventModel(
                    binding.etTitle.text.toString(),
                    binding.etDescription.text.toString(),
                    null, Timestamp.now(),
                    FirebaseAuth.getInstance().currentUser?.uid.toString(),
                    MultiselectDialog.selectedInterestsArray,
                    binding.etDate.text.toString(),
                    binding.etTime.text.toString()
                )
                newEventRef.set(event)
                    .addOnSuccessListener {
                        runOnUiThread {
                            showMessage("Event Uploaded successfully")
                        }
                    }
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid
                val userRef = db
                    .collection("users")
                    .document(currentUser.toString())
                //continue working from here

            }.start()

            startActivity(Intent(this, UserDashboard::class.java))
            finish()
        }
    }

    private fun showMessage(m: String) {
        Toast.makeText(applicationContext, m, Toast.LENGTH_SHORT).show()
    }

    fun showDialog(view: android.view.View) {
        MultiselectDialog(interestsArray, binding.tvLevelOfEmer, "OK").show(
            supportFragmentManager,
            "interestDialog"
        )
    }

    fun openMapActivity(view: android.view.View) {
        val intent = Intent(this, MapActivity::class.java)
        resultLauncher.launch(intent)
    }
}