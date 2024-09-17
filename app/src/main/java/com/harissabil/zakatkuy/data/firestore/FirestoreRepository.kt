package com.harissabil.zakatkuy.data.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.firestore.models.ChatHistory
import com.harissabil.zakatkuy.data.firestore.models.ChatMessage
import com.harissabil.zakatkuy.data.firestore.models.UserData
import com.harissabil.zakatkuy.data.firestore.models.ZakatDocumentation
import com.harissabil.zakatkuy.data.firestore.models.ZakatMalHistory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class FirestoreRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @Named("chat_history") private val chatHistoryRef: CollectionReference,
    @Named("chat_message") private val chatMessageRef: CollectionReference,
    @Named("user_data") private val userDataRef: CollectionReference,
    @Named("zakat_mal_history") private val zakatMalHistoryRef: CollectionReference,
    @Named("zakat_documentation") private val zakatDocumentationRef: CollectionReference,
) {
    suspend fun getChatHistory(): Flow<Resource<List<ChatHistory>>> = callbackFlow {
        val snapshotListener =
            chatHistoryRef.whereEqualTo(
                "email", auth.currentUser?.email
            ).addSnapshotListener { snapshot, e ->
                val emergencyLocations = if (snapshot != null) {
                    val locations = snapshot.toObjects(ChatHistory::class.java)
                    Resource.Success(locations)
                } else {
                    Resource.Error(e?.message ?: "Something went wrong!")
                }
                trySend(emergencyLocations)
            }

        awaitClose {
            snapshotListener.remove()
        }
    }

    suspend fun getChatMessages(chatHistoryId: String): Resource<List<ChatMessage>> {
        return try {
            val snapshot =
                chatMessageRef.whereEqualTo("chat_history_id", chatHistoryId).get().await()
            val messages = snapshot.toObjects(ChatMessage::class.java)
            Resource.Success(messages)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

    suspend fun addChatHistoryAndMessages(
        chatHistory: ChatHistory,
        messages: List<ChatMessage>,
    ): Resource<Unit> {
        return try {
            val chatHistoryId = chatHistoryRef.add(chatHistory).await().id
            messages.forEach { message ->
                chatMessageRef.add(message.copy(chatHistoryId = chatHistoryId)).await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

    suspend fun addUserData(userData: UserData): Resource<Unit> {
        return try {
            userDataRef.add(userData.copy(email = auth.currentUser?.email)).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

    suspend fun getUserData(): Resource<UserData> {
        return try {
            val snapshot = userDataRef.whereEqualTo("email", auth.currentUser?.email).get().await()
            val userData = snapshot.toObjects(UserData::class.java).firstOrNull()
            Timber.i("userData: $userData")
            if (userData != null) {
                Resource.Success(userData)
            } else {
                Resource.Error("User data not found!", data = null)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!", data = null)
        }
    }

    suspend fun getZakatMalHistory(): Flow<Resource<List<ZakatMalHistory>>> = callbackFlow {
        val snapshotListener = zakatMalHistoryRef.whereEqualTo("email", auth.currentUser?.email)
            .addSnapshotListener { snapshot, e ->
                val zakatMalHistories = if (snapshot != null) {
                    val histories = snapshot.toObjects(ZakatMalHistory::class.java)
                    Resource.Success(histories)
                } else {
                    Resource.Error(e?.message ?: "Something went wrong!")
                }
                trySend(zakatMalHistories)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    suspend fun addZakatMalHistory(zakatMalHistory: ZakatMalHistory): Resource<Unit> {
        return try {
            zakatMalHistoryRef.add(zakatMalHistory.copy(email = auth.currentUser?.email)).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

    suspend fun getZakatDocumentation(): Flow<Resource<List<ZakatDocumentation>>> = callbackFlow {
        val snapshotListener = zakatDocumentationRef.whereEqualTo("email", auth.currentUser?.email)
            .addSnapshotListener { snapshot, e ->
                val zakatMalHistories = if (snapshot != null) {
                    val histories = snapshot.toObjects(ZakatDocumentation::class.java)
                    Resource.Success(histories)
                } else {
                    Resource.Error(e?.message ?: "Something went wrong!")
                }
                trySend(zakatMalHistories)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    suspend fun addZakatDocumentation(zakatDocumentation: ZakatDocumentation): Resource<Unit> {
        return try {
            zakatDocumentationRef.add(zakatDocumentation.copy(email = auth.currentUser?.email))
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }
}