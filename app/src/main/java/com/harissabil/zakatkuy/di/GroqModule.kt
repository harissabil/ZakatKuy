package com.harissabil.zakatkuy.di

import com.harissabil.zakatkuy.BuildConfig
import com.harissabil.zakatkuy.data.groq.GroqApiService
import com.harissabil.zakatkuy.data.groq.GroqRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GroqModule {

    @Provides
    @Singleton
    @Named("groq_logging_interceptor")
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    @Named("groq_okhttp")
    fun provideOkHttpClient(
        @Named("groq_logging_interceptor") httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("groq_retrofit")
    fun provideRetrofit(
        @Named("groq_okhttp") okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesGroqService(
        @Named("groq_retrofit") retrofit: Retrofit,
    ): GroqApiService = retrofit.create(GroqApiService::class.java)

    @Provides
    @Singleton
    fun providesGroqRepository(
        groqApiService: GroqApiService,
    ): GroqRepository = GroqRepository(groqApiService)
}