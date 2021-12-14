package com.example.notify_around

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.notify_around.models.EventModel
import com.example.notify_around.databinding.ActivityPostEventBinding
import com.example.notify_around.models.GeneralUser
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import firebaseNotifications.Message
import firebaseNotifications.Notification
import firebaseNotifications.retrofit.ApiClient
import firebaseNotifications.retrofit.ApiClient.FIRE_BASE_SERVER_KEY
import firebaseNotifications.retrofit.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostEventBinding
    private lateinit var db: FirebaseFirestore
    lateinit var interestsArray: MutableList<String>
    private var selectedLatLng:GeoPoint?=null
    private lateinit var imageSwitcher: ImageSwitcher
    private lateinit var img_view: ImageView
    private lateinit var root_layout: RelativeLayout
    private val PERMISSION_CODE: Int= 1000
    private val IMAGE_CAPTURE_CODE: Int= 1001
    private val PICK_IMAGES_CODE=0
    private var mUri: Uri?=null
    private var images:MutableList<Uri> = ArrayList()
    var imagesList: MutableList<String> = ArrayList()
    var usersList: MutableList<GeneralUser> = ArrayList()
    lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etDate.inputType = 0; binding.etTime.inputType = 0; binding.tvInterests.inputType =
            0
        binding.etLocation.inputType = 0
        db = FirebaseFirestore.getInstance()

        binding.toolbar.setNavigationOnClickListener {
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
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }.start()


        root_layout=findViewById(R.id.rellay1)

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if(intent!=null){

                    binding.etLocation.setText(intent.getStringExtra("location"))
                    val lat=intent.getStringExtra("lat")
                    val lng=intent.getStringExtra("lng")
                    val latLng= GeoPoint(lat!!.toDouble(), lng!!.toDouble())
                    selectedLatLng=latLng


                }

            }
        }

        binding.etLocation.setOnClickListener {

            startForResult.launch(Intent(this,MapActivity::class.java))
        }



        imageSwitcher=findViewById(R.id.image_switcher)
        img_view=findViewById(R.id.img_view)
        imageSwitcher?.setFactory { ImageView(applicationContext) }
        // Set a click listener for button widget
        img_view.setOnClickListener{
            // Initialize a new layout inflater instancemageSwitcher.setFactory{ImageView(applicationContext)}
            val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.activity_pop_up,null)

            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            // Set an elevation for the popup window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 10.0F
            }


            // If API level 23 or higher then execute the code
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.TOP
                popupWindow.enterTransition = slideIn

                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.RIGHT
                popupWindow.exitTransition = slideOut

            }

            // Get the widgets reference from custom view
            val tv_camera=view.findViewById<TextView>(R.id.tv_camera)
            val tv_gallery=view.findViewById<TextView>(R.id.tv_gallery)

            tv_camera.setOnClickListener{
                Toast.makeText(this@PostEventActivity,"Capture images...", Toast.LENGTH_SHORT).show()
                openCamera()
                popupWindow.dismiss()
            }
            tv_gallery.setOnClickListener{
                Toast.makeText(this@PostEventActivity,"Select images from gallery...", Toast.LENGTH_SHORT).show()
                pickImagesIntent()
                popupWindow.dismiss()
            }
            // Finally, show the popup window on app
            TransitionManager.beginDelayedTransition(root_layout)
            popupWindow.showAtLocation(
                root_layout, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )
        }

        getUsers()
        getUser()

    }

    fun getUser() {

        val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
        docRef
            .get()
            .addOnSuccessListener {
                val currentUser = it.toObject(GeneralUser::class.java)!!
                userName=currentUser.FirstName+" "+currentUser.LastName

            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Data not found", Toast.LENGTH_SHORT).show()
            }
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
            this@PostEventActivity, R.style.my_dialog_theme,
            { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                binding.etDate.setText("$dayOfMonth/ $monthOfYear/ $year")

            }, year, month, day
        )

        dpd.show()
    }

    fun getTimeFromUser(view: android.view.View) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            binding.etTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
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

    fun postNewEvent(v: View) {

        Toast.makeText(this, "Please wait", Toast.LENGTH_LONG).show()


        if(mUri!=null) {

            Log.v("size",images.size.toString())

            for((index,item) in images.withIndex()){

                val storageReference: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("image" + System.currentTimeMillis() + ".png")

                storageReference.putFile(item).addOnSuccessListener { taskSnapshot ->
                    val uri = taskSnapshot.storage.downloadUrl
                        .addOnCompleteListener { task ->
                           imagesList.add(task.getResult().toString())

                            Log.v("size","add")
                            if(index==images.size-1){
                                uploadData()
                            }

                        }
                }
            }


        }
        else
        {
            uploadData()
        }


    }

    private fun uploadData(){
        if (binding.etTitle.text.isBlank() || binding.etDescription.text.isBlank() || binding.etDate.text.isBlank() || binding.etTime.text.isBlank() || binding.etLocation.text.isBlank() || imagesList.isNullOrEmpty())
            showMessage("All fields are required")
        if (!binding.etTitle.text.isBlank() && !binding.etDescription.text.isBlank() && !binding.etDate.text.isBlank() && !binding.etTime.text.isBlank() && !binding.etLocation.text.isBlank() && !imagesList.isNullOrEmpty()) {
            Thread {
                var eventid = db.collection("events").document().id
                val newEventRef = db.collection("events").document(eventid)
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                val event = EventModel(
                    eventid,
                    binding.etTitle.text.toString(),
                    binding.etDescription.text.toString(),
                    imagesList,
                    selectedLatLng,
                    binding.etLocation.text.toString(),
                    Timestamp.now(),
                    userName,
                    MultiselectDialog.selectedInterestsArray,
                    binding.etDate.text.toString(),
                    binding.etTime.text.toString()
                )
                newEventRef.set(event)
                    .addOnSuccessListener {
                        runOnUiThread {
                            showMessage("Event Uploaded successfully")
                        }

                        for(user in usersList){

                            if(userHaveInterest(user)){

                                var distance=getDistanceBetweenTwoPoints(selectedLatLng?.latitude,selectedLatLng?.longitude,user.location?.latitude,user.location?.longitude)

                                distance /= 1000

                                Log.v("distance",distance.toString())
                                if(distance>0.0&&distance<=10){
                                    if(UserManager.user?.tokenId?.equals(user.tokenId) != true){
                                        sendNotification(user.tokenId,eventid) }
                                }

                            }

                        }
                    }


            }.start()
            startActivity(Intent(this, UserDashboard::class.java))
            finish()
        }
    }

    private fun showMessage(m: String) {
        Toast.makeText(applicationContext, m, Toast.LENGTH_SHORT).show()
    }

    fun showDialog(view: android.view.View) {
        MultiselectDialog(interestsArray, binding.tvInterests, "OK").show(
            supportFragmentManager,
            "interestDialog"
        )
    }

    private fun openCamera() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission wasn't enabled
                val permission= arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            }
            else{
                pickCameraIntent()
            }
        }
        else{
            pickCameraIntent()
        }
    }

    private fun pickCameraIntent() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    packageName,
                    photoFile
                )
                mUri = photoURI
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent,IMAGE_CAPTURE_CODE)
            }
        }
    }


    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = System.currentTimeMillis().toString()
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }


    private fun pickImagesIntent() {
        val intent= Intent()
        intent.type="image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action= Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"), PICK_IMAGES_CODE)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_CAPTURE_CODE){
            if(resultCode == Activity.RESULT_OK){
                imageSwitcher.setImageURI(mUri)
                mUri?.let { images!!.add(it) }
            }
        }
        else
        if(requestCode == PICK_IMAGES_CODE){
            if(resultCode == Activity.RESULT_OK )
            {
                if(data!!.clipData != null){
                    //picked multiple images
                    //get number of picked images
                    val count = data.clipData!!.itemCount
                    for(i in 0 until count){
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        //age image to list
                        images!!.add(imageUri)
                        mUri=imageUri
                    }
                    //set first image from list to image switcher
                    imageSwitcher?.setImageURI(images!![0])
                }
                else{
                    //picked single image
                    val imageUri= data.data
                    //set image to image switcher
                    imageSwitcher?.setImageURI(imageUri)
                    mUri=imageUri
                    imageUri?.let { images.add(it) }
                }
            }
        }


    }


    private fun sendNotification(tokenId:String,eventId:String) {


        val message="New event added"

        val apiClient =
            ApiClient.getClient("https://fcm.googleapis.com/")?.create(ApiInterface::class.java)
        val to: String = tokenId
        val data = Notification(
            UserManager.user!!.FirstName,
            message, eventId,
            "Event"
        )
        val notification = Message(to, data)
        val call: Call<Message?>? = apiClient?.sendMessage("key=$FIRE_BASE_SERVER_KEY", notification)

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