package com.example.response_auth

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data: DataLogin
)
