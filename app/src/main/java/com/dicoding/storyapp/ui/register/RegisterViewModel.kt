package com.dicoding.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.api.ResultState
import com.dicoding.storyapp.data.api.response.RegisterResponse
import com.dicoding.storyapp.data.repository.UserRepository

class RegisterViewModel (private val repository: UserRepository) : ViewModel() {
    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<ResultState<RegisterResponse>> = repository.registerUser(name, email, password)
}