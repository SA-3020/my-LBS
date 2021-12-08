package com.example.notify_around

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.notify_around.models.ProblemModel
import com.example.notify_around.databinding.ActivityProblemDetailsBinding
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

        contact = "+921234567"

        var model = ProblemModel()
        Thread {
            val query = FirebaseFirestore.getInstance()
                .collection("problems").document(id!!)


            query.get().addOnSuccessListener {
                model = it.toObject(ProblemModel::class.java)!!
                Log.v(TAG, model.toString())

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

                var userid = model.postedBy.replace("/", "")
                Log.d(TAG, "phoeee $userid")
                /*val query2 = FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(userid)

                query2.get().addOnSuccessListener {
                    contact = it.get("PhoneNo").toString()
                }*/
//                        initAdapter()

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