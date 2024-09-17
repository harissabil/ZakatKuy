package com.harissabil.zakatkuy.di

import com.harissabil.zakatkuy.data.gemini.GeminiClient
import com.harissabil.zakatkuy.data.gemini.GoldPriceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GeminiClientModule {

    @Provides
    fun provideGeminiClient(
        goldPriceService: GoldPriceService
    ): GeminiClient {
        return GeminiClient(
            goldPriceService
        )
    }
}