package com.cybercute.share.data.auth.model

data class SignedInResponse(
    val userId: String,
    val userName: String?,
    val email: String?,
    val profilePictureUrl: String?,
)