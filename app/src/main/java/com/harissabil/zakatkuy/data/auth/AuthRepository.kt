package com.cybercute.share.data.auth

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.cybercute.share.data.auth.model.SignedInResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.harissabil.zakatkuy.data.Resource
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.CancellationException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val credentialManager: CredentialManager,
    private val auth: FirebaseAuth,
) {
    suspend fun registerWithEmail(email: String, password: String): Resource<SignedInResponse> =
        try {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            Resource.Success(
                data = user?.run {
                    SignedInResponse(
                        userId = uid,
                        userName = displayName,
                        email = email,
                        profilePictureUrl = photoUrl?.toString(),
                    )
                }
            )
        } catch (e: Exception) {
            Timber.e("registerWithEmail: ${e.message}")
            e.printStackTrace()
            if (e is CancellationException) throw e
            Resource.Error(
                data = null,
                message = e.message
            )
        }

    suspend fun signInWithEmail(email: String, password: String): Resource<SignedInResponse> =
        try {
            val user = auth.signInWithEmailAndPassword(email, password).await().user
            Resource.Success(
                data = user?.run {
                    SignedInResponse(
                        userId = uid,
                        userName = displayName,
                        email = email,
                        profilePictureUrl = photoUrl?.toString(),
                    )
                }
            )
        } catch (e: Exception) {
            Timber.e("signInWithEmail: ${e.message}")
            e.printStackTrace()
            if (e is CancellationException) throw e
            Resource.Error(
                data = null,
                message = e.message
            )
        }

    suspend fun signInWithGoogle(token: String): Resource<SignedInResponse> {

        val googleCredentials = GoogleAuthProvider.getCredential(token, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            Resource.Success(
                data = user?.run {
                    SignedInResponse(
                        userId = uid,
                        userName = displayName,
                        email = email,
                        profilePictureUrl = photoUrl?.toString(),
                    )
                }
            )
        } catch (e: Exception) {
            Timber.e("signInWithIntent: ${e.message}")
            e.printStackTrace()
            if (e is CancellationException) throw e
            Resource.Error(
                data = null,
                message = e.message
            )
        }
    }

    suspend fun signOut(): Resource<Boolean> {
        return try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            auth.signOut()
            Resource.Success(data = true)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Resource.Error(
                data = false,
                message = e.message
            )
        }
    }

    fun getSignedInUser(): Resource<SignedInResponse> {
        val user = auth.currentUser
        Timber.e("getSignInUser: ${user?.displayName}")
        return if (user != null) {
            Resource.Success(
                data = SignedInResponse(
                    userId = user.uid,
                    userName = user.displayName,
                    email = user.email,
                    profilePictureUrl = user.photoUrl.toString(),
                )
            )
        } else {
            Resource.Error(
                data = null,
                message = "User is not signed in."
            )
        }
    }
}