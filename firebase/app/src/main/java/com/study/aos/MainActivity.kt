package com.study.aos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.study.aos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //등록된 토큰 가져오는 방법. _ 단일 기기 타겟팅 등에서 사용.
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener{ task->
                if(task.isSuccessful){
                    val token = task.result
                    Log.d("FIREBASE_CURRENT_TOKEN****************", token.toString())
                }
            }
    }

}