package com.harissabil.zakatkuy.di

import android.content.Context
import com.harissabil.zakatkuy.BuildConfig
import com.harissabil.zakatkuy.core.network.ConnectivityChecker
import com.harissabil.zakatkuy.data.gemini.GoldPriceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoldPriceModule {
    private const val BASE_URL = "https://logam-mulia-api.vercel.app/"

    @Provides
    @Singleton
    @Named("gold_price_logging_interceptor")
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
    @Named("gold_price_online_interceptor")
    fun provideOnlineInterceptor(): Interceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val maxAge = 60 * 60
        response
            .newBuilder()
            .header(CACHE_CONTROL, "public, max-age=$maxAge")
            .removeHeader(PRAGMA)
            .build()
    }

    @Provides
    @Singleton
    @Named("gold_price_offline_interceptor")
    fun provideOfflineInterceptor(@ApplicationContext mContext: Context): Interceptor =
        Interceptor { chain ->
            var request = chain.request()
            val connectivityChecker = ConnectivityChecker()
            val isConnectivityAvailable = connectivityChecker(mContext)
            if (!isConnectivityAvailable) {
                val maxStale = 60 * 60 * 24 * 7
                request = request
                    .newBuilder()
                    .header(CACHE_CONTROL, "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader(PRAGMA)
                    .build()
            }
            chain.proceed(request)
        }

    @Provides
    @Singleton
    @Named("gold_price_cache")
    fun provideCache(@ApplicationContext context: Context): Cache =
        Cache(context.cacheDir, CACHE_SIZE)

    @Provides
    @Singleton
    @Named("gold_price_okhttp")
    fun provideOkHttpClient(
        @Named("gold_price_logging_interceptor") httpLoggingInterceptor: HttpLoggingInterceptor,
        @Named("gold_price_online_interceptor") internetInterceptor: Interceptor,
        @Named("gold_price_offline_interceptor") offlineInterceptor: Interceptor,
        @Named("gold_price_cache") cache: Cache,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(offlineInterceptor)
            .addNetworkInterceptor(internetInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .cache(cache)
            .build()
    }

    @Provides
    @Singleton
    @Named("gold_price_retrofit")
    fun provideRetrofit(
        @Named("gold_price_okhttp") okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesGoldPriceService(
        @Named("gold_price_retrofit") retrofit: Retrofit,
    ): GoldPriceService = retrofit.create(GoldPriceService::class.java)
}

private const val CACHE_SIZE: Long = (50 * 1024 * 1024).toLong()
private const val CACHE_CONTROL = "Cache-Control"
private const val PRAGMA = "Pragma"