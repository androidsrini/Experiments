package com.musicplayerapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.musicplayerapplication.helper.*
import com.musicplayerapplication.model.Music
import kotlinx.android.synthetic.main.content_music_player.*
import java.io.IOException


class MusicPlayerActivity : AppCompatActivity() {

    private val mHandler = Handler();
    private var mediaPlayer: MediaPlayer? = null
    private var notificationGenerator: NotificationGenerator? = null
    private var musicBroadCastReceiver: MusicBroadCastReceiver? = null

    companion object {
        val MUSIC_ARG = "MusicArg";
        val NOTIFICATION_ACTION = "NotificationActionArg";
        val BROADCAST_ACTION = "com.musicplayerapplication.broadcastreceiverdemo"

        fun start(context: Context, music: Music) {
            val intent = Intent(context, MusicPlayerActivity::class.java)
            intent.putExtra(MUSIC_ARG, music)
            context.startActivity(intent)
        }
    }

    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            val totalDuration = mediaPlayer?.getDuration()
            val currentDuration = mediaPlayer?.getCurrentPosition()
            val progress = Utils.getProgressPercentage(currentDuration?.toLong(), totalDuration?.toLong()) as Int
            songsPlaySeekBar.setProgress(progress)
            mHandler.postDelayed(this, 100)
        }
    }

    fun playMusic(music: Music) {
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(music.albumArtPathId)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
                override fun onCompletion(mp: MediaPlayer?) {
                    songsPlayImageView.isSelected = false
                }

            })
            val imageBitmap = MusicPlayerManager.getMusicThumb(music.albumArtPathId!!)
            songAlbumImageView.setImageBitmap(imageBitmap)
            songsPlaySeekBar.setProgress(0)
            songsPlaySeekBar.setMax(100)
            songsPlayImageView.isSelected = true
            updateProgressBar()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun registerMyReceiver() {
        try {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BROADCAST_ACTION)
            registerReceiver(musicBroadCastReceiver, intentFilter)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    fun updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100)
    }

    fun stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer = null
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        songsPlayImageView.isSelected = false
    }

    fun playMusic() {
        mediaPlayer?.start()
        songsPlayImageView.isSelected = true
    }

    fun seekMusic(seekBar: SeekBar?) {
        val totalDuration = mediaPlayer!!.getDuration()
        val currentPosition = Utils.progressToTimer(seekBar!!.getProgress(), totalDuration)
        mediaPlayer!!.seekTo(currentPosition)
        updateProgressBar()
    }

    fun seekMusic() {
        val totalDuration = mediaPlayer!!.getDuration()
        val currentDuration = songsPlaySeekBar.progress + 10
        val currentPosition = Utils.progressToTimer(currentDuration, totalDuration)
        mediaPlayer!!.seekTo(currentPosition)
        updateProgressBar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_music_player)
        notificationGenerator = NotificationGenerator()
        musicBroadCastReceiver = MusicBroadCastReceiver()
        mediaPlayer = MediaPlayer()
        val music = intent.getParcelableExtra<Music>(MUSIC_ARG)
        if (music != null) {
            val imageBitmap = MusicPlayerManager.getMusicThumb(music.albumArtPathId!!)
            notificationGenerator!!.showBigContentMusicPlayer(applicationContext, music.name)
            playMusic(music)
        }
        songsPlayImageView.setOnClickListener({
            if (!songsPlayImageView.isSelected && !mediaPlayer?.isPlaying!!) {
               playMusic()
            } else {
               pauseMusic()
            }
        })
        songsPlaySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mHandler.removeCallbacks(mUpdateTimeTask)
                seekMusic(seekBar)
            }
        })

        val serviceIntent = Intent(applicationContext, NotificationService::class.java)
        //serviceIntent!!.setAction(STARTFOREGROUND_ACTION)
        startService(serviceIntent)
        registerMyReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (notificationGenerator != null) {
            notificationGenerator!!.dismissNotification()
        }
        unregisterReceiver(musicBroadCastReceiver)
        stopMusic()
    }

    internal inner class MusicBroadCastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            //Log.d(FragmentActivity.TAG, "onReceive() called")
            val action = intent.getStringExtra(NOTIFICATION_ACTION)
            when (action) {
                NOTIFY_PLAY -> {
                    //Toast.makeText(applicationContext, "Handle the PLAY button", Toast.LENGTH_LONG).show()
                    if (!songsPlayImageView.isSelected && !mediaPlayer?.isPlaying!!) {
                        playMusic()
                    }
                }
                NOTIFY_SEEK-> {
                    if (songsPlayImageView.isSelected && mediaPlayer?.isPlaying!!) {
                        seekMusic()
                    }
                    //Toast.makeText(applicationContext, "Handle the NEXT button", Toast.LENGTH_LONG).show()
                }
                NOTIFY_PAUSE->{
                    if (songsPlayImageView.isSelected && mediaPlayer?.isPlaying!!) {
                        pauseMusic()
                    }
                }
                NOTIFY_CLOSE->{
                    if (notificationGenerator != null) {
                        notificationGenerator!!.dismissNotification()
                    }
                }
            }
        }
    }

}
