package com.example.notify_around

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notify_around.databinding.ActivityEventsBinding

class Events : AppCompatActivity() {

    private lateinit var binding: ActivityEventsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnAddEvent.setOnClickListener {
            startActivity(Intent(applicationContext, Events::class.java))
            finish()
        }

    }
}