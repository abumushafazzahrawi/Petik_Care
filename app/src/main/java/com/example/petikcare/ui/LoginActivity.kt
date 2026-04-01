package com.example.petikcare.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.petikcare.ui.MainActivity
import com.example.petikcare.databinding.ActivityLoginBinding
import com.example.petikcare.viewmodel.AuthViewModel
import com.example.response_auth.LoginRequest
import com.example.retrofit.ApiConfig
import com.example.retrofit.ApiService
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    companion object {
        const val PREF_NAME = "petikCare"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
            viewModel.loginResult.observe(this) { body ->
                body?.let {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                    val data = it.data
                    val role = it.data.user.role
                    val name = it.data.user.name

                    val sharedfPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    sharedfPref.edit {
                        putString("ACCESS_TOKEN", data.tokens.accessToken)
                        putString("REFRESH_TOKEN", data.tokens.refreshToken)
                        putString("ROLE", role)
                        putString("USERNAME", name ?: "")
                        putBoolean("IS_LOGIN", true)
                    }

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

            viewModel.isLoading.observe(this) { isLoading ->
                binding.btnLogin.isEnabled = !isLoading
            }

            viewModel.errorMessage.observe(this) { message ->
                message?.let {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                binding.btnLogin.isEnabled = true
                return@setOnClickListener
            }

            viewModel.loginUser(this, email, password)
        }
    }
}