package com.example.notify_around.drawerActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.databinding.ActivityMyEventsBinding

class MyEventsActivity : AppCompatActivity() {
    private lateinit var b: ActivityMyEventsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMyEventsBinding.inflate(layoutInflater)
        setContentView(b.root)

    }
}