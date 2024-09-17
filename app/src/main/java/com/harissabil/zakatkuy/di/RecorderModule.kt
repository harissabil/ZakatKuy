package com.harissabil.zakatkuy.di

import com.harissabil.zakatkuy.core.recorder.AndroidAudioRecorder
import com.harissabil.zakatkuy.core.recorder.AudioRecorder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecorderModule {

    @Binds
    @Singleton
    abstract fun bindAudioRecorder(
        androidAudioRecorder: AndroidAudioRecorder,
    ): AudioRecorder
}