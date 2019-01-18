package com.musicplayerapplication.helper

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.musicplayerapplication.MusicPlayerActivity

class NotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun sendMyBroadCast(action: String?) {
        val broadCastIntent = Intent()
        broadCastIntent.action = MusicPlayerActivity.BROADCAST_ACTION
        broadCastIntent.putExtra(MusicPlayerActivity.NOTIFICATION_ACTION, action)
        sendBroadcast(broadCastIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent!!.action
        Log.d("onStartCommand", "action=$action")
        sendMyBroadCast(action)
        /*when (action) {
            NOTIFY_PLAY -> {
                Toast.makeText(applicationContext, "Handle the PLAY button", Toast.LENGTH_LONG).show()
            }
            NOTIFY_NEXT -> {
                Toast.makeText(applicationContext, "Handle the NEXT button", Toast.LENGTH_LONG).show()
            }
        }*/
        return START_STICKY
    }
}