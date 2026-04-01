package com.example.response_auth

import com.google.gson.annotations.SerializedName

data class Tokens(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String
)
