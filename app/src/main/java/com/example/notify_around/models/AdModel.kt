package com.example.notify_around.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.lang.StringBuilder
import java.util.ArrayList

class AdModel(

    var id: String ="",
    var title: String ="",
    var contact: String ="",
    var uid: String ="",
    var geoPoints: GeoPoint?=null,
    var desc: String = "",
    var images:MutableList<String> = arrayListOf(),
    var interests: MutableList<String> = arrayListOf(),
    var address: String = "",

    var postedOn: Timestamp? = null,// = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),

){}
