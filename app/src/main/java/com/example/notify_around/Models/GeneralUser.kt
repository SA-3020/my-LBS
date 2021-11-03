package com.example.notify_around.Models

open class GeneralUser(
    var UserType: String = "General",
    var PhoneNo: String = "",
    var FirstName: String = "",
    var LastName: String = "",
    var Email: String = "",
    var interests: ArrayList<String> = arrayListOf()//<InterestsModel> = arrayListOf()
)
