package com.partnerkin.teststories.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.partnerkin.teststories.databinding.ItemStoryBinding
import com.partnerkin.teststories.models.StoryInfo


class StoryAdapter() : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(private val binding : ItemStoryBinding) : RecyclerView.ViewHolder(binding.root), VideoPlayerEventListener {

        private var item: StoryInfo? = null

        fun bind(videoItem: StoryInfo) {
            item = videoItem
            with(item) {
                binding.storyView.setPreview(item !!.preview)
            }
        }

        override fun onPrePlay(player : ExoPlayer) {
            binding.storyView.showPreview()
            with(player) {
                playVideo()
                binding.storyView.setPlayer(this)
            }
        }

        override fun onPlayCanceled() {
            binding.storyView.setPlayer(null)
            binding.storyView.showPreview()
        }

        override fun onPlay() {
            itemView.postDelayed({
                if (binding.storyView.getPlayer() != null) {
                   binding.storyView.showVideo()
                }
            }, DELAY_BEFORE_HIDE_THUMBNAIL)
        }

        private fun ExoPlayer.playVideo() {
            stop(true)
            val videoUrl = item?.url ?: return
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
        }
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : StoryViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder : StoryViewHolder, position : Int) {
        val item = StoryInfo(
            "https://partnerkin.com/uploads/apps/stories/e4c2c7c7e5dfd9dcb5d641d33a223102.mp4",
            "https://partnerkin.com/uploads/apps/stories/e4c2c7c7e5dfd9dcb5d641d33a223102_low.jpg"
        )
        holder.bind(item)
    }

    override fun getItemCount() : Int = 50

    companion object {
        private const val DELAY_BEFORE_HIDE_THUMBNAIL = 500L
    }

}