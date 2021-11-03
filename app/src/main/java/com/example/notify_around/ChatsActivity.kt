package com.example.notify_around

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notify_around.databinding.ActivityChatsBinding

class ChatsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsBinding.inflate(layoutInflater)
        val view = binding.root.apply {
            setContentView(this)
        }
    }
}