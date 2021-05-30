package com.example.notify_around

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_event.*

class CreateEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        btn_cancel.setOnClickListener {
            startActivity(Intent(applicationContext, UserDashboard::class.java ))
            finish()

        }

        btn_save.setOnClickListener {
            Toast.makeText(applicationContext, "Event Created", Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, UserDashboard::class.java ))
            finish()
        }
    }
}