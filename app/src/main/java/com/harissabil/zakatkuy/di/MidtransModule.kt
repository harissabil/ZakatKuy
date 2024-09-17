package com.harissabil.zakatkuy.di

import com.harissabil.zakatkuy.BuildConfig
import com.harissabil.zakatkuy.data.midtrans.ApiService
import com.harissabil.zakatkuy.data.midtrans.MidtransRepository
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
object MidtransModule {
    @Provides
    @Singleton
    @Named("midtrans_logging")
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
    @Named("midtrans_api_service")
    fun provideOkHttpClient(
        @Named("midtrans_logging") httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("midtrans_retrofit")
    fun provideRetrofit(
        @Named("midtrans_api_service") okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.sandbox.midtrans.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @Named("midtrans_api_service")
    fun providesMidtransService(
        @Named("midtrans_retrofit") retrofit: Retrofit,
    ): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesMidtransRepository(
        @Named("midtrans_api_service") apiService: ApiService,
    ) = MidtransRepository(apiService)
}