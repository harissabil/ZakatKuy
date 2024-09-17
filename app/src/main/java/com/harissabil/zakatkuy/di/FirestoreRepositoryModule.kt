package com.harissabil.zakatkuy.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.harissabil.zakatkuy.data.firestore.FirestoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirestoreRepositoryModule {

    private const val CHAT_HISTORY = "chat_history"
    private const val CHAT_MESSAGE = "chat_message"
    private const val USER_DATA = "user_data"
    private const val ZAKAT_MAL_HISTORY = "zakat_mal_history"
    private const val ZAKAT_DOCUMENTATION = "zakat_documentation"

    @Provides
    @Singleton
    @Named(CHAT_HISTORY)
    fun provideChatHistoryRef(): CollectionReference =
        Firebase.firestore.collection(CHAT_HISTORY)

    @Provides
    @Singleton
    @Named(CHAT_MESSAGE)
    fun provideChatMessageRef(): CollectionReference =
        Firebase.firestore.collection(CHAT_MESSAGE)

    @Provides
    @Singleton
    @Named(USER_DATA)
    fun provideUserDataRef(): CollectionReference =
        Firebase.firestore.collection(USER_DATA)

    @Provides
    @Singleton
    @Named(ZAKAT_MAL_HISTORY)
    fun provideZakatMalHistoryRef(): CollectionReference =
        Firebase.firestore.collection(ZAKAT_MAL_HISTORY)

    @Provides
    @Singleton
    @Named(ZAKAT_DOCUMENTATION)
    fun provideZakatDocumentationRef(): CollectionReference =
        Firebase.firestore.collection(ZAKAT_DOCUMENTATION)

    @Provides
    @Singleton
    fun provideFirestoreRepository(
        @Named(CHAT_HISTORY) chatHistoryRef: CollectionReference,
        @Named(CHAT_MESSAGE) chatMessageRef: CollectionReference,
        @Named(USER_DATA) userDataRef: CollectionReference,
        @Named(ZAKAT_MAL_HISTORY) zakatMalHistoryRef: CollectionReference,
        @Named(ZAKAT_DOCUMENTATION) zakatDocumentationRef: CollectionReference,
    ): FirestoreRepository =
        FirestoreRepository(
            auth = Firebase.auth,
            chatHistoryRef = chatHistoryRef,
            chatMessageRef = chatMessageRef,
            userDataRef = userDataRef,
            zakatMalHistoryRef = zakatMalHistoryRef,
            zakatDocumentationRef = zakatDocumentationRef
        )
}