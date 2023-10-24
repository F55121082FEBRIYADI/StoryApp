package com.dicoding.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.preference.UserModel
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSessionToken(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) =
        repository.loginUser(email, password)
}

