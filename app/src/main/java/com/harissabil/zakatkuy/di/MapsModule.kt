package com.harissabil.zakatkuy.di

import android.content.Context
import com.harissabil.zakatkuy.BuildConfig
import com.harissabil.zakatkuy.core.network.ConnectivityChecker
import com.harissabil.zakatkuy.data.maps.MapsRepository
import com.harissabil.zakatkuy.data.maps.api.MapsService
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
object MapsModule {

    @Provides
    @Singleton
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
    @Named(ONLINE_INTERCEPTOR)
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
    @Named(OFFLINE_INTERCEPTOR)
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
    fun provideCache(@ApplicationContext context: Context): Cache =
        Cache(context.cacheDir, CACHE_SIZE)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        @Named(ONLINE_INTERCEPTOR) internetInterceptor: Interceptor,
        @Named(OFFLINE_INTERCEPTOR) offlineInterceptor: Interceptor,
        cache: Cache,
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
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesMapDirectionsService(
        retrofit: Retrofit,
    ): MapsService = retrofit.create(MapsService::class.java)

    @Provides
    @Singleton
    fun providesMapsRepository(
        mapsService: MapsService,
    ) = MapsRepository(mapsService)
}

private const val CACHE_SIZE: Long = (50 * 1024 * 1024).toLong()
private const val ONLINE_INTERCEPTOR = "online_interceptor"
private const val OFFLINE_INTERCEPTOR = "offline_interceptor"
private const val CACHE_CONTROL = "Cache-Control"
private const val PRAGMA = "Pragma"