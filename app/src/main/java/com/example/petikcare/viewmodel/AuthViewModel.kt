package com.example.petikcare.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.response_auth.LoginRequest
import com.example.response_auth.ResponseLogin
import com.example.retrofit.ApiConfig
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<ResponseLogin>()
    val loginResult: LiveData<ResponseLogin> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loginUser(context: Context, email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = ApiConfig.getApiService(context).loginUser(request)
                if (response.isSuccessful) {
                    _loginResult.value = response.body()
                } else {
                    _errorMessage.value = "Login gagal: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "KOneksi error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}