package com.example.retrofit

import com.example.petikcare.response_complaint.RespondResponse
import com.example.petikcare.response_complaint.ResponseComplaints
import com.example.petikcare.response_complaint.RespondRequest
import com.example.petikcare.response_complaint.ResponseDetailComplaint
import com.example.petikcare.response_complaint.RevertResponse
import com.example.petikcare.response_obat.GeneralResponse
import com.example.petikcare.response_obat.ObatRequest
import com.example.petikcare.response_obat.ResponseGetObat
import com.example.response_auth.LoginRequest
import com.example.response_auth.RefreshRequest
import com.example.response_auth.RefreshResponse
import com.example.response_auth.ResponseLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun loginUser(
        @Body requestBody: LoginRequest
    ): Response<ResponseLogin>

    @POST("auth/refresh")
    fun refreshToken(
        @Body request: RefreshRequest
    ): retrofit2.Call<RefreshResponse>

    @GET("complaints/lookall")
    suspend fun getAllComplaint(
    ): Response<ResponseComplaints>

    @POST("complaints/respond/{id}")
    suspend fun respondComplaint(
        @Path("id") id: String,
        @Body request: RespondRequest
    ): Response<ResponseDetailComplaint>

    @POST("complaints/revert/{id}")
    suspend fun revertComplaint(
        @Path("id") id: String
    ): Response<RevertResponse>

    @GET("medicine/lookup")
    suspend fun getAllObat(
    ): Response<ResponseGetObat>

    @POST("medicine/create")
    suspend fun createObat(
        @Body request: ObatRequest
    ): Response<GeneralResponse>
}