package com.example.video

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.video.adapter.VideoAdapter
import com.example.video.dto.VideoDto
import com.example.video.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_container_home, PlayerFragment())
            .commit()

        videoAdapter = VideoAdapter()
        findViewById<RecyclerView>(R.id.rv_home).apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
        getVideoList()
    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            Log.d("mainActivity", "***************")
            it.listVideos().enqueue(object : Callback<VideoDto>{
                override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    //예외처리
                    Log.d("MainActivity", "Exception" + t.message.toString())
                }

                override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                    if(response.isSuccessful.not()){
                        Log.d("MainActivity", "response fail")
                        return

                    }
                    response.body()?.let{ videoDto ->
                        Log.d("MainActivity", it.toString())
                        videoAdapter.submitList(videoDto.videos)
                    }
                }
            })
        }

    }
}