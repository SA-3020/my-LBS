package com.example.notify_around.businessUser.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.notify_around.drawerActivities.MyInterestsActivity
import com.example.notify_around.drawerActivities.UserProfile
import com.example.notify_around.Events
import com.example.notify_around.fragments.HomeFragment
import com.example.notify_around.MainActivity
import com.example.notify_around.R
import com.example.notify_around.databinding.ActivityBusinessUserDashboardBinding
import com.example.notify_around.drawerActivities.MyAdsActivity
import com.google.firebase.auth.FirebaseAuth

class BUserDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityBusinessUserDashboardBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessUserDashboardBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //Setting the toolbar and drawer
        binding.toolbar.setNavigationIcon(R.drawable.ic_round_menu_open_24)

        /* This method sets the toolbar as the app bar for the activity. */
        setSupportActionBar(binding.toolbar)

        /* Make sure that you have imported your R-class and not android.R */
        toggle = ActionBarDrawerToggle(
            this,
            binding.budrawer,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        binding.budrawer.addDrawerListener(toggle)
        toggle.syncState()
        //to override the default icon colors with the customised colors
        binding.naview.itemIconTintList = null

        var header = binding.naview.getHeaderView(0)
        //for opening default fragment on user dashboard

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commit()

        /* Called when an item in the navigation menu is selected.*/
        binding.naview.setNavigationItemSelectedListener { menuItem ->
            Log.d("TAG", "Inside setNavigationItemSelected")

            when (menuItem.itemId) {
                R.id.menu_myprofile -> {
                    startActivity(Intent(applicationContext, UserProfile::class.java))
                }
                R.id.menu_myinterests -> {
                    startActivity(Intent(applicationContext, MyInterestsActivity::class.java))
                    Toast.makeText(
                        applicationContext,
                        "My Interests Panel is Open",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                R.id.menu_myevents -> {
                    startActivity(Intent(applicationContext, Events::class.java))
                }
                R.id.menu_chats -> {

                }
                R.id.menu_myads -> {
                    startActivity(Intent(applicationContext, MyAdsActivity::class.java))

                }
                R.id.menu_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }
            binding.budrawer.closeDrawer(GravityCompat.START)
            true
        }
    }
}