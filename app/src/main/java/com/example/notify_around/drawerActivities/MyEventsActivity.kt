package com.example.notify_around.drawerActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.notify_around.databinding.ActivityMyEventsBinding

class MyEventsActivity : AppCompatActivity() {
    private lateinit var b: ActivityMyEventsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMyEventsBinding.inflate(layoutInflater)
        setContentView(b.root)
    }
}