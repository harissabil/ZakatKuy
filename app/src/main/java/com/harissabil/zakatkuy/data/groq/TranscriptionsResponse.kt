package com.harissabil.zakatkuy.data.groq

import com.google.gson.annotations.SerializedName

data class TranscriptionsResponse(

    @field:SerializedName("duration")
    val duration: Float? = null,

    @field:SerializedName("task")
    val task: String? = null,

    @field:SerializedName("x_groq")
    val xGroq: XGroq? = null,

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("text")
    val text: String? = null,

    @field:SerializedName("segments")
    val segments: List<SegmentsItem?>? = null,
)

data class XGroq(

    @field:SerializedName("id")
    val id: String? = null,
)

data class SegmentsItem(

    @field:SerializedName("start")
    val start: Int? = null,

    @field:SerializedName("temperature")
    val temperature: Int? = null,

    @field:SerializedName("avg_logprob")
    val avgLogprob: Float? = null,

    @field:SerializedName("no_speech_prob")
    val noSpeechProb: Float? = null,

    @field:SerializedName("end")
    val end: Float? = null,

    @field:SerializedName("tokens")
    val tokens: List<Int?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("text")
    val text: String? = null,

    @field:SerializedName("seek")
    val seek: Int? = null,

    @field:SerializedName("compression_ratio")
    val compressionRatio: Float? = null,
)
