package com.example.notify_around.drawerActivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.Adapters.CatAdapter
import com.example.notify_around.MultiselectDialog
import com.example.notify_around.UserDashboard
import com.example.notify_around.databinding.ActivityMyInterestsBinding
import com.example.notify_around.models.InterestsModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MyInterestsActivity : AppCompatActivity() {
    private lateinit var b: ActivityMyInterestsBinding
    private var mIAdapter: CatAdapter? = null//InterestAdapter? = null
    private lateinit var followedInterests: MutableList<String>
    private lateinit var otherInterestsArr: MutableList<String>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMyInterestsBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, UserDashboard::class.java)); finish();
        }

        /*
        * Get the interests array of current user
        * Get all interests from interests collection where id = id from users interests array. This will give the interest ids that the user follows
        * Add title of the interests into arraylist named other interests
        * */
        firestore = FirebaseFirestore.getInstance()
        followedInterests =
            intent.getSerializableExtra("currentuser") as MutableList<String>

        Thread {
            otherInterestsArr = ArrayList()
            // for interests that a user is noot following
            firestore
                .collection("interests").whereNotIn("Title", followedInterests)
                //.whereNotIn(FieldPath.documentId(), followedInterests)
                .get()
                .addOnSuccessListener {
                    for (doc in it)
                    //Log.d(TAG, "folll ${doc.get("Title")}")
                        otherInterestsArr.add(doc.get("Title").toString())
                    runOnUiThread {
                        Toast.makeText(applicationContext, it.size().toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
                .addOnFailureListener {
                }
        }.start()

        b.btnFollowNew.setOnClickListener {
            MultiselectDialog(otherInterestsArr, null, "Follow").show(
                supportFragmentManager,
                "interestDialog"
            )
        }

    }

    override fun onStart() {
        super.onStart()
        if (followedInterests.isNotEmpty()) {
            //for interests that a user is following
            val query = firestore
                .collection("interests").whereIn("Title", followedInterests)
                //.whereIn(FieldPath.documentId(), followedInterests)
                .orderBy("Title")//? = null

            val options = FirestoreRecyclerOptions.Builder<InterestsModel>()
                .setQuery(query, InterestsModel::class.java)
                .build()//? = null
            b.progressBar.visibility = View.INVISIBLE
            mIAdapter = CatAdapter(followedInterests)//InterestAdapter(options)
            b.myinterestsRecview.layoutManager = LinearLayoutManager(baseContext)
            b.myinterestsRecview.adapter = mIAdapter
            //mIAdapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        //mIAdapter?.stopListening()
    }

    //fun followNewEvent(view: android.view.View){ }

}