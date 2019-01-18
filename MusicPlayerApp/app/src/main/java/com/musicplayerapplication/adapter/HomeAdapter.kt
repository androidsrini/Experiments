package com.musicplayerapplication.adapter

import android.app.PendingIntent.getActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.musicplayerapplication.HomeActivity
import com.musicplayerapplication.R
import com.musicplayerapplication.customView.CircleImageView
import com.musicplayerapplication.helper.MusicPlayerManager
import com.musicplayerapplication.model.Music
import com.squareup.picasso.Picasso

class HomeAdapter(val context: Context, val musicList: ArrayList<Music>,
                  val onMusicItemClickListener: HomeActivity.OnMusicItemClickListener) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_details_screen, parent, false))
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val music = musicList[holder.adapterPosition]
        holder.songNameTextView.setText(music.name)
        if (music.albumArtPathId != null) {
            val albumArtBitmap = MusicPlayerManager.getMusicThumb(music.albumArtPathId)
            holder.songImageImageView.setImageBitmap(albumArtBitmap)
        }
        holder.itemView.setOnClickListener {
            onMusicItemClickListener.onMusicItem(music)
        }
    }

    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songNameTextView = itemView.findViewById<TextView>(R.id.songNameTextView);
        val songImageImageView = itemView.findViewById<CircleImageView>(R.id.songImageImageView);
    }
}