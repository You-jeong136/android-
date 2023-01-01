package com.example.music

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlayListAdapter(private val callback: (MusicModel) -> Unit) : ListAdapter<MusicModel, PlayListAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        fun bind(item : MusicModel){
            val track = view.findViewById<TextView>(R.id.tv_item_track)
            val artist = view.findViewById<TextView>(R.id.tv_item_artist)
            val cover = view.findViewById<ImageView>(R.id.iv_item_cover)

            track.text = item.track
            artist.text = item.artist

            Glide.with(cover.context)
                .load(item.coverUrl)
                .into(cover)

            if(item.isPlaying){
                itemView.setBackgroundColor(Color.GRAY)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            itemView.setOnClickListener {
                callback(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        currentList[position].also { musicModel ->
            holder.bind(musicModel)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MusicModel>() {
            override fun areItemsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}