package com.example.notify_around

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notify_around.databinding.ActivityPostEventBinding

class PostProblemActivity : AppCompatActivity() {
    private lateinit var b: ActivityPostEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPostEventBinding.inflate(layoutInflater)
        setContentView(b.root)
    }
}