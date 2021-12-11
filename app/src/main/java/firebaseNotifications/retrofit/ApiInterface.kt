package firebaseNotifications.retrofit


import firebaseNotifications.Message
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @POST("/fcm/send")
    fun sendMessage(
        @Header("Authorization") token: String?,
        @Body notification: Message?
    ): Call<Message?>?
}