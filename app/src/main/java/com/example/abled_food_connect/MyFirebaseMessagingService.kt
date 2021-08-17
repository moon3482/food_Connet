package com.example.abled_food_connect

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.VectorDrawable
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMsgService"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        @SuppressLint("LongLogTAG")//이건 뭐지?
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data);
        }

        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body);
        }


        remoteMessage.data?.let {
            when(true){
                !it["islikeBtnClick"].isNullOrEmpty()->{
                    likeBtnClickSendNotification(it["title"]!!,it["body"]!!,it["review_id"]!!)
                }

                !it["isdm"].isNullOrEmpty()->{
                    DMSendNotification(it["title"]!!,it["body"]!!,it["roomId"]!!,it["fromUserProfileImage"]!!,it["fromUserNickname"]!!,it["fromUserTbId"]!!)
                }

                !it["isParentComment"].isNullOrEmpty()->{
                    ParentCommentSendNotification(it["title"]!!,it["body"]!!,it["review_id"]!!)
                }

                !it["isChildComment"].isNullOrEmpty()->{
                    ChildCommentSendNotification(it["title"]!!,it["body"]!!,it["review_id"]!!,it["groupNum"]!!,it["comment_writing_user_id"]!!,it["comment_writing_user_nicname"]!!,it["reviewWritingUserId"]!!)
                }


                !it["finish"].isNullOrEmpty()->{
                    finishRoomCheckSendNotification(it["title"]!!,it["body"]!!,it["roomId"]!!,it["hostName"]!!)
                }
                !it["finishedGroup"].isNullOrEmpty() ->{
                    finishGroupFCMSendNotification(it["title"]!!,it["body"]!!,it["roomId"]!!,it["hostName"]!!)
                }
                !it["finished"].isNullOrEmpty()->{
                    finishDoneSendNotification(it["title"]!!,it["body"]!!,it["roomId"]!!,it["hostName"]!!)
                }
                !it["cancel"].isNullOrEmpty() ->{
                    messageCancelSendNotification(it["title"]!!,it["body"]!!,it["roomId"]!!,it["hostName"]!!)
                }
                else->{
                    messageSendNotification(it["title"]!!,it["body"]!!,it["roomId"]!!,it["hostName"]!!)
                }
            }
        }
    }


    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

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

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
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

        retrofit.create(RoomAPI::class.java).tokenInsert(MainActivity.user_table_id, p0)
            .enqueue(object :
                Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        if (response.body() == "true") {

                        } else {

                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                }
            })
    }

    private fun messageSendNotification(title: String, messageBody: String,roomId:String,hostName:String) {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("FCMRoomId",roomId)
        intent.putExtra("hostName",hostName)

        Log.d("호스트네임", "messageSendNotification: "+hostName)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = "message"
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
            .setGroupSummary(true)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "message"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)


        }

        notificationManager.notify(
            0/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */,
            notificationBuilder
        )
    }
    private fun messageCancelSendNotification(title: String, messageBody: String,roomId:String,hostName:String) {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        Log.d("호스트네임", "messageSendNotification: "+hostName)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = "message"
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
            .setGroupSummary(true)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "message"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)


        }

        notificationManager.notify(
            0/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */,
            notificationBuilder
        )
    }

    private fun finishRoomCheckSendNotification(title: String, messageBody: String,roomId:String,hostName:String) {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("FCMRoomId",roomId)
        intent.putExtra("hostName",hostName)


        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val icon = (ResourcesCompat.getDrawable(resources, R.drawable.ic_finish, null) as VectorDrawable).toBitmap()
        val channelId = "finish"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_noti_icon)
            .setLargeIcon(icon)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setColor(getColor(R.color.txt_white_gray))
            .setPriority(Notification.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(messageBody))
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "finish"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)


        }

        notificationManager.notify(
            1/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */,
            notificationBuilder
        )
    }
    private fun finishDoneSendNotification(title: String, messageBody: String,roomId:String,hostName:String) {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("FCMRoomId",roomId)
        intent.putExtra("hostName",hostName)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val icon = (ResourcesCompat.getDrawable(resources, R.drawable.ic_finish, null) as VectorDrawable).toBitmap()
        val channelId = "finished"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_noti_icon)
            .setLargeIcon(icon)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setColor(getColor(R.color.txt_white_gray))
            .setPriority(Notification.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(messageBody))
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)

            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "finished"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)


        }

        notificationManager.notify(
            2/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */,
            notificationBuilder
        )
    }
    private fun finishGroupFCMSendNotification(title: String, messageBody: String,roomId:String,hostName:String) {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("review",roomId)
        intent.putExtra("hostName",hostName)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val icon = (ResourcesCompat.getDrawable(resources, R.drawable.ic_finish, null) as VectorDrawable).toBitmap()
        val channelId = "Group"

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_noti_icon)
            .setLargeIcon(icon)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setColor(getColor(R.color.txt_white_gray))
            .setPriority(Notification.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(messageBody))
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "Group"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)


        }

        notificationManager.notify(
            3/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */,
            notificationBuilder
        )
    }



    //DM을 받았을 때
    private fun DMSendNotification(title: String, messageBody: String, roomId:String, fromUserProfileImage:String, fromUserNickname:String,fromUserTbId:String) {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("isDM",true)
        intent.putExtra("FCM_DM_RoomId",roomId)
        intent.putExtra("fromUserProfileImage",fromUserProfileImage)
        intent.putExtra("fromUserNickname",fromUserNickname)
        intent.putExtra("fromUserTbId",fromUserTbId)



        Log.d("FCM_DM_RoomId", roomId)

        val pendingIntent = PendingIntent.getActivity(
            this, 999 /* Request code */, intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val channelId = "DM"
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
            .setGroupSummary(true)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "DM"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)


        }

        notificationManager.notify(
            0/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */,
            notificationBuilder
        )
    }


    //부모댓글이 달렸을때
    private fun ParentCommentSendNotification(title: String, messageBody: String, review_id:String) {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("isParentComment",true)
        intent.putExtra("review_id",review_id.toInt())




        Log.d("fcm - review_id", review_id)

        val pendingIntent = PendingIntent.getActivity(
            this, 998 /* Request code */, intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val channelId = "ParentComment"
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
            .setGroupSummary(true)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "ParentComment"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)


        }

        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = manager.getRunningTasks(1)
        val componentName = info[0].topActivity
        val ActivityName = componentName!!.shortClassName.substring(1)
        Log.e("현재 액티비티", "" + ActivityName)


            Log.e("현재 노티왔어요!", "노티왔어요!")
            notificationManager.notify(
                0/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */,
                notificationBuilder
            )

    }


    //자식댓글이 달렸을때
    private fun ChildCommentSendNotification(title: String, messageBody: String, review_id:String, groupNum:String,sendTargetUserTable_id:String,sendTargetUserNicName:String,reviewWritingUserId:String) {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("isChildComment",true)
        intent.putExtra("review_id",review_id.toInt())
        intent.putExtra("groupNum",groupNum.toInt())
        intent.putExtra("sendTargetUserTable_id", sendTargetUserTable_id.toInt())
        intent.putExtra("sendTargetUserNicName",sendTargetUserNicName)
        intent.putExtra("reviewWritingUserId",reviewWritingUserId.toInt())




        Log.d("fcm - review_id", review_id)



        val pendingIntent = PendingIntent.getActivity(
            this, 997 /* Request code */, intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val channelId = "ChildComment"
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
            .setGroupSummary(true)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "ChildComment"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)


        }

        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = manager.getRunningTasks(1)
        val componentName = info[0].topActivity
        val ActivityName = componentName!!.shortClassName.substring(1)
        Log.e("현재 액티비티", "" + ActivityName)



            Log.e("현재 노티왔어요!", "노티왔어요!")
            notificationManager.notify(
                0/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */,
                notificationBuilder
            )


    }





    //좋아요가 눌렸을때
    private fun likeBtnClickSendNotification(title: String, messageBody: String, review_id:String) {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        //부모댓글과 동일한 형식으로 인텐트를 날려주면된다. 키값은 isParentComment으로 준다.
        intent.putExtra("isParentComment",true)
        intent.putExtra("review_id",review_id.toInt())




        Log.d("fcm - review_id", review_id)

        val pendingIntent = PendingIntent.getActivity(
            this, 998 /* Request code */, intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val channelId = "islikeBtnClick"
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
            .setGroupSummary(true)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelName = "islikeBtnClick"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(0, 1500)




            notificationManager.createNotificationChannel(channel)


        }

        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = manager.getRunningTasks(1)
        val componentName = info[0].topActivity
        val ActivityName = componentName!!.shortClassName.substring(1)
        Log.e("현재 액티비티", "" + ActivityName)


            Log.e("현재 노티왔어요!", "islikeBtnClick 노티왔어요!")
            notificationManager.notify(
                0/*(Math.random()*1000).toInt()+System.currentTimeMillis().toInt()*/ /* ID of notification */,
                notificationBuilder
            )

    }



}