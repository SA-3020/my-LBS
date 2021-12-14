package com.example.notify_around

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.Utils.MethodsUtils
import com.example.notify_around.databinding.ActivityPostSkillBinding
import com.example.notify_around.models.GeneralUser
import com.example.notify_around.models.SkillModel
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
import kotlin.collections.ArrayList

class PostSkillActivity : AppCompatActivity() {
    private lateinit var b: ActivityPostSkillBinding
    private var selectedLatLng: GeoPoint? = null
    private var db = FirebaseFirestore.getInstance()
    lateinit var interestsArray: MutableList<String>
    var usersList: MutableList<GeneralUser> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPostSkillBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.tvInterests.inputType = 0; b.etLocation.inputType = 0

        b.toolbar.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, AddNewItem::class.java)); finish()
        }

        Thread {
            interestsArray = ArrayList()
            db.collection("interests")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
//                        Log.d("dialoglog","inside for")
//                        Log.d(TAG, "${document.get("Title")} => ${document.data}")
                        interestsArray.add(document.get("Title").toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.start()

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

    /*fun getDateFromUser(view: android.view.View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this, R.style.my_dialog_theme,
            { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                b.etDate.setText("$dayOfMonth/ $monthOfYear/ $year")

            }, year, month, day
        )

        dpd.show()
    }*/

    public fun postNewSkill(v: View) {
        if (b.etTitle.text.isBlank() || b.etDescription.text.isBlank() || b.tvInterests.text.isBlank() || b.etLocation.text.isBlank())
            MethodsUtils.makeShortToast(this, "All fields are required")
        if (!b.etTitle.text.isBlank() && !b.etDescription.text.isBlank() && !b.etLocation.text.isBlank()) {
            Thread {
                var skillid = db.collection("skills").document().id
                val newSkillRef = db.collection("skills").document(skillid)
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                val event = SkillModel(
                    skillid,
                    b.etTitle.text.toString(),
                    b.etDescription.text.toString(),
                    selectedLatLng,
                    MultiselectDialog.selectedInterestsArray,
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
                    uid!!,
                    b.etLocation.text.toString(),
                )
                newSkillRef.set(event)
                    .addOnSuccessListener {
                        runOnUiThread {
                            MethodsUtils.makeShortToast(this, "Skill Uploaded successfully")
                        }


                        for(user in usersList){

                            if(userHaveInterest(user)){

                                var distance=getDistanceBetweenTwoPoints(selectedLatLng?.latitude,selectedLatLng?.longitude,user.location?.latitude,user.location?.longitude)

                                distance /= 1000

                                if(distance>0.0&&distance<=10){
                                    if(UserManager.user?.tokenId?.equals(user.tokenId) != true){
                                        sendNotification(user.tokenId,skillid) }
                                }

                            }

                        }
                    }
            }.start()

            startActivity(Intent(this, UserDashboard::class.java))
            finish()
        }
    }

    fun showDialog(view: android.view.View) {
        MultiselectDialog(interestsArray, b.tvInterests, "OK").show(
            supportFragmentManager,
            "interestDialog"
        )
    }

    private fun sendNotification(tokenId:String,skillId:String) {


        val message="New skill added"

        val apiClient =
            ApiClient.getClient("https://fcm.googleapis.com/")?.create(ApiInterface::class.java)
        val to: String = tokenId
        val data = Notification(
            UserManager.user!!.FirstName,
            message, skillId,
            "Skill"
        )
        val notification = Message(to, data)
        val call: Call<Message?>? = apiClient?.sendMessage("key=${ApiClient.FIRE_BASE_SERVER_KEY}", notification)

        try {
            call?.enqueue(object : Callback<Message?> {
                override fun onResponse(call: Call<Message?>?, response: retrofit2.Response<Message?>?) {}
                override fun onFailure(call: Call<Message?>?, t: Throwable?) {}
            })
        }catch (e:Exception){
            Log.e("PostAdError",e.message.toString())
        }


    }

    private fun userHaveInterest(user: GeneralUser):Boolean{

        var have=false

        for(interest in interestsArray){

            if(user.interests.contains(interest)){
                have=true
                return have
            }


        }

        return have
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