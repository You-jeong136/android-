package com.example.video.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.video.R
import com.example.video.model.VideoModel

class VideoAdapter(val callback : (String, String) -> Unit) : ListAdapter<VideoModel, VideoAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        fun bind(item : VideoModel){
            val title = view.findViewById<TextView>(R.id.tv_title)
            val subTitle = view.findViewById<TextView>(R.id.tv_subtitle)
            val thumbnail = view.findViewById<ImageView>(R.id.iv_thumbnail)

            title.text = item.title
            subTitle.text = item.subtitle
            Glide.with(thumbnail.context)
                .load(item.thumb)
                .into(thumbnail)

            view.setOnClickListener {
                callback(item.sources, item.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate( R.layout.item_video, parent,false ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VideoModel>(){
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                //보통은 객체 자체가 아니라 old와 new의 id값을 비교하는 방식으로 비교
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}