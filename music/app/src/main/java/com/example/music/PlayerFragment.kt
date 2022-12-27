package com.example.music

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.music.databinding.FragmentPlayerBinding
import com.example.music.service.MusicDto
import com.example.music.service.MusicService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var model : PlayerModel = PlayerModel()
    private var binding : FragmentPlayerBinding? = null
    private var player : SimpleExoPlayer? = null

    //ㅈㅐ생중이면 한번 더 updateSeekㅇㅡㄹ 계속 불러버리겠다.
    private val updateSeekRunnable = Runnable {
        updateSeek()
    }

    private lateinit var playListAdapter: PlayListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initPlayView(fragmentPlayerBinding)
        initPlayListButton(fragmentPlayerBinding)
        initPlayControlButtons(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)

        initSeekBar(fragmentPlayerBinding)
        getVideoList()
    }

    private fun initSeekBar(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.sbPlayer.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                player?.seekTo((seekBar.progress * 1000).toLong())
            }

        })

        fragmentPlayerBinding.sbPlaylist.setOnTouchListener{ v, event ->
            false
        }
    }

    private fun initPlayControlButtons(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.ivPlayControl.setOnClickListener {
            val player = this.player ?: return@setOnClickListener
            if(player.isPlaying){
                player.pause()
            } else{
                player.play()
            }

        }
        fragmentPlayerBinding.ivSkipNext.setOnClickListener {
            val nextMusic = model.nextMusic() ?: return@setOnClickListener
            playMusic(nextMusic)
        }
        fragmentPlayerBinding.ivSkipPrev.setOnClickListener {
            val prevMusic = model.prevMusic() ?: return@setOnClickListener
            playMusic(prevMusic)
        }
    }

    private fun initPlayView(fragmentPlayerBinding: FragmentPlayerBinding) {
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }
        fragmentPlayerBinding.pvPlayer.player = player
        binding?.let { binding->
            player?.addListener(object : Player.EventListener{
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if(isPlaying){
                        binding.ivPlayControl.setImageResource(R.drawable.ic_baseline_pause_48)
                    } else {
                        binding.ivPlayControl.setImageResource(R.drawable.ic_baseline_play_arrow_48)
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)

                    val newIndex = mediaItem?.mediaId ?: return
                    model.currentPosition = newIndex.toInt()

                    updatePlayerView(model.currentMusicModel())
                    playListAdapter.submitList(model.getAdapterModels())
                }

                override fun onPlaybackStateChanged(state: Int) {
                    super.onPlaybackStateChanged(state)

                    updateSeek()
                }
            })

        }
    }

    private fun updateSeek() {
        val player = this.player ?: return
        val duration = if(player.duration >= 0) player.duration else 0
        val position = player.currentPosition

        updateSeekUi(duration, position)

        //todo ui update
        val state = player.playbackState

        //ㅇㅕ러번 호출되는 것을 막기 위해 대기 중인 콜백 지움
        view?.removeCallbacks(updateSeekRunnable)

        //player가 돌아가고 있을때
        if(state != Player.STATE_IDLE && state != Player.STATE_ENDED){
            view?.postDelayed(updateSeekRunnable, 1000)
        }
    }
    private fun updateSeekUi(duration: Long, position : Long){
        binding?.let {binding ->
            binding.sbPlaylist.max = (duration / 1000).toInt()
            binding.sbPlaylist.progress = (position / 1000).toInt()

            binding.sbPlayer.max = (duration / 1000).toInt()
            binding.sbPlayer.progress = (position / 1000).toInt()

            binding.tvPlaytime.text = String.format("%02d:%02d", TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS), (position / 1000) % 60)
            binding.tvTotaltime.text = String.format("%02d:%02d", TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS), (duration / 1000) % 60)
        }
    }

    private fun updatePlayerView(currentMusicModel: MusicModel?) {
        currentMusicModel ?: return

        binding?.let { binding ->
            binding.tvTrack.text = currentMusicModel.track
            binding.tvArtist.text = currentMusicModel.artist
            Glide.with(binding.ivCover.context)
                .load(currentMusicModel.coverUrl)
                .into(binding.ivCover)
        }
    }

    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        playListAdapter = PlayListAdapter {
            playMusic(it)
        }

        fragmentPlayerBinding.rvPlaylisy.apply {
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(context)

        }

    }

    private fun initPlayListButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.ivPlaylist.setOnClickListener {

            if(model.currentPosition == -1)
                return@setOnClickListener

            fragmentPlayerBinding.vgPlayer.isVisible = model.isWatchingPlayListView
            fragmentPlayerBinding.vgPlaylist.isVisible = model.isWatchingPlayListView.not()

            model.isWatchingPlayListView = !model.isWatchingPlayListView
        }
    }

    private fun getVideoList(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MusicService::class.java)
            .also{
                it.listMusics()
                    .enqueue(object : Callback<MusicDto>{
                        override fun onResponse(
                            call: Call<MusicDto>,
                            response: Response<MusicDto>
                        ) {
                           Log.d("PlayFragment", "${response.body()}")
                           response.body()?.let { musicDto ->
                               model = musicDto.mapper()
                               setMusicList(model.getAdapterModels())
                               playListAdapter.submitList(model.getAdapterModels())
                           }
                        }

                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {
                            Log.d("PlayFragment", "${t.message}")
                        }
                    })
            }
    }

    private fun setMusicList(list: List<MusicModel>) {
        context?.let{
            player?.addMediaItems(list.map{
                //MediaItem.fromUri()
                MediaItem.Builder()
                    .setMediaId(it.id.toString())
                    .setUri(it.streamUrl)
                    .build()
            })

            player?.prepare()
        }
    }

    private fun playMusic(musicModel: MusicModel){
        model.updateCurrentPosition(musicModel)
        player?.seekTo(model.currentPosition, 0)
        player?.play()
    }

    override fun onStop() {
        super.onStop()

        player?.pause()
        view?.removeCallbacks(updateSeekRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
        player?.release()
        view?.removeCallbacks(updateSeekRunnable)
    }

    companion object {
        fun newInstance() : PlayerFragment{
            return PlayerFragment()
        }
    }
}