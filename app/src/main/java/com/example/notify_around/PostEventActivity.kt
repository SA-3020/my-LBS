package com.example.notify_around

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anurag.multiselectionspinner.MultiSelectionSpinnerDialog
import com.example.notify_around.databinding.ActivityPostEventBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PostEventActivity : AppCompatActivity(),
    MultiSelectionSpinnerDialog.OnMultiSpinnerSelectionListener {
    private lateinit var binding: ActivityPostEventBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private lateinit var docRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contentList: MutableList<String> = ArrayList()

        db.collection("interests")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.get("title")} => ${document.data}")
//                    contentList.add(document.get("title") as String)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        contentList.add("One")
        contentList.add("Two")
        contentList.add("Three")
        contentList.add("Four")
        contentList.add("Five")

        binding.spinner.setAdapterWithOutImage(this, contentList, this)

    }

    override fun OnMultiSpinnerItemSelected(chosenItems: MutableList<String>?) {
        //This is where you get all your items selected from the Multi Selection Spinner :)
        for (i in chosenItems!!.indices) {

            Log.e("chosenItems", chosenItems[i])
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
                binding.etDate.text = "$dayOfMonth/ $monthOfYear/ $year"

            }, year, month, day
        )

        dpd.show()
    }

    fun getTimeFromUser(view: android.view.View) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            binding.etTime.text = SimpleDateFormat("HH:mm").format(cal.time)
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

        FirebaseFirestore.getInstance().collection("events")

    }

    private fun showMessage(m: String) {
        Toast.makeText(applicationContext, m, Toast.LENGTH_SHORT).show()
    }
}