package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import kotlin.random.Random


class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {

        Log.d("TAG","OnMessageReceived: ${message.data[content]}")
        val value = gson.fromJson(message.data[content], Notific::class.java)
        if( value.recipientId == null || value.recipientId == AppAuth.getInstance().authStateFlow.value.id){
            val str=value.content
            println(str)}
        if (value.recipientId != 0L && value.recipientId != AppAuth.getInstance().authStateFlow.value.id && value.recipientId != null) {
            AppAuth.getInstance().uploadPushToken()
        } else if (value.recipientId == 0L && value.recipientId != AppAuth.getInstance().authStateFlow.value.id && value.recipientId != null) {
            AppAuth.getInstance().uploadPushToken()
        }

        try {
            message.data[action]?.let {

                when (Action.valueOf(it)) {
                    Action.LIKE ->
                        handleLike(gson.fromJson(message.data[content], Like::class.java))

                    Action.TEXT ->
                        handleText(gson.fromJson(message.data[content], hanText::class.java))


                }
            }
        } catch (e: Throwable) {
            //error()
            //можно выдавать ошибку об обновлении
            return
        }
    }

    override fun onNewToken(token: String) {
        AppAuth.getInstance().uploadPushToken(token)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }

    private fun handleText(content: hanText) {
        var notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(content.title)
            .setContentText(content.minText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content.contentText)
            )
            .setDefaults(Notification.DEFAULT_SOUND)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)

    }

    private fun error() {
        var notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.title_error))
            .setContentText(getString(R.string.content_text_error))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.catch_error))
            )
            .setDefaults(Notification.DEFAULT_SOUND)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}

enum class Action {
    LIKE, TEXT,
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)


data class hanText(
    val authorName: String,
    val title: String,
    val contentText: String,
    val minText: String
)

data class Notific(
    val recipientId: Long?,
    val content: String,
)