package com.example.notify_around.Models

import android.icu.text.SimpleDateFormat
import com.google.firebase.firestore.GeoPoint
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class EventModel(
    var title: String = "",
    var desc: String = "",
    var locationAt: GeoPoint? = null,
    var dateCreated: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
    var createdBy: GeneralUser? = null,
    var interests: ArrayList<InterestsModel> = arrayListOf(),
    var dateAt: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
    var timeAt: String = Time(0, 0, 0).toString()
)