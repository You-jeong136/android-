package com.study.aos.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import com.study.aos.R
import com.study.aos.databinding.ActivityMainBinding
import com.study.aos.util.PagerFragmentStateAdapter

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initTab()
        getFirebaseToken()
    }

    private fun getFirebaseToken() : String {
        var token : String = ""
        //등록된 토큰 가져오는 방법. _ 단일 기기 타겟팅 등에서 사용.
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener{ task->
                if(task.isSuccessful){
                    token = task.result.toString()
                    Log.d("FIREBASE_CURRENT_TOKEN****************", token.toString())
                }
            }
        return token
    }

    private fun initTab(){
        supportFragmentManager.beginTransaction().add(R.id.fl_home_main, RemoteConfigFragment()).commit();

        binding.tlMain.addTab(binding.tlMain.newTab().setText(getString(R.string.main_tab_config)))
        binding.tlMain.addTab(binding.tlMain.newTab().setText("EMPTY"))
        binding.tlMain.addTab(binding.tlMain.newTab().setText("EMPTY"))
        binding.tlMain.addTab(binding.tlMain.newTab().setText("EMPTY"))

        binding.tlMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> changeFragment(RemoteConfigFragment())
                    1 -> changeFragment(EmptyFragment())
                    2 -> changeFragment(EmptyFragment())
                    3 -> changeFragment(EmptyFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        /*val pagerAdapter = PagerFragmentStateAdapter(this)
        pagerAdapter.addFragment(RemoteConfigFragment())
        pagerAdapter.addFragment(EmptyFragment())
        pagerAdapter.addFragment(EmptyFragment())
        pagerAdapter.addFragment(EmptyFragment())

        binding.vpHomeMain.adapter = pagerAdapter

        TabLayoutMediator(binding.tlMain, binding.vpHomeMain){ tab, position ->
            val empty = getString(R.string.main_tab_empty)
            when(position){
                0 -> tab.text = getString(R.string.main_tab_config)
                1 -> tab.text =  String.format(empty, position)
                2 -> tab.text =  String.format(empty, position)
                3 -> tab.text =  String.format(empty, position)
            }
        }.attach()*/

    }

    private fun changeFragment(fragment: Fragment) {
        Log.d("fragmentChangd", fragment.toString())
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_home_main, fragment)
            .commit()
    }
}