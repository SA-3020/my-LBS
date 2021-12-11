package com.example.notify_around

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.businessUser.activities.PostAdd
import com.example.notify_around.databinding.ActivityAddNewItemBinding

class AddNewItem : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, UserDashboard::class.java)); finish()
        }
        if (UserManager.user?.businessUser == null) {
            binding.adCard.visibility = View.GONE

        }
    }

    fun postNewAd(v: View) {
        goTo(PostAdd())
    }

    fun postNewEvent(v: View) {
        goTo(PostEventActivity())
    }

    fun postNewProblem(v: View) {
        goTo(PostProblemActivity())
    }

    fun postNewSkill(v: View) {
        goTo(PostSkillActivity())
    }

    private fun goTo(activity: AppCompatActivity) {
        startActivity(Intent(applicationContext, activity::class.java))
    }

}