package com.harissabil.zakatkuy.data.groq

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class GroqRepository @Inject constructor(
    private val groqApiService: GroqApiService,
) {

    suspend fun uploadAudio(file: MultipartBody.Part) = groqApiService.uploadAudio(
        model = "whisper-large-v3".toRequestBody("text/plain".toMediaTypeOrNull()),
        file = file,
        language = "id".toRequestBody("text/plain".toMediaTypeOrNull()),
        responseFormat = "verbose_json".toRequestBody("text/plain".toMediaTypeOrNull())
    )
}