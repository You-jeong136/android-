package com.example.video.service

import com.example.video.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    @GET("/v3/a57d8f63-09be-4622-a6f2-916d4d6955ce")
    fun listVideos() : Call<VideoDto>

}