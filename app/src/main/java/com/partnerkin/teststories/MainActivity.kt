package com.partnerkin.teststories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.partnerkin.teststories.databinding.ActivityMainBinding
import com.partnerkin.teststories.adapters.StoryAdapter
import com.partnerkin.teststories.utils.AndroidUtil.Companion.pxFromDp
import com.partnerkin.teststories.utils.AndroidUtil.Companion.setCornerRadiusOfView
import com.partnerkin.teststories.utils.getStories
import com.partnerkin.teststories.views.listeners.StoryCompletionListener

class MainActivity : AppCompatActivity(), StoryCompletionListener {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            val adapter = StoryAdapter(this@MainActivity)
            testRV.adapter = adapter
            testRV.hasFixedSize()
            testRV.setCornerRadiusOfView(10f.pxFromDp(context = applicationContext).toFloat())
            val snapHelper : SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(testRV)

            adapter.setData(getStories())
        }
    }

    override fun onPause() {
        super.onPause()
        binding.testRV.changePlayingState(false)
    }

    override fun onResume() {
        super.onResume()
        binding.testRV.changePlayingState(true)
    }

    override fun onComplete() {
        binding.apply {
            val position = testRV.findCurrentVideoPosition()
            testRV.smoothScrollToPosition(position + 1)
        }
    }

}