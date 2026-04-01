package com.example.response_auth

import com.google.gson.annotations.SerializedName

data class DataLogin (

    @SerializedName("tokens")
    val tokens: Tokens,

    @SerializedName("user")
    val user: User
)