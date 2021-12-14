package com.example.notify_around.businessUser.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
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
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.notify_around.*
import com.example.notify_around.models.GeneralUser
import com.example.notify_around.models.AdModel
import com.example.notify_around.models.BusinessUser
import com.example.notify_around.databinding.ActivityPostAddBinding
import firebaseNotifications.retrofit.ApiClient
import firebaseNotifications.retrofit.ApiInterface
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import firebaseNotifications.Message
import firebaseNotifications.Notification
import firebaseNotifications.retrofit.ApiClient.FIRE_BASE_SERVER_KEY
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.IOException

class PostAdd : AppCompatActivity() {


    private lateinit var b: ActivityPostAddBinding
    private lateinit var et_title: EditText
    private lateinit var et_Interest: EditText
    private lateinit var et_Description: EditText
    private lateinit var et_adress: EditText
    private lateinit var btnSave: Button
    private lateinit var imageSwitcher: ImageSwitcher
    private lateinit var img_view: ImageView
    private val PERMISSION_CODE: Int= 1000;
    private val IMAGE_CAPTURE_CODE: Int= 1001;
    private var mUri:Uri?=null
    private var images:MutableList<Uri> = ArrayList()
    private var imageUri2:String=""

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var docRef: DocumentReference

    //request code to pick image(s)
    private val PICK_IMAGES_CODE=0

    private lateinit var root_layout: RelativeLayout
    private var selectedLatLng:GeoPoint?=null
    private var businessDetails:BusinessUser?=null
    var interestsArray= mutableListOf<String>()
    var imagesList: MutableList<String> = ArrayList()
    var usersList: MutableList<GeneralUser> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_add)
        b = ActivityPostAddBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

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


        root_layout=findViewById(R.id.rl1)


        btnSave = findViewById(R.id.btn_save)
        et_Interest = findViewById(R.id.et_Interest)
        et_title = findViewById(R.id.et_title)
        et_Description = findViewById(R.id.et_Description)
        et_adress = findViewById(R.id.et_Address)


        //init list  ++++++++FOR GALLERY OPTION+++++++++++
        images=ArrayList()
        //setup image switcher ++++++++FOR GALLERY OPTION+++++++++++
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
                Toast.makeText(this@PostAdd,"Capture images...", Toast.LENGTH_SHORT).show()
                openCamera()
                popupWindow.dismiss()
            }
            tv_gallery.setOnClickListener{
                Toast.makeText(this@PostAdd,"Select images from gallery...", Toast.LENGTH_SHORT).show()
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


        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if(intent!=null){

                    et_adress.setText(intent.getStringExtra("location"))
                    val lat=intent.getStringExtra("lat")
                    val lng=intent.getStringExtra("lng")
                    val latLng= GeoPoint(lat!!.toDouble(), lng!!.toDouble())
                    selectedLatLng=latLng


                }

            }
        }

        et_adress.setOnClickListener {

            startForResult.launch(Intent(this,MapActivity::class.java))
        }


        val message2 = intent.getStringExtra("address")
        val messageTextView: TextView = findViewById(R.id.et_Address)
        messageTextView.text = message2


        btnSave.setOnClickListener {

            Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show()

            var interest = et_Interest.text.toString()
            var description = et_Description.text.toString()
            var adress = et_adress.text.toString()
            var title = et_title.text.toString()
            var uid = mAuth.currentUser?.uid.toString()

            //val uid= intent.getStringExtra("uid") //to get uid of loggedin user from other activity

            //Log.d(TAG, "User Id "+uid.toString())

            if (interest == "" || description == "" || adress == "" || title == "" || images.isNullOrEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()

            } else {

                if (mUri != null) {

                    for ((index, item) in images.withIndex()) {

                        val storageReference: StorageReference =
                            FirebaseStorage.getInstance().getReference()
                                .child("image" + System.currentTimeMillis() + ".png")

                        storageReference.putFile(item).addOnSuccessListener { taskSnapshot ->
                            val uri = taskSnapshot.storage.downloadUrl
                                .addOnCompleteListener { task ->
                                    imagesList.add(task.getResult().toString())

                                    if(index==images.size-1){
                                        postAd(
                                            title,
                                            interest,
                                            description,
                                            adress,
                                            task.getResult().toString(),
                                            uid
                                        )
                                    }

                                }
                        }
                    }

                }
                else
                {
                    postAd(
                        title,
                        interest,
                        description,
                        adress,
                        "",
                        uid
                    )
                }

            }
        }


        et_Interest.setOnClickListener {
            showDialog()
        }

        getUser()
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


    fun getUser() {
        docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
        docRef
            .get()
            .addOnSuccessListener {
                val currentUser = it.toObject(GeneralUser::class.java)!!
                businessDetails=currentUser.businessUser

            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Data not found", Toast.LENGTH_SHORT).show()
            }
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


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses Allow or Deny from permission Request Popup
        when(requestCode){
            PERMISSION_CODE->{
                if(grantResults.size> 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    pickCameraIntent()
                }
                else{
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                mUri?.let { images.add(it) }
            }
        }
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
                        images.add(imageUri)
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
                    if (imageUri != null) {
                        images.add(imageUri)
                    }
                }
            }
        }
    }

    private fun  postAd(
            title:String,
            interest: String,
            description: String,
            adress: String,
            imageUri:String,
            uid: String
    ){
        Log.d(ContentValues.TAG, "User Id"+uid.toString())
        //Log.d(TAG, "User Id ${mAuth.currentUser?.uid}")
        val id=db.collection("Business Ad").document().id
        docRef= db.collection("Business Ad").document(id) //creates collection if doesn't exist already



        val model=AdModel(id,title,businessDetails!!.businessContact,uid,selectedLatLng,description,imagesList,
            MultiselectDialog.selectedInterestsArray,adress,  Timestamp.now() )


        docRef.set(model)
                .addOnSuccessListener {
                    Toast.makeText(this,"Ad Posted!", Toast.LENGTH_SHORT).show()
                    //startActivity(Intent(applicationContext, UserDashboard::class.java))


                    for(user in usersList){

                        if(userHaveInterest(user)){

                            var distance=getDistanceBetweenTwoPoints(selectedLatLng?.latitude,selectedLatLng?.longitude,user.location?.latitude,user.location?.longitude)

                            distance /= 1000

                            if(distance>0.0&&distance<=10){

                                if(UserManager.user?.tokenId?.equals(user.tokenId) != true){
                                sendNotification(user.tokenId) }
                            }

                        }

                    }

                }
                .addOnFailureListener{ e->
                    Toast.makeText(
                            baseContext,
                            e.toString(),
                            Toast.LENGTH_SHORT
                    ).show()
                }
    }



    private fun sendNotification(tokenId:String) {



        var media = ""
        val message="New post added"

        val apiClient =
            ApiClient.getClient("https://fcm.googleapis.com/")?.create(ApiInterface::class.java)
        val to: String = tokenId
        val data = Notification(
            UserManager.user!!.businessUser!!.companyName,
            message, media,
            "Ad"
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

    fun showDialog() {
        MultiselectDialog(interestsArray, et_Interest, "OK").show(
            supportFragmentManager,
            "interestDialog"
        )
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
