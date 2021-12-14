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

    // 메세지 수신 시 마다 해당 메소드 호출됨.
    override fun onMessageReceived(remoteMessage: RemoteMessage){
        super.onMessageReceived(remoteMessage)

        //받은 remoteMessage의 값 출력해보기.
        Log.d("FirebaseMessagingService*************", "Message data : ${remoteMessage.data}")
        Log.d("FirebaseMessagingService*************", "Message noti : ${remoteMessage.notification}")

        //알림 메세지의 경우.
        remoteMessage.notification?.let {
            Log.d("FirebaseMessagingService*************", "Message Notification Body: ${it.body}")
            //알림 메세지 _ 포그라운드에서도 알림 받은 것 처럼 받은 정보를 가지고 notification 구현하기.
            sendNotification(remoteMessage.notification!!)
        }

        //데이터 메세지의 경우.
        if(remoteMessage.data.isNotEmpty()){
            sendDataMessage(remoteMessage.data)
        }

    }

    //FirebaseMessaingServie를 확장시 _ onNewToken 재정의 필요
    // 등록토큰이 앱 데이터 삭제, 혹은 앱 삭제 및 재설치, 앱 복원 등의 상황에서 변경될 수 있기에
    // 앱에서 토큰이 갱신될 때마다 서버에 해당 토큰을 갱신, 처리해주게 됨.
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    //알림 메세지 _ 포그라운드일 경우 처리하기. _ 알림 받은 것 처럼 받은 정보로 notification 구현
    private fun sendNotification(notification : RemoteMessage.Notification ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
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
                        Log.d("imageObject1*********************", resource.toString())
                            notificationBuilder.setStyle(NotificationCompat.BigPictureStyle()
                                .bigPicture(resource))

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        }

        notificationManager.notify(100, notificationBuilder.build() )
    }

    //데이터 메세지 _ 백/포 전부 이걸로 처리. _ notification 구현.
    private fun sendDataMessage(data: MutableMap<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setContentTitle(data["title"])
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(data["message"])
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        //큰 이미지가 따로 올 경우. notification에 imageUrl이 들어있을 경우
        if(data["image"] != null) {

            Log.d("imageUri2***********************", data["image"].toString())
            Glide.with(applicationContext)
                .asBitmap()
                .load(data["image"])
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        Log.d("imageObject2*********************", resource.toString())
                        notificationBuilder.setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(resource))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        }
        notificationManager.notify(100, notificationBuilder.build() )

    }

    //채널 관리 : oreo 버전 이상의 경우, 채널별로 관리
    // 즉, noti 여러개 보낼 경우, 각각 쌓이는게 아니라 앱별로 그룹을 지어서 묶이게 됨.
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableLights(true)
            channel.enableVibration(true)

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_NAME = "FCM STUDY"
        private const val CHANNEL_ID = "FCM__channel_id"

    }

}
