package com.example.notify_around

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MultiselectDialog(
    var interestsArray: MutableList<String>,
    var view: TextView?,
    var positiveButtonText: String,
    private val checkedItemIndexes: ArrayList<Int> = ArrayList(),
    private var checked: BooleanArray = BooleanArray(interestsArray.size),
) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //super.onCreate(savedInstanceState)

        return activity?.let {
            val alertBuilder = AlertDialog.Builder(it)

            var array = interestsArray.toTypedArray()

            alertBuilder
                .setTitle("Select Interests")
                .setMultiChoiceItems(
                    array,
                    checked
                ) { _: DialogInterface, index: Int, isCheckedIndex: Boolean ->
                    if (isCheckedIndex) {
                        checkedItemIndexes.add(index)
                        checked[index] = true
                    } else if (checkedItemIndexes.contains(index)) {
                        checkedItemIndexes.remove(index)
                        checked[index] = false
                    }
                }
                .setPositiveButton(positiveButtonText) { dialog, id ->
                    Log.d(
                        "DialogLog",
                        "OK Pressed, checked index is $checkedItemIndexes \n ${checked[3]}"
                    )
                    for (index in checkedItemIndexes) {
                        selectedInterestsArray.add(interestsArray[index])
                        if (positiveButtonText.equals("OK", true))
                            view?.text = view?.text.toString().plus("${interestsArray[index]}, ")

                        else if (positiveButtonText.equals("follow", true))
                            Thread {
                                val db = FirebaseFirestore.getInstance()
                                val userRef = db
                                    .collection("users")
                                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())

                                /*db.collection("interests")
                                    .document()
                                    .get()
                                    .addOnSuccessListener {
                                        if (it.equals(interestsArray[index]))*/
                                            userRef.update(
                                                "interests",
                                                FieldValue.arrayUnion(interestsArray[index])
                                            )
                                        Log.d(TAG, "ooo ${it}")
                                //}

                            }.start()
                    }
                }
                .setNeutralButton("Clear All") { dialog, id ->
                    //checked = booleanArrayOf(false)
                    array = arrayOf()
                    view?.text = ""
                    interestsArray = arrayListOf()
                    selectedInterestsArray = ArrayList()
                }

            alertBuilder.create()
        } ?: throw IllegalStateException("Exception !! Activity is null")
    }

    companion object {
        var selectedInterestsArray: MutableList<String> = ArrayList()
    }
}