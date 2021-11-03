package com.example.notify_around.DrawerActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.notify_around.R
import com.example.notify_around.databinding.ActivityMyInterestsBinding

class MyInterestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyInterestsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyInterestsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.floatingActionButton.setOnClickListener {

        }

    }

    private fun followBtn(v: android.view.View){
        Toast.makeText(applicationContext, "Successfully Followed", Toast.LENGTH_SHORT).show()
    }


}