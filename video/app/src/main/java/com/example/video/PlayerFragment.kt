package com.example.video

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import com.example.video.databinding.FragmentPlayerBinding
import kotlin.math.abs


class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var binding : FragmentPlayerBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}