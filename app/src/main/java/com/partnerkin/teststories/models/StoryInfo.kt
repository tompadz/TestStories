package com.partnerkin.teststories.models

data class StoryInfo(
    val stories:List<StoryMedia>
)

data class StoryMedia(
    val url:String,
    val preview:String,
    val preview_low:String,
)
