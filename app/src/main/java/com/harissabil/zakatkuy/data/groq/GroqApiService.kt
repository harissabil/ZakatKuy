package com.harissabil.zakatkuy.data.groq

import com.harissabil.zakatkuy.BuildConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GroqApiService {

    @Multipart
    @POST("openai/v1/audio/transcriptions")
    suspend fun uploadAudio(
        @Header("Authorization") apiKey: String = "Bearer ${BuildConfig.GROQ_API_KEY}",
        @Part("model") model: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("language") language: RequestBody,
        @Part("response_format") responseFormat: RequestBody,
    ): Response<TranscriptionsResponse>
}