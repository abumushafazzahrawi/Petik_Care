package com.example.petikcare.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petikcare.databinding.ActivitySplashBinding
import com.example.retrofit.ApiConfig
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    companion object{
        const val PREF_NAME = "petikCare"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        },2000)
    }

    private fun checkUserSession() {
        val sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", null)

        android.util.Log.d("PETIK_CARE", "Token yang terbaca di Splash: $token")

        if (!token.isNullOrEmpty()) {
            if (isOnline()) {
                // Jika token ada (sudah login), langsung ke MainActivity
                android.util.Log.d("PETIK_CARE", "Token ada, langsung ke MainActivity")
                checkTokenServer(token)

            } else {
                Toast.makeText(this, "Mode offline", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } else {
            // Jika token tidak ada (belum login), ke halaman login
            android.util.Log.d("PETIK_CARE", "Token tidak ada, ke halaman login")
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
            }
        }

    private fun checkTokenServer(token: String) {
        lifecycleScope.launch {
            try {
                val response = ApiConfig.getApiService(applicationContext).getAllComplaint()

                if (response.isSuccessful) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                } else {
                    if (response.code() == 401) {
                        goToLogin()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@SplashActivity, "Mode offline", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()

            }
        }
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network)?: return false
        return  capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    private fun goToLogin() {
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        finish()
    }
}