package firebaseNotifications.retrofit


import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    val FIRE_BASE_SERVER_KEY="AAAAQNID3HM:APA91bGD72r_TuVignX7EWPqd_yMo__uG7lO8fDDuGCqlcLmYdu5QTkq0Ud9h-CzZrRWkUREI-EjeJ0TkctmeVhBXB2zcU8E9AqIo-t8VZ7P2078kB_An44uI29W3PHlshxkRfrxpPyl"


    private var retrofit: Retrofit? = null
    fun getClient(baseUrl: String?): Retrofit? {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        clientBuilder.addInterceptor(loggingInterceptor)
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}