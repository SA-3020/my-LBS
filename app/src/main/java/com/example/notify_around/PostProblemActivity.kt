package com.example.notify_around

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.notify_around.models.ProblemModel
import com.example.notify_around.Utils.MethodsUtils
import com.example.notify_around.databinding.ActivityPostProblemBinding
import com.example.notify_around.models.GeneralUser
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import firebaseNotifications.Message
import firebaseNotifications.Notification
import firebaseNotifications.retrofit.ApiClient
import firebaseNotifications.retrofit.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*

class PostProblemActivity : AppCompatActivity() {
    private lateinit var b: ActivityPostProblemBinding
    private lateinit var db: FirebaseFirestore
    private var selectedLatLng: GeoPoint? = null
    var usersList: MutableList<GeneralUser> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPostProblemBinding.inflate(layoutInflater)
        setContentView(b.root)
        db = FirebaseFirestore.getInstance()

        b.etDate.inputType = 0; b.etTime.inputType = 0; b.tvEmergencyLevel.inputType =
            0; b.etLocation.inputType = 0

        b.toolbar.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, AddNewItem::class.java)); finish()
        }

        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {

                        b.etLocation.setText(intent.getStringExtra("location"))
                        val lat = intent.getStringExtra("lat")
                        val lng = intent.getStringExtra("lng")
                        val latLng = GeoPoint(lat!!.toDouble(), lng!!.toDouble())
                        selectedLatLng = latLng
                    }

                }
            }

        b.etLocation.setOnClickListener {
            startForResult.launch(Intent(this, MapActivity::class.java))
        }
    }

    fun showDialog(view: android.view.View) {
        MultiselectDialog(
            resources.getStringArray(R.array.emergencyLevelArr)
                .toMutableList() as MutableList<String>,
            b.tvEmergencyLevel,
            "OK"
        ).show(
            supportFragmentManager,
            "interestDialog"
        )

        getUsers()
    }

    private fun getUsers(){

        db.collection("users").get().addOnSuccessListener {

            for(user in it){

                val userData = user.toObject(GeneralUser::class.java)

                usersList.add(userData)
            }
        }

    }

    fun getDateFromUser(view: android.view.View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this@PostProblemActivity, R.style.my_dialog_theme,
            { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                b.etDate.setText("$dayOfMonth/ $monthOfYear/ $year")
            }, year, month, day
        )
        dpd.show()
    }

    fun getTimeFromUser(view: android.view.View) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            b.etTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        TimePickerDialog(
            this,
            R.style.my_dialog_theme,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    fun postProblem(view: android.view.View) {
        if (b.etTitle.text.isBlank())
            MethodsUtils.makeShortToast(this, "Please enter event title")
        if (b.etDescription.text.isBlank())
            MethodsUtils.makeShortToast(this, "Please enter event description")
        if (b.etDate.text.isBlank() || b.etTime.text.isBlank())
            MethodsUtils.makeShortToast(this, "Problem date and time are required")
        if (b.etLocation.text.isBlank())
            MethodsUtils.makeShortToast(this, "Please select a location of the problem")
        if (b.etTitle.text.isBlank() && b.etDescription.text.isBlank() && b.etDate.text.isBlank() && b.etTime.text.isBlank() && b.etLocation.text.isBlank())
            MethodsUtils.makeShortToast(this, "All fields are required")
        if (b.etTitle.text.isNotBlank() && b.etDescription.text.isNotBlank() && b.etDate.text.isNotBlank() && b.etTime.text.isNotBlank()/* && !b.etLocation.text.isBlank()*/) {
            Thread {
                val newproblemRef = db.collection("problems").document()
                var problemid = newproblemRef.id

                val problem = ProblemModel(
                    problemid,
                    b.etTitle.text.toString(),
                    b.etDescription.text.toString(),
                    b.tvEmergencyLevel.text.toString(),
                    selectedLatLng,
                    Timestamp.now(),
                    FirebaseAuth.getInstance().currentUser?.uid.toString(),
                    b.etDate.text.toString(),
                    b.etTime.text.toString(),
                    b.etLocation.text.toString()
                )
                newproblemRef.set(problem)
                    .addOnSuccessListener {
                        runOnUiThread {
                            MethodsUtils.makeShortToast(this, "Event uploaded")
                        }

                        for(user in usersList){

                            var distance=getDistanceBetweenTwoPoints(selectedLatLng?.latitude,selectedLatLng?.longitude,user.location?.latitude,user.location?.longitude)

                            distance /= 1000

                            if(distance>0.0&&distance<=10){
                                if(UserManager.user?.tokenId?.equals(user.tokenId) == true){
                                    sendNotification(user.tokenId) }
                            }

                        }
                    }
            }.start()

            startActivity(Intent(this, UserDashboard::class.java))
            finish()
        }
    }




    private fun getDistanceBetweenTwoPoints(
        lat1: Double?,
        lon1: Double?,
        lat2: Double?,
        lon2: Double?
    ): Float {
        val distance = FloatArray(2)
        if (lat1 != null) {
            if (lon1 != null) {
                if (lat2 != null) {
                    if (lon2 != null) {
                        Location.distanceBetween(
                            lat1, lon1,
                            lat2, lon2, distance
                        )
                    }
                }
            }
        }
        return distance[0]
    }
}