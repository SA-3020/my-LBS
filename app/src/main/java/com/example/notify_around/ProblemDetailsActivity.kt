package com.example.notify_around

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.databinding.ActivityProblemDetailsBinding
import com.example.notify_around.models.ProblemModel
import com.google.firebase.firestore.FirebaseFirestore

class ProblemDetailsActivity : AppCompatActivity() {
    private lateinit var b: ActivityProblemDetailsBinding
    private lateinit var builder: StringBuilder
    private lateinit var contact: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProblemDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)

        val id = intent.extras?.getString("problemId")
        Log.v("Details", id.toString())


        Thread {
            val query = FirebaseFirestore.getInstance()
                .collection("problems").document(id!!)


            query.get().addOnSuccessListener {
                val model = it.toObject(ProblemModel::class.java)!!


                val userid = model.postedBy.replace("/", "")
                Log.d(TAG, "phoeee $userid")
                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(userid).get().addOnSuccessListener {
                        contact = it.get("phoneNo").toString()
                        runOnUiThread {
                            b.tvEventTitle.text = model.title
                            b.tvProblemDesc.text = model.description
                            b.tvDate.text = model.dateAt
                            b.tvTime.text = model.timeAt
                            b.tvProlemLocation.text = model.locationAt
                            b.tvPostedBy.text =
                                "${it.get("firstName").toString()} ${it.get("lastName").toString()}"
                            b.tvLevelOfEmer.text = model.levelOfEmergency
                            b.progressBar.visibility = View.INVISIBLE
                            b.btnDialer.visibility = View.VISIBLE

                        }

                    }

            }.addOnFailureListener {

                Log.v("Details", it.message.toString())
            }


        }.start()


        b.btnDialer.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$contact")
            startActivity(intent)
        }
    }


}