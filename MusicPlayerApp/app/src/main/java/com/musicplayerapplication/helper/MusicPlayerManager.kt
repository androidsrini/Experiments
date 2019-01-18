package com.musicplayerapplication.helper

import android.content.ContentUris
import android.content.Context
import android.graphics.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import com.musicplayerapplication.model.Music
import java.io.File


object MusicPlayerManager {
    val TAG = "Music"
    val MEDIA_PATH = Environment.getExternalStorageDirectory().getPath()
    private val songsList = ArrayList<Music>()
    private val MP3PATTERN = ".mp3"

    fun getAlbumArtUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
    }

    fun getAlbumArtUri(context: Context, albumId: Int): Uri? {
        val artCursor = context.getContentResolver().query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.AlbumColumns.ALBUM_ART),
            MediaStore.Audio.Media._ID + " =?",
            arrayOf(albumId.toString()), null
        )
        val albumArt: String?
        if (artCursor != null) {
            if (artCursor.moveToNext()) {
                albumArt = "file://" + artCursor.getString(0)
            } else {
                albumArt = null
            }
            artCursor.close()
            if (albumArt != null) {
                //BitmapFactory.decodeFile(albumArt)
                return Uri.fromFile(File(albumArt))
            }
        }
        return null
    }

    fun getPlayList(context: Context): ArrayList<Music> {
        val musicResolver = context.getContentResolver()
        val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            do {
                val songPath = musicCursor.getString(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val thisTitle = musicCursor.getString(titleColumn)
                //val songUri = Uri.fromFile(File(songPath))
                Log.d(TAG, " The song path: " + songPath)
                songsList.add(Music(thisTitle, songPath))
            } while (musicCursor.moveToNext())
        }
        return songsList
    }

    fun getMusicThumb(filePath: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val art = retriever.embeddedPicture
            if (art != null) {
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
            }
            retriever.release()
            return bitmap
        } catch (e: Exception) {
            if (bitmap != null && !bitmap.isRecycled) {
                bitmap.recycle()
            }
            System.gc()
            return null
        } catch (o: OutOfMemoryError) {
            System.gc()
            return null
        }
    }

    fun getRoundedShape(imageView: ImageView, scaleBitmapImage: Bitmap): Bitmap {
        val targetWidth = imageView.width
        val targetHeight = imageView.height
        val targetBitmap = Bitmap.createBitmap(
            targetWidth,
            targetHeight, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(targetBitmap)
        canvas.drawBitmap(
            scaleBitmapImage,
            Rect(
                0, 0, scaleBitmapImage.width,
                scaleBitmapImage.height
            ),
            Rect(0, 0, targetWidth, targetHeight), null
        )
        return targetBitmap
    }
}