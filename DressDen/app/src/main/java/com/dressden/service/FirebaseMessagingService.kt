package com.dressden.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dressden.DressDenApp.Companion.CHANNEL_GENERAL
import com.dressden.DressDenApp.Companion.CHANNEL_ORDERS
import com.dressden.DressDenApp.Companion.CHANNEL_PROMOTIONS
import com.dressden.R
import com.dressden.ui.main.MainActivity
import com.dressden.work.NotificationSyncWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("From: ${remoteMessage.from}")

        // Handle data payload
        remoteMessage.data.isNotEmpty().let {
            Timber.d("Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Handle notification payload
        remoteMessage.notification?.let {
            Timber.d("Message Notification Body: ${it.body}")
            it.body?.let { body -> sendNotification(it.title, body, remoteMessage.data) }
        }
    }

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationSyncWorker>()
            .setInputData(
                Data.Builder()
                    .putAll(data)
                    .build()
            )
            .build()

        workManager.enqueue(workRequest)
    }

    private fun sendNotification(
        title: String?,
        messageBody: String,
        data: Map<String, String>
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data.forEach { (key, value) -> putExtra(key, value) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = when (data["type"]) {
            "order" -> CHANNEL_ORDERS
            "promotion" -> CHANNEL_PROMOTIONS
            else -> CHANNEL_GENERAL
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channels for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                when (channelId) {
                    CHANNEL_ORDERS -> getString(R.string.notification_channel_orders)
                    CHANNEL_PROMOTIONS -> getString(R.string.notification_channel_promotions)
                    else -> getString(R.string.notification_channel_general)
                },
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO: Send token to your server
        val workRequest = OneTimeWorkRequestBuilder<NotificationSyncWorker>()
            .setInputData(
                Data.Builder()
                    .putString("fcm_token", token)
                    .build()
            )
            .build()

        workManager.enqueue(workRequest)
    }
}
