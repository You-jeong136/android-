package com.study.aos.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.study.aos.R
import com.study.aos.data.RemoteEvent
import com.study.aos.databinding.FragmentRemoteConfigBinding

class RemoteConfigFragment : Fragment() {

    private var _binding: FragmentRemoteConfigBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRemoteConfigBinding.inflate(inflater, container, false)

        initAdapter()

        return binding.root
    }

    private fun initAdapter(){
        val remoteEventPagerAdapter = RemoteEventPagerAdapter()
        remoteEventPagerAdapter.setRemoteEvent(
            listOf(
                RemoteEvent("https://media.vlpt.us/images/yujeong136/post/31c0ffb3-e52a-4a4c-b5c1-05a63e7f4c20/Android%20Logo.PNG", "이벤트1"),
                RemoteEvent("https://media.vlpt.us/images/yujeong136/post/31c0ffb3-e52a-4a4c-b5c1-05a63e7f4c20/Android%20Logo.PNG", "이벤트2")
            )
        )
        binding.vpRemoteBanner.adapter = remoteEventPagerAdapter
    }

    private fun initData(){
        val remoteConfig = Firebase.remoteConfig

        //server에서 block 하지 않는 이상 계속 fetch 할 수 있도록.
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
    }
}