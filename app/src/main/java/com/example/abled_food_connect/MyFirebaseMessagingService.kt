package com.example.abled_food_connect

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyFirebaseMessagingService: FirebaseMessagingService() {
    private  val TAG = "MyFirebaseMsgService"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        @SuppressLint("LongLogTAG")//이건 뭐지?
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data);
        }

        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body);
        }

        remoteMessage.data?.let {
            val title = it["title"]
            val body = it["body"]
            if (title != null) {
                if (body != null) {
                    sendNotification2(title,body)
                }
            }
        };
    }
//
//
private fun sendNotification(title:String,messageBody: String) {
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        PendingIntent.FLAG_ONE_SHOT)

    val channelId = "food"
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val notificationBuilder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_noti_icon)
        .setColor(getColor(R.color.txt_white_gray))
        .setContentTitle(title)
        .setContentText(messageBody)
        .setPriority(Notification.PRIORITY_HIGH)
        .setDefaults(Notification.DEFAULT_VIBRATE)
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
}

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(RoomAPI::class.java).tokenInsert(MainActivity.user_table_id,p0).enqueue(object:
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful){
                    if (response.body() == "true"){

                    }else{

                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }
    private fun sendNotification2(title: String,messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = "id"
        val groupId = packageName
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_noti_icon)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(false)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setColor(getColor(R.color.txt_white_gray))
            .setPriority(Notification.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)
            .setGroup(groupId)
            .setGroupSummary(true)
            .build()




        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "ssssdsadas"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannelGroup(NotificationChannelGroup(channelId,groupId))

        }

        notificationManager.notify(0/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */, notificationBuilder)
    }
}