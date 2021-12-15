package com.example.notify_around

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.notify_around.Adapters.ViewPagerAdapter


import com.example.notify_around.databinding.ActivityEventDetailsBinding
import com.example.notify_around.models.EventModel
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventDetailsBinding

    private lateinit var model: EventModel
    private lateinit var builder: StringBuilder
    private var currentPage = 0
    private lateinit var contact: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get id of the event from the calling activity extra
        val id = intent.extras?.getString("eventId")
        Log.v("Details", id.toString())

        //get the event using the id
        val query = FirebaseFirestore.getInstance()
            .collection("events").document(id!!)

        query.get().addOnSuccessListener {
            model = it.toObject(EventModel::class.java)!!

            val userid = model.postedBy

            //get the user who posted the event
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(userid).get().addOnSuccessListener {
                    contact = it.get("phoneNo").toString()
                    binding.tvEventTitle.text = model.title
                    binding.tvEventLocation.text = model.locationAt
                    binding.tvDate.text = model.dateAt
                    binding.tvTime.text = model.timeAt
                    binding.tvDescription.text = model.desc
                    binding.tvPostedBy.text = "${it.get("firstName").toString()} ${
                        it.get("lastName").toString()
                    }"//model.postedBy


                    builder = StringBuilder()

                    model.interests.forEach {

                        builder.append(it)
                            .append(", ")

                    }

                    if (model.images.isNotEmpty()) {
                        Glide.with(this).load(model.images[0]).into(binding.imgView)
                    }

                    binding.tvInterests.text = builder.toString()
                }
            initAdapter()

        }.addOnFailureListener {

            Log.v("Details",it.message.toString())
        }

        binding.backBtn.setOnClickListener {

            if(currentPage>=1){currentPage--
                binding.viewPager.currentItem = currentPage
            }
        }

        binding.nextBtn.setOnClickListener {

            if (currentPage < model.images.size - 1) {
                currentPage++
                binding.viewPager.currentItem = currentPage
            }
        }
        binding.btnDialer.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$contact")
            startActivity(intent)
        }

    }


    private fun initAdapter(){

        val adapter= ViewPagerAdapter(this,model.images)
        binding.viewPager.adapter=adapter
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)

    }


    var pageChangeCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            currentPage=position

            if (position == model.images.size - 1) {
                binding.nextBtn.setVisibility(View.INVISIBLE)
            } else {
                binding.nextBtn.setVisibility(View.VISIBLE)
            }

            if (position == 0) {
                binding.backBtn.setVisibility(View.INVISIBLE)
            } else {
                binding.backBtn.setVisibility(View.VISIBLE)
            }
        }
    }


}