package com.example.response_auth

data class RefreshRequest(
    val refreshToken: String
)

data class RefreshResponse(
    val success: Boolean,
    val message: String,
    val data: TokenData
)

data class TokenData(
    val accessToken: String
)
