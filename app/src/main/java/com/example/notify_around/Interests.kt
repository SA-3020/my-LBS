package com.example.notify_around

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony.Mms.Part.TEXT
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_interests.*
import org.xmlpull.v1.XmlPullParser.TEXT

class Interests : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests)

        floatingActionButton.setOnClickListener {

        }

    }

    private fun followBtn(v: android.view.View){
        Toast.makeText(applicationContext, "Successfully Followed", Toast.LENGTH_SHORT).show()
    }
}