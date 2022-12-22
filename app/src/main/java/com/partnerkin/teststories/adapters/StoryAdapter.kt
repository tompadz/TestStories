package com.partnerkin.teststories.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.partnerkin.teststories.databinding.ItemStoryBinding
import com.partnerkin.teststories.models.StoryInfo
import com.partnerkin.teststories.models.StoryMedia
import com.partnerkin.teststories.views.listeners.StoryCompletionListener
import com.partnerkin.teststories.views.VideoPlayerEventListener

class StoryAdapter(val completionListener : StoryCompletionListener) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private var stories = mutableListOf<StoryInfo>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data:List<StoryInfo>) {
        stories.addAll(data)
        notifyDataSetChanged()
    }

    inner class StoryViewHolder(private val binding : ItemStoryBinding) : RecyclerView.ViewHolder(binding.root),
        VideoPlayerEventListener {

        private var item: StoryMedia? = null

        fun bind(storyMedia : List<StoryMedia>) {
            binding.apply {
                item = storyMedia[0]
                storyView.setStoryCompletionListener(completionListener)
                storyView.setStories(storyMedia)
            }
        }

        override fun onPrePlay(player : ExoPlayer) {
            binding.storyView.showPreview()
            with(player) {
                binding.storyView.loadFirstVideo(this)
                binding.storyView.exoPlayer = this
            }
        }

        override fun onPlayCanceled() {
            binding.storyView.exoPlayer = null
            binding.storyView.showPreview()
        }

        override fun onPlay() {
            itemView.postDelayed({
                if (binding.storyView.getPlayer() != null) {
                   binding.storyView.showVideo()
                }
            }, DELAY_BEFORE_HIDE_THUMBNAIL)
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
        val item = stories[position]
        holder.bind(item.stories)
    }

    override fun getItemCount() : Int = stories.size

    companion object {
        private const val DELAY_BEFORE_HIDE_THUMBNAIL = 300L
    }

}