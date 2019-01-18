package com.musicplayerapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.musicplayerapplication.adapter.HomeAdapter
import com.musicplayerapplication.helper.MusicPlayerManager
import com.musicplayerapplication.model.Music
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    private val TAG = "Music"
    private val READ_EXTERNAL_STORAGE_PERMISSION = 0x001
    private val musicPlayerList = ArrayList<Music>()
    private lateinit var homeAdapter: HomeAdapter

    fun AppCompatActivity.isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    fun AppCompatActivity.requestPermission(permission: String, requestId: Int) =
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            musicPlayerList.addAll(MusicPlayerManager.getPlayList(this))
            Log.d(TAG, " The music: " + musicPlayerList)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_PERMISSION)
            }
        }
        homeAdapter = HomeAdapter(this@HomeActivity, musicPlayerList, object : OnMusicItemClickListener {
            override fun onMusicItem(music: Music) {
                MusicPlayerActivity.start(this@HomeActivity, music)
            }

        })
        musicPlayerListRecyclerView.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(this@HomeActivity)
            addItemDecoration(DividerItemDecoration(this@HomeActivity, DividerItemDecoration.VERTICAL))
        }
        musicPlayerListRecyclerView.layoutManager
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults[0] == (PackageManager.PERMISSION_GRANTED)) {
                musicPlayerList.addAll(MusicPlayerManager.getPlayList(this))
                homeAdapter.notifyDataSetChanged()
                Log.d(TAG, " The music: " + musicPlayerList)
            } else {
                Log.i(TAG, "permission was NOT granted.")

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    interface OnMusicItemClickListener {
        fun onMusicItem(music: Music)
    }
}
