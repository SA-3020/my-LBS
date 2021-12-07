package com.example.notify_around

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.notify_around.Models.ProblemModel
import com.example.notify_around.databinding.ActivityProblemDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProblemDetailsActivity : AppCompatActivity() {
    private lateinit var b: ActivityProblemDetailsBinding
    private lateinit var model: ProblemModel
    private lateinit var builder: StringBuilder
    private lateinit var contact: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProblemDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)

        val id = intent.extras?.getString("eventId")
        Log.v("Details", id.toString())

        Thread {
            var query = FirebaseFirestore.getInstance()
                .collection("problems").document(id!!)


            query.get().addOnSuccessListener {
                model = it.toObject(ProblemModel::class.java)!!

                b.tvEventTitle.text = model.title
                b.tvProblemDesc.text = model.description
                b.tvDate.text = model.dateAt
                b.tvTime.text = model.timeAt
                b.tvProlemLocation.text = model.locationAt
                b.tvPostedBy.text = model.postedBy


                builder = StringBuilder()

                model.levelOfEmergency.forEach {

                    builder.append(it)
                        .append(", ")

                }


                b.tvLevelOfEmer.text = builder.toString()

                //initAdapter()

            }.addOnFailureListener {

                Log.v("Details", it.message.toString())
            }

            query = FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(model.postedBy)

            query.get().addOnSuccessListener {
                contact = it.get("PhoneNo").toString()
            }

        }

        b.btnDialer.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$contact")
            startActivity(intent)
        }
    }


}