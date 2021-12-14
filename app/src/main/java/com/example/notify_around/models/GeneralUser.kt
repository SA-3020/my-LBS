package com.example.notify_around.models

import com.google.firebase.firestore.GeoPoint

open class GeneralUser(
    var userType: String = "General",
    var PhoneNo: String = "",
    var FirstName: String = "",
    var LastName: String = "",
    var Email: String = "",
    var location: GeoPoint? = null,
    var businessUser: BusinessUser? = null,
    var interests: ArrayList<String> = arrayListOf(),//<InterestsModel> = arrayListOf()
    var tokenId: String = "",

    )
