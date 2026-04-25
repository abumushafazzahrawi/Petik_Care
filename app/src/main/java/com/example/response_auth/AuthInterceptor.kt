package com.example.response_auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import com.example.petikcare.ui.LoginActivity
import com.example.retrofit.ApiService
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class AuthInterceptor(private val context: Context): Interceptor {
    companion object {
        const val PREF_NAME = "petikCare"
        const val BASE_URL = "https://petikcare.petik.or.id/api/"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val accessToken = sharedPref.getString("ACCESS_TOKEN", null)
        val request = chain.request().newBuilder()
            .apply {
                accessToken?.let {
                    addHeader("Authorization", "Bearer $it")
                }
            }
            .build()
        val response = chain.proceed(request)

        //Kalau token expired
        if (response.code == 401) {
            response.close()
            Log.d("REFRESH_DEBUG", "401")

            val refreshToken = sharedPref.getString("REFRESH_TOKEN", null)

            Log.d("REFRESH_DEBUG", "Refresh Token : $refreshToken")

            if (refreshToken != null) {
                val newToken = refreshAccessToken(refreshToken)

                if (newToken != null) {
                    // simpan token baru
                    sharedPref.edit {
                        putString("ACCESS_TOKEN", newToken)
                    }

                    //Ulangi request dengan token baru
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $newToken")
                        .build()

                    return chain.proceed(newRequest)
                }
            }

            Log.e("REFRESH_DEBUG", "Refresh Token Gagal - Mengarahkanke Login")

            sharedPref.edit().clear().apply()

            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)

        }
        return response
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(ApiService::class.java)

            val response = api.refreshToken(RefreshRequest(refreshToken)).execute()

            if (response.isSuccessful) {
                response.body()?.data?.accessToken
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("REFRESH_DEBUG", "Error : ${e.message}")
            null
        }
    }
}