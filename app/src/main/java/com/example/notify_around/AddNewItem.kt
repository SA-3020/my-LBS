package com.example.notify_around

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.Utils.MethodsUtils
import com.example.notify_around.databinding.ActivityAddNewItemBinding

class AddNewItem : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    fun postNewAd(v: View) {
    }

    fun postNewEvent(v: View) {
        MethodsUtils.goToActivity(this, PostEventActivity()); finish()
    }

    fun postNewProblem(v: View) {
        MethodsUtils.goToActivity(this, PostProblemActivity()); finish()
    }

    fun postNewSkill(v: View) {
    }

/*
    private fun goTo(activity: AppCompatActivity) {
        startActivity(Intent(applicationContext, activity::class.java))
        finish()
    }
*/

}