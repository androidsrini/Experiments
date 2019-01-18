package com.musicplayerapplication.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.view.View
import android.widget.RemoteViews
import com.musicplayerapplication.MusicPlayerActivity
import com.musicplayerapplication.R

const val NOTIFY_PAUSE = "com.musicplayerapplication.pause"
const val NOTIFY_PLAY = "com.musicplayerapplication.play"
const val NOTIFY_SEEK = "com.musicplayerapplication.seek"
const val NOTIFY_CLOSE = "com.musicplayerapplication.close"
const val NOTIFICATION_ID_BIG_CONTENT = 99

class NotificationGenerator(var notificationIntentClass: Class<*> = MusicPlayerActivity::class.java) {

    private var notificationManager: NotificationManager? = null
    private var notificationChannel: NotificationChannel? = null
    private val channelId = "com.musicplayerapplication.notificationdemo"
    private val description = "Test notification"

    fun showBigContentMusicPlayer(context: Context, title: String?) {
        // Using RemoteViews to bind custom layouts into Notification
        val smallView = RemoteViews(context.packageName, R.layout.status_bar)
        val bigView = RemoteViews(context.packageName, R.layout.status_bar_expanded)

        // showing default album image
        smallView.setViewVisibility(R.id.status_bar_icon, View.VISIBLE)
        smallView.setViewVisibility(R.id.status_bar_album_art, View.GONE)
        bigView.setImageViewBitmap(
            R.id.status_bar_album_art,
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        )
        setListeners(bigView, smallView, title, context)

        // Build the content of the notification
        val nBuilder = getNotificationBuilder(
            context,
            "Music Player",
            "Control Audio",
            R.mipmap.ic_launcher,
            "Illustrate how a big content notification can be created."
        )

        // Notification through notification manager
        lateinit var notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nBuilder.setCustomBigContentView(bigView)
            nBuilder.setCustomContentView(smallView)
            notification = nBuilder.build()
        } else {
            notification = nBuilder.build()
            notification.contentView = smallView
            notification.bigContentView = bigView
        }

        // Notification through notification manager
        notification.flags = Notification.FLAG_ONGOING_EVENT
        notificationManager?.notify(NOTIFICATION_ID_BIG_CONTENT, notification)
    }

    private fun setListeners(bigView: RemoteViews, smallView: RemoteViews, title: String?, context: Context) {
        val intentNext = Intent(context, NotificationService::class.java)
        intentNext.action = NOTIFY_PAUSE
        val pendingIntentNext = PendingIntent.getService(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT)
        bigView.setOnClickPendingIntent(R.id.status_bar_pause, pendingIntentNext)
        //smallView.setOnClickPendingIntent(R.id.status_bar_pause, pendingIntentNext)

        val intentPlay = Intent(context, NotificationService::class.java)
        intentPlay.action = NOTIFY_PLAY
        val pendingIntentPlay = PendingIntent.getService(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
        bigView.setOnClickPendingIntent(R.id.status_bar_play, pendingIntentPlay)
        smallView.setOnClickPendingIntent(R.id.status_bar_play, pendingIntentPlay)

        val intentSeek = Intent(context, NotificationService::class.java)
        intentSeek.action = NOTIFY_SEEK
        val pendingIntentSeek = PendingIntent.getService(context, 0, intentSeek, PendingIntent.FLAG_UPDATE_CURRENT)
        bigView.setOnClickPendingIntent(R.id.status_bar_seek, pendingIntentSeek)

       /* val intentClose = Intent(context, NotificationService::class.java)
        intentClose.action = NOTIFY_CLOSE
        val pendingIntentClose = PendingIntent.getService(context, 0, intentClose, PendingIntent.FLAG_UPDATE_CURRENT)
        bigView.setOnClickPendingIntent(R.id.status_bar_collapse, pendingIntentClose)
        smallView.setOnClickPendingIntent(R.id.status_bar_collapse, pendingIntentClose)*/

        bigView.setTextViewText(R.id.status_bar_track_name, title)
        smallView.setTextViewText(R.id.status_bar_track_name, title)

        /*smallView.setBitmap(R.id.status_bar_icon, "setImageViewBitmap", imageBitmap)
        bigView.setBitmap(R.id.status_bar_album_art, "setImageViewBitmap", imageBitmap)*/

        /*bigView.setTextViewText(R.id.status_bar_artist_name, "Artist Name")
        smallView.setTextViewText(R.id.status_bar_artist_name, "Artist Name")

        bigView.setTextViewText(R.id.status_bar_album_name, "Album Name")*/
    }

    private fun getNotificationBuilder(
        context: Context,
        notificationTitle: String,
        notificationText: String,
        notificationIconId: Int,
        notificationTicker: String
    ): Notification.Builder {
        // Define the notification channel for newest Android versions
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = getPendingIntent(context)
        lateinit var builder: Notification.Builder

        if (Build.VERSION.SDK_INT >= O) {
            if (null == notificationChannel) {
                notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel?.enableLights(true)
                notificationChannel?.lightColor = Color.GREEN
                notificationChannel?.enableVibration(false)
                notificationManager?.createNotificationChannel(notificationChannel)
            }
            builder = Notification.Builder(context, channelId)
        } else {
            builder = Notification.Builder(context)
        }

        // Build the content of the notification
        builder.setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(notificationIconId)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, notificationIconId))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setTicker(notificationTicker)
        // Restricts the notification information when the screen is blocked.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PRIVATE)
        }

        return builder
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val resultIntent = Intent(context, notificationIntentClass)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val resultPendingIntent = PendingIntent.getActivity(
            context, 0,
            resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        return resultPendingIntent
    }

    fun dismissNotification() {
        notificationManager?.cancelAll()
    }
}