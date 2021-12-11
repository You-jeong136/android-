package com.study.aos

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.bumptech.glide.request.transition.Transition


class FirebaseMessagingService : FirebaseMessagingService() {

    //포그라운드 상태에서도 _ notification 알림을 받기 위한 설정.
    override fun onMessageReceived(remoteMessage: RemoteMessage){
        super.onMessageReceived(remoteMessage)

        //받은 remoteMessage의 값 출력해보기.

        Log.d("FirebaseMessagingService*************", "data paylod : ${remoteMessage.data}")

        remoteMessage.notification?.let {
            Log.d("FirebaseMessagingService*************", "Message Notification Body: ${it.body}")
            //포그라운드에서도 알림 받은 것 처럼 받은 정보를 가지고 notification 구현하기.
            sendNotification(remoteMessage.notification!!)
        }



    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    private fun sendNotification(notification : RemoteMessage.Notification ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.noti_channel_id)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //채널 관리 : oreo 버전 이상의 경우, 채널별로 관리
        // 즉, noti 여러개 보낼 경우, 각각 쌓이는게 아니라 앱별로 그룹을 지어서 묶이게 됨.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                getString(R.string.noti_channel_name1),
                NotificationManager.IMPORTANCE_DEFAULT)

            channel.enableLights(true)
            channel.enableVibration(true)

            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        notificationBuilder.setContentTitle(notification.title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(notification.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        //큰 이미지가 따로 올 경우. notification에 imageUrl이 들어있을 경우
        if(notification.imageUrl != null) {

            Log.d("imageUri1***********************", notification.imageUrl.toString())
            Glide.with(applicationContext)
                .asBitmap()
                .load(notification.imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        Log.d("imageUri2*********************", resource.toString())
                            notificationBuilder.setStyle(NotificationCompat.BigPictureStyle()
                                .bigPicture(resource))

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        }

        notificationManager.notify(100, notificationBuilder.build() )
    }

}
