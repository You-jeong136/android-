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

        initView()

        CoroutineScope(Dispatchers.Main).launch {
            val events = initData()
            if(events.isNotEmpty())
                initAdapter(events)
        }

        return binding.root
    }

    private fun initView(){
        //뷰페이저 _ 효과주기 연습1 -> 페이지 넘길 때 이전이나 이후 페이지가 옅게 보이게 만들기
        binding.vpRemoteBanner.setPageTransformer{ page, position ->
            when{
                position.absoluteValue >= 1.0F -> {
                    page.alpha = 0F
                } position== 0F -> {
                    page.alpha = 1F
                } else -> {
                    page.alpha = 1F - position.absoluteValue * 2
                }
            }
        }

        //뷰페이저 _ 효과주기 연습2 ->  미리보기 액자
        //viewpager xml에서 clipChlidren이랑 clipToPadding false로 주기.
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(20))
        compositePageTransformer.addTransformer { view: View, fl: Float ->
            val v = 1 - abs(fl)
            view.scaleY = 0.8f + v * 0.2f
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

    suspend fun initData() : List<RemoteEvent>{
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
            }
        }.addOnFailureListener {
            Log.d("******REMOTE_CONFIG_FAILURE", it.stackTrace.toString())
        }.await()

        return events
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

