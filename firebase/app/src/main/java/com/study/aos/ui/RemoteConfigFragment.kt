package com.study.aos.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.study.aos.data.RemoteEvent
import com.study.aos.databinding.FragmentRemoteConfigBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.absoluteValue

class RemoteConfigFragment : Fragment() {
    
    private var _binding: FragmentRemoteConfigBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRemoteConfigBinding.inflate(inflater, container, false)

        initData()
        initView()

        return binding.root
    }

    private fun initView(){
        //뷰페이저 _ 효과주기 연습1 -> 페이지 넘길 때 이전이나 이후 페이지가 옅게 보이게 만들기
        val animaTransformer = ViewPager2.PageTransformer{ page, position ->
            val r = 1 - abs(position)
            page.alpha = 0.8f + r
            page.scaleY =  0.85f + r * 0.15f
        }

        //viewpager xml에서 clipChlidren이랑 clipToPadding false로 주기.
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.apply {
            addTransformer(MarginPageTransformer(20))
            addTransformer(animaTransformer)
        }

        binding.vpRemoteBanner.offscreenPageLimit = 3
        binding.vpRemoteBanner.getChildAt(0).overScrollMode=View.OVER_SCROLL_NEVER
        binding.vpRemoteBanner.setPageTransformer(compositePageTransformer)

    }

    private fun initAdapter(events : List<RemoteEvent>){
        val remoteEventPagerAdapter = RemoteEventPagerAdapter()
        remoteEventPagerAdapter.setRemoteEvent(events)
        binding.vpRemoteBanner.adapter = remoteEventPagerAdapter

        binding.vpRemoteBanner.setCurrentItem(remoteEventPagerAdapter.itemCount / 2, false)
    }

    private fun initData() {
        val remoteConfig = Firebase.remoteConfig
        var events = emptyList<RemoteEvent>()
        //server에서 block 하지 않는 이상 계속 fetch 할 수 있도록 인터벌 0으로 설정
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                // 연습하느라 0으로 줬지만, 권장은 최소 1시간 _ 60분 _ 3600초
                minimumFetchIntervalInSeconds = 0
            }
        )

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if(it.isSuccessful){
                events = parseEventsJson(remoteConfig.getString("remoteEvent"))
                initAdapter(events)
            }
        }.addOnFailureListener {
            Log.d("******REMOTE_CONFIG_FAILURE", it.stackTrace.toString())
        }
    }

    private fun parseEventsJson(json: String): List<RemoteEvent> {
        val jsonArray = JSONArray(json)
        var jsonList = emptyList<JSONObject>()

        for(i in 0 until jsonArray.length()){
            jsonArray.getJSONObject(i)?.let {
                jsonList = jsonList + it
            }

        }

        return jsonList.map {
            RemoteEvent( imageUrl = it.getString("imageUrl"),
                        name = it.getString("name"))
        }
    }

}

