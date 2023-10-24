package com.dicoding.storyapp.data.repository

import androidx.lifecycle.liveData
import com.dicoding.storyapp.data.api.ResultState
import com.dicoding.storyapp.data.api.response.LoginResponse
import com.dicoding.storyapp.data.api.response.RegisterResponse
import com.dicoding.storyapp.data.api.retrofit.ApiService
import com.dicoding.storyapp.preference.UserModel
import com.dicoding.storyapp.preference.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> = userPreference.getSession()

    suspend fun logout() {
        userPreference.logout()
    }

    fun registerUser(name: String, email: String, password: String) = liveData<ResultState<RegisterResponse>> {
        emit(ResultState.Loading)
        try {
            val client = apiService.register(name, email, password)
            emit(ResultState.Success(client))
        } catch (e: HttpException) {
            emit(ResultState.Error("Registration Failed"))
        }
    }

    fun loginUser(email: String, password: String) = liveData<ResultState<LoginResponse>> {
        emit(ResultState.Loading)
        try {
            val client = apiService.login(email, password)
            emit(ResultState.Success(client))
        } catch (e: HttpException) {
            val errorBody = parseErrorBody<LoginResponse>(e)
            emit(ResultState.Error(errorBody.message ?: "Login Failed"))
        }
    }

    private inline fun <reified T> parseErrorBody(e: HttpException): T {
        val jsonInString = e.response()?.errorBody()?.string()
        return Gson().fromJson(jsonInString, T::class.java)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
