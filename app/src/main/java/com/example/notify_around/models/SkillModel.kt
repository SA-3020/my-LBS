package com.example.notify_around.models

import android.icu.text.SimpleDateFormat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.sql.Time
import java.util.*

class SkillModel(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var geoPoints: GeoPoint?=null,
    var interests: MutableList<String> = arrayListOf(),
    var postedOn: Timestamp = Timestamp.now(),//SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
    var postedBy: String = "",
    var locationAt: String = ""
)