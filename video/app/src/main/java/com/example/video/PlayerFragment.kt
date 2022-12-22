package com.example.video

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.video.adapter.VideoAdapter
import com.example.video.databinding.FragmentPlayerBinding
import com.example.video.dto.VideoDto
import com.example.video.service.VideoService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs


class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var binding : FragmentPlayerBinding? = null
    private lateinit var videoAdapter: VideoAdapter
    private var player : SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initMotionLayoutEvent((fragmentPlayerBinding))
        initRecyclerView(fragmentPlayerBinding)
        initPlayer(fragmentPlayerBinding)
        initControlButton(fragmentPlayerBinding)

        getVideoList()
    }

    private fun initMotionLayoutEvent(fragmentPlayerBinding : FragmentPlayerBinding){
        fragmentPlayerBinding.mlPlayer.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted( motionLayout: MotionLayout?, startId: Int, endId: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTransitionChange( motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float ) {
                binding?.let {
                    (activity as MainActivity).also { mainActivity ->
                        mainActivity.findViewById<MotionLayout>(R.id.ml_main).progress = abs(progress)
                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
                //TODO("Not yet implemented")
            }

        })
    }

    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding){
        videoAdapter = VideoAdapter(callback = {url, title ->
            play(url, title)
        })
        fragmentPlayerBinding.rvPlayer.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayer(fragmentPlayerBinding: FragmentPlayerBinding){

        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }
        fragmentPlayerBinding.exoPlayerView.player = player
        binding?.let {
            player?.addListener(object : Player.EventListener{
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if(isPlaying){
                        it.ivBottomPlayerBtn.setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        it.ivBottomPlayerBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
            })
        }

    }

    private fun initControlButton(fragmentPlayerBinding: FragmentPlayerBinding){
        fragmentPlayerBinding.ivBottomPlayerBtn.setOnClickListener{
            val player = this.player ?: return@setOnClickListener
            if(player.isPlaying){
                player.pause()
            } else {
                player.play()
            }
        }
    }


    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            Log.d("mainActivity", "***************")
            it.listVideos().enqueue(object : Callback<VideoDto> {
                override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    //예외처리
                    Log.d("MainActivity", "Exception" + t.message.toString())
                }

                override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                    if (response.isSuccessful.not()) {
                        Log.d("MainActivity", "response fail")
                        return

                    }
                    response.body()?.let { videoDto ->
                        Log.d("MainActivity", it.toString())
                        videoAdapter.submitList(videoDto.videos)
                    }
                }
            })
        }
    }

    fun play(url: String, title : String){
        context?.let {
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            player?.setMediaSource(mediaSource)
            player?.prepare()
            player?.play()
        }
        
        binding?.let {
            it.mlPlayer.transitionToEnd()
            it.tvBottomPlayerTitle.text = title
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}