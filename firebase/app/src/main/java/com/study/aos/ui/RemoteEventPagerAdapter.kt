package com.study.aos.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.study.aos.R
import com.study.aos.data.RemoteEvent
import com.study.aos.databinding.ItemRemoteConfigBinding

class RemoteEventPagerAdapter : RecyclerView.Adapter<RemoteEventPagerAdapter.RemoteEventViewHolder>() {

    private var eventList  = emptyList<RemoteEvent>()

    class RemoteEventViewHolder(private val binding : ItemRemoteConfigBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun bind(remoteEvent: RemoteEvent){
                binding.remoteEvent = remoteEvent
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoteEventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRemoteConfigBinding>(
            layoutInflater, R.layout.item_remote_config, parent, false )
        return RemoteEventViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RemoteEventViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size

    fun setRemoteEvent(eventList : List<RemoteEvent>){
        this.eventList = eventList
        notifyDataSetChanged()
    }
}