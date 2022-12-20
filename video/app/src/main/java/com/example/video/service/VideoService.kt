package com.example.video.service

import com.example.video.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    @GET("/v3/1d20b88b-123b-49bf-873a-e4d375325086")
    fun listVideos() : Call<VideoDto>

}