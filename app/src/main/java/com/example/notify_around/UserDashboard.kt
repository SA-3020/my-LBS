package com.example.notify_around

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.notify_around.businessUser.activities.BUserDashboard
import com.example.notify_around.businessUser.activities.BUserDetailsActivity
import com.example.notify_around.databinding.ActivityUserDashboardBinding
import com.example.notify_around.drawerActivities.MyAdsActivity
import com.example.notify_around.drawerActivities.MyEventsActivity
import com.example.notify_around.drawerActivities.MyInterestsActivity
import com.example.notify_around.drawerActivities.UserProfile
import com.example.notify_around.fragments.*
import com.example.notify_around.models.BusinessUser
import com.example.notify_around.models.GeneralUser
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.messaging.FirebaseMessaging
import firebaseNotifications.MyFirebaseMessagingService
import kotlinx.coroutines.*
import java.io.Serializable


class UserDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityUserDashboardBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback:LocationCallback

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var docRef: DocumentReference
    private var currentUser = GeneralUser()
    private var businessDetails:BusinessUser?=null
    //private var myInterests = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        binding.tvCatEvents.setTextColor(resources.getColor(R.color.black, null))
        binding.tvCatEvents.typeface = Typeface.DEFAULT_BOLD
        //for opening default fragment on user dashboard
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, EventsFragment())
            .commit()

        initializeFirebase()

        var header = binding.naview.getHeaderView(0)

        Thread {
            docRef.get()
                .addOnSuccessListener {
                    Log.d(TAG, "Hello Hello $docRef")
                    currentUser = it.toObject(GeneralUser::class.java)!!
                    businessDetails=currentUser.businessUser
                    UserManager.user=currentUser

                    runOnUiThread {
                        "${currentUser.FirstName} ${currentUser.LastName}".also {
                            header.findViewById<TextView>(
                                R.id.tv_username
                            ).text = it
                            updateMenu()

                        }
                        header.findViewById<TextView>(R.id.tv_location).text = currentUser.PhoneNo
                    }
                }
        }.start()
        setContentView(view)

        /* This method sets the toolbar as the app bar for the activity. */
        setSupportActionBar(binding.toolbar)

        /* Make sure that you have imported your R-class and not android.R */
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawer,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()
        //to override the default icon colors with the customised colors
        binding.naview.itemIconTintList = null


        /* Called when an item in the navigation menu is selected.*/
        binding.naview.setNavigationItemSelectedListener { menuItem ->
            Log.d("TAG", "Inside setNavigationItemSelected")

            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(applicationContext, UserProfile::class.java))
                }
                R.id.menu_myinterests -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            MyInterestsActivity::class.java
                        ).putExtra("currentuser", currentUser.interests as Serializable)
                    )
                    Toast.makeText(
                        applicationContext,
                        "My Interests Panel is Open",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                R.id.menu_myevents -> {
                    startActivity(Intent(applicationContext, MyEventsActivity::class.java))
                    showMessage("My Events panel is open")
                }
                R.id.menu_myads -> {
                    startActivity(Intent(applicationContext, MyAdsActivity::class.java))
                    showMessage("My Ads panel is open")
                }
                R.id.menu_chats -> {

                }
                R.id.menu_gobusiness -> {
                    showMessage("Business user add Details")
                    if(businessDetails!=null){
                        startActivity(Intent(applicationContext, BUserDashboard::class.java))

                    }
                    else{
                    startActivity(Intent(applicationContext, BUserDetailsActivity::class.java))}
                }
                R.id.menu_logout -> {
                    showMessage("You are logged out")
                    auth.signOut()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }
            binding.drawer.closeDrawer(GravityCompat.START)
            true
        }


        askPermissionLocation()
        getCurrentLocation()



        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    UserManager.user?.tokenId=task.result.toString()
                    MyFirebaseMessagingService.newToken(task.result.toString())
                }
            }.addOnFailureListener { e -> Log.v("TokenError", e.message!!) }


    }

    override fun onResume() {
        super.onResume()
        updateMenu()
    }

    private fun updateMenu(){

        val menu: Menu = binding.naview.menu

        val itemBusiness: MenuItem = menu.findItem(R.id.menu_gobusiness)
        val itemMyAds: MenuItem = menu.findItem(R.id.menu_myads)

        if( UserManager.user?.businessUser==null){
            itemBusiness.isVisible=true
            itemMyAds.isVisible=false
        }
        else
        {

            itemBusiness.isVisible=false
            itemMyAds.isVisible=true
        }


    }



    fun addNewBtnOnClick(view: android.view.View) {
        startActivity(Intent(applicationContext, AddNewItem::class.java))
    }

    suspend fun getCurrentUser(uid: String): GeneralUser {
        delay(500L)
        Log.d(TAG, "Inside suspend function")
        docRef.get()
            .addOnSuccessListener {
                Log.d(TAG, "Hello Hello $docRef")
                currentUser = it.toObject(GeneralUser::class.java)!!

                //Log.d(TAG, "Hello Hello ${currentUser}")
                var header = binding.naview.getHeaderView(0)
                //Log.d( TAG, "Hello Here ${currentUser}")

                "${currentUser.FirstName} ${currentUser.LastName}".also {
                    header.findViewById<TextView>(
                        R.id.tv_username
                    ).text = it
                }
                header.findViewById<TextView>(R.id.tv_location).text = currentUser.PhoneNo
            }

        return currentUser
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        docRef = firestore.collection("users").document(auth.currentUser?.uid.toString())
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    fun openHome(view: android.view.View) {
        binding.tvCatAds.typeface = Typeface.DEFAULT
        binding.tvCatAds.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatEvents.typeface = Typeface.DEFAULT
        binding.tvCatEvents.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatSkills.typeface = Typeface.DEFAULT
        binding.tvCatSkills.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatProblems.typeface = Typeface.DEFAULT
        binding.tvCatProblems.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commit()
    }

    fun openAds(view: android.view.View) {
        binding.tvCatAds.typeface = Typeface.DEFAULT_BOLD
        binding.tvCatAds.setTextColor(resources.getColor(R.color.black, null))
        binding.tvCatProblems.typeface = Typeface.DEFAULT
        binding.tvCatProblems.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatEvents.typeface = Typeface.DEFAULT
        binding.tvCatEvents.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatSkills.typeface = Typeface.DEFAULT
        binding.tvCatSkills.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, AdsFragment())
            .commit()
    }

    fun openEvents(view: android.view.View) {
        binding.tvCatEvents.typeface = Typeface.DEFAULT_BOLD
        binding.tvCatEvents.setTextColor(resources.getColor(R.color.black, null))
        binding.tvCatAds.typeface = Typeface.DEFAULT
        binding.tvCatAds.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatSkills.typeface = Typeface.DEFAULT
        binding.tvCatSkills.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatProblems.typeface = Typeface.DEFAULT
        binding.tvCatProblems.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, EventsFragment())
            .commit()
    }

    fun openSkills(view: android.view.View) {
        binding.tvCatSkills.typeface = Typeface.DEFAULT_BOLD
        binding.tvCatSkills.setTextColor(resources.getColor(R.color.black, null))
        binding.tvCatAds.typeface = Typeface.DEFAULT
        binding.tvCatAds.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatEvents.typeface = Typeface.DEFAULT
        binding.tvCatEvents.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatProblems.typeface = Typeface.DEFAULT
        binding.tvCatProblems.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, SkillsFragment())
            .commit()
    }

    fun openProblems(view: android.view.View) {
        binding.tvCatProblems.typeface = Typeface.DEFAULT_BOLD
        binding.tvCatProblems.setTextColor(resources.getColor(R.color.black, null))
        binding.tvCatAds.typeface = Typeface.DEFAULT
        binding.tvCatAds.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatEvents.typeface = Typeface.DEFAULT
        binding.tvCatEvents.setTextColor(resources.getColor(R.color.defaultContentColor, null))
        binding.tvCatSkills.typeface = Typeface.DEFAULT
        binding.tvCatSkills.setTextColor(resources.getColor(R.color.defaultContentColor, null))

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, ProblemsFragment())
            .commit()
    }



    private fun askPermissionLocation(){
        askPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            getCurrentLocation()
        }.onDeclined { e ->
            if (e.hasDenied()) {
                e.denied.forEach{
                }
                AlertDialog.Builder(this)
                    .setMessage("Please enable Location")
                    .setPositiveButton("Yes") { _, _ ->
                        e.askAgain()
                    }
                    .setNegativeButton("no") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            if (e.hasForeverDenied()) {
                e.foreverDenied.forEach {

                }
                e.goToSettings();
            } }

    }



    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        val db = FirebaseFirestore.getInstance()
        val   docRef = db.collection("users").document(auth.currentUser?.uid!!)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@UserDashboard)

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 20 * 1000
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.v("NewLocation","result")

                for (location in locationResult.locations) {
                    if (location != null) {
                        val geoPoints= GeoPoint(location.latitude,location.longitude)
                        UserManager.user?.location= geoPoints

                        val user: MutableMap<String, Any> = HashMap()
                        user["location"]=geoPoints


                        docRef.update(user).addOnSuccessListener {
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                            Log.v("NewLocation","updated")
                        }
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())


    }

}