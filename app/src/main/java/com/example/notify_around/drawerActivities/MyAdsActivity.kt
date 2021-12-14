package com.example.notify_around.drawerActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify_around.Adapters.MyAdsAdapter
import com.example.notify_around.EventDetailsActivity
import com.example.notify_around.UserDashboard
import com.example.notify_around.Adapters.MyEventAdapter
import com.example.notify_around.businessUser.activities.AdDetailsActivity
import com.example.notify_around.databinding.ActivityMyEventsBinding
import com.example.notify_around.models.AdModel
import com.example.notify_around.models.EventModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MyAdsActivity : AppCompatActivity(), MyAdsAdapter.OnAdItemClickListener {
    private lateinit var b: ActivityMyEventsBinding
    private lateinit var adapterMy: MyAdsAdapter
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMyEventsBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, UserDashboard::class.java)); finish();
        }
        uid = FirebaseAuth.getInstance().currentUser!!.uid

        b.toolbar.setTitle("My Ads")
        //get only those events whose posted by 'user id' == current user id
        val query = FirebaseFirestore.getInstance()
            .collection("Business Ad")
            .whereEqualTo("uid", uid)
            .orderBy("postedOn")

        val options: FirestoreRecyclerOptions<AdModel?>? =
            FirestoreRecyclerOptions.Builder<AdModel>()
                .setQuery(query, AdModel::class.java).build()

        b.progressBar.visibility = View.INVISIBLE

        adapterMy = MyAdsAdapter(options!!, this)
        adapterMy.setOnAdItemClickListener(this)
        b.myeventsRecview.layoutManager = LinearLayoutManager(this)
        b.myeventsRecview.adapter = adapterMy


    }

    override fun onAdItemClick(ds: DocumentSnapshot?) {
        val model = ds?.toObject(AdModel::class.java)

        val intent = Intent(this, AdDetailsActivity::class.java)
        intent.putExtra("adId", model?.id)
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