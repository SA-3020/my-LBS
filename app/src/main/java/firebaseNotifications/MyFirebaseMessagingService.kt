package firebaseNotifications

import android.app.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import android.content.Intent
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import org.json.JSONException
import android.app.PendingIntent
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.notify_around.EventDetailsActivity
import com.example.notify_around.ProblemDetailsActivity
import com.example.notify_around.R
import com.example.notify_around.SkillDetailsActivity
import com.example.notify_around.businessUser.activities.AdDetailsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var intent: Intent? = null
    var title: String? = null
    var body: String? = null
    var action: String? = null
    var media: String? = null
    var content: String? = null
    override fun onNewToken(s: String) {
        //Log.v("NEW_TOKEN", s);
        newToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val params = remoteMessage.data
        val `object` = JSONObject(params as Map<String, String>)
        try {
            title = `object`.getString("title")
            body = `object`.getString("body")
            media = `object`.getString("media")
            action = `object`.getString("click_action")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (!body!!.isEmpty() && !media!!.isEmpty()) {
            content = "$media: $body"
        } else if (!body!!.isEmpty()) {
            content = body
        } else if (!media!!.isEmpty()) {
            content = media
        }
        val NOTIFICATION_CHANNEL_ID = resources.getString(R.string.app_name) + " MESSAGING_CHANNEL"
        val pattern = longArrayOf(0, 1000, 500, 1000)
        lateinit var intent: Intent

        when(action){

            "Event"->{
                intent = Intent(this,EventDetailsActivity::class.java)
            }
            "Ad"->{
                intent = Intent(this,AdDetailsActivity::class.java)

            }
            "Problem"->{
                intent = Intent(this,ProblemDetailsActivity::class.java)

            }
            "Skill"->{
                intent = Intent(this,SkillDetailsActivity::class.java)

            }
        }

        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "Your Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = ""
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = pattern
            notificationChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setContentTitle(title)
            .setContentText(content)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)


        notificationBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1000, notificationBuilder.build())
    }

    companion object {
        fun newToken(token: String) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val db = FirebaseFirestore.getInstance()
            val   docRef = userId?.let { db.collection("users").document(it) }


            val user: MutableMap<String, Any> = HashMap()
            user["tokenId"]=token


            docRef?.update(user)?.addOnSuccessListener {
            }
        }
    }
}