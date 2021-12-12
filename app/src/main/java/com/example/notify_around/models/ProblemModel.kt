package com.example.notify_around.models

import android.icu.text.SimpleDateFormat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.sql.Time
import java.util.*

class ProblemModel(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var levelOfEmergency: String = "",
    var geoPoints: GeoPoint?=null,
    var postedOn: Timestamp? = null,// = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
    var postedBy: String = "",
    var dateAt: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
    var timeAt: String = Time(0, 0, 0).toString(),
    var locationAt: String = ""
)