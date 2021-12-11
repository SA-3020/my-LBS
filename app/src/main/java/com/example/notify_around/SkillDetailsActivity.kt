package com.example.notify_around

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.databinding.ActivitySkillDetailsBinding
import com.example.notify_around.models.SkillModel
import com.google.firebase.firestore.FirebaseFirestore

class SkillDetailsActivity : AppCompatActivity() {

    private lateinit var b: ActivitySkillDetailsBinding
    private lateinit var builder: StringBuilder
    private lateinit var contact: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySkillDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)

        val id = intent.extras?.getString("skillId")
        Log.v("Details", id.toString())


        Thread {
            val query = FirebaseFirestore.getInstance()
                .collection("skills").document(id!!)


            query.get().addOnSuccessListener {
                val model = it.toObject(SkillModel::class.java)!!


                val userid = model.postedBy.replace("/", "")
                Log.d(ContentValues.TAG, "phoeee $userid")
                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(userid).get().addOnSuccessListener {
                        contact = it.get("phoneNo").toString()
                        builder = StringBuilder()

                        model.interests.forEach {

                            builder.append(it)
                                .append(", ")

                        }
                        runOnUiThread {
                            b.tvEventTitle.text = model.title
                            b.tvSkillDesc.text = model.description
                            b.tvSkillInt.text = builder.toString()
                            b.tvDate.text = model.postedOn.toString()
                            b.tvSkillLocation.text = model.locationAt
                            b.tvPostedBy.text =
                                "${it.get("firstName").toString()} ${it.get("lastName").toString()}"

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