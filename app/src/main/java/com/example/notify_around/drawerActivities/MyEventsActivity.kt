package com.example.notify_around.drawerActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.EventDetailsActivity
import com.example.notify_around.UserDashboard
import com.example.notify_around.Adapters.MyEventAdapter
import com.example.notify_around.databinding.ActivityMyEventsBinding
import com.example.notify_around.models.EventModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MyEventsActivity : AppCompatActivity(), MyEventAdapter.OnEventItemClickListener {
    private lateinit var b: ActivityMyEventsBinding
    private lateinit var adapterMy: MyEventAdapter
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMyEventsBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, UserDashboard::class.java)); finish();
        }
        uid = FirebaseAuth.getInstance().currentUser!!.uid

        //get only those events whose posted by 'user id' == current user id
        val query = FirebaseFirestore.getInstance()
            .collection("events")
            .whereEqualTo("postedBy", uid)
            .orderBy("dateAt")

        val options: FirestoreRecyclerOptions<EventModel?>? =
            FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel::class.java).build()

        b.progressBar.visibility = View.INVISIBLE

        adapterMy = MyEventAdapter(options!!, this)
        adapterMy.setOnEventItemClickListener(this)
        b.myeventsRecview.layoutManager = LinearLayoutManager(this)
        b.myeventsRecview.adapter = adapterMy


    }

    override fun onEventItemClick(ds: DocumentSnapshot?) {
        val model = ds?.toObject(EventModel::class.java)

        val intent = Intent(this, EventDetailsActivity::class.java)
        intent.putExtra("eventId", model?.id)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        adapterMy.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapterMy.stopListening()
    }
}