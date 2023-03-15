package com.binay.shaw.justap.services

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.NotificationHelper
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var notificationHelper: NotificationHelper
    val channelId = "notification_channel_id"
    val channelName = "notification_channel_name"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle incoming notification
        try {
            if (remoteMessage.notification != null) {
                showNotification(
                    remoteMessage.notification?.title,
                    remoteMessage.notification?.body
                )
            } else {
                showNotification(remoteMessage.data["title"], remoteMessage.data["message"])
            }
        } catch (e: Exception) {
            Util.log("Error: ${e.localizedMessage}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Util.log("New token: $token")
    }

    private fun showNotification(
        title: String?,
        body: String?
    ) {
        notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel(channelId, channelName)

        if (title != null && body != null) {
            val builder = createNotificationBuilder(title, body)
            notificationHelper.showNotification(0, builder)
        }
    }

    private fun createNotificationBuilder(
        title: String,
        message: String
    ): NotificationCompat.Builder {
        // Create an Intent for the notification action
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create a NotificationCompat.Builder object
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
    }
}