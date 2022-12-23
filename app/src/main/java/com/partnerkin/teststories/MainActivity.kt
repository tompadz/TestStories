package com.partnerkin.teststories

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.partnerkin.teststories.databinding.ActivityMainBinding
import com.partnerkin.teststories.adapters.StoryAdapter
import com.partnerkin.teststories.utils.AndroidUtil.Companion.pxFromDp
import com.partnerkin.teststories.utils.AndroidUtil.Companion.setCornerRadiusOfView
import com.partnerkin.teststories.utils.getStories
import com.partnerkin.teststories.views.listeners.StoryButtonClickListener
import com.partnerkin.teststories.views.listeners.StoryCompletionListener

class MainActivity : AppCompatActivity(), StoryCompletionListener, StoryButtonClickListener {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            val adapter = StoryAdapter(
                completionListener = this@MainActivity,
                buttonClickListener = this@MainActivity
            )
            testRV.adapter = adapter
            testRV.hasFixedSize()
            testRV.setCornerRadiusOfView(10f.pxFromDp(context = applicationContext).toFloat())
            val snapHelper : SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(testRV)

            adapter.setData(getStories())
        }
    }

    override fun onPause() {
        binding.testRV.changePlayingState(false)
        super.onPause()
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

    override fun onCloseClick() {
        this.onBackPressed()
    }

    override fun onCommentsClick() {
        onPause()
        val sheet = SheetStoryComments() {   //dismiss
            onResume()
        }
        sheet.show(supportFragmentManager, SheetStoryComments.TAG)
    }

    override fun onLikeClick() {

    }

    override fun onWriteCommentsClick() {
        startActivity(Intent(this, TestActivity::class.java))
    }
}