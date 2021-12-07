package com.example.notify_around.models

import com.google.firebase.firestore.GeoPoint

class BusinessUser(
    var companyName: String = "",
    var businessEmail: String = "",
    var businessContact: String = "",
    var location: GeoPoint? = null
) {

}