package com.example.notify_around.models

open class GeneralUser(
    var userType: String = "General",
    var PhoneNo: String = "",
    var FirstName: String = "",
    var LastName: String = "",
    var Email: String = "",
    var businessUser: BusinessUser?=null,
    var interests: ArrayList<String> = arrayListOf()//<InterestsModel> = arrayListOf()
)
