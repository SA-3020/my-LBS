package com.example.notify_around

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_events.*

class Events : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        btn_AddEvent.setOnClickListener {
            startActivity(Intent(applicationContext, CreateEvent::class.java))
            finish()
        }

    }
}