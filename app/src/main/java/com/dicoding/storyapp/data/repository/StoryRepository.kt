package com.dicoding.storyapp.data.repository

import androidx.lifecycle.liveData
import com.dicoding.storyapp.data.api.ResultState
import com.dicoding.storyapp.data.api.response.DetailStoryResponse
import com.dicoding.storyapp.data.api.response.GetAllStoryResponse
import com.dicoding.storyapp.data.api.response.StoryUploadResponse
import com.dicoding.storyapp.data.api.retrofit.ApiConfig
import com.dicoding.storyapp.preference.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(private val userPreference: UserPreference) {
    fun getAllStories() = liveData<ResultState<GetAllStoryResponse>> {
        emit(ResultState.Loading)
        try {
            val user = userPreference.getSession().first()
            val apiService = ApiConfig.getApiService(user.token)
            val client = apiService.getAllStories()
            emit(ResultState.Success(client))
        } catch (e: HttpException) {
            val errorBody = parseErrorBody<GetAllStoryResponse>(e)
            emit(ResultState.Error(errorBody.message ?: "Unknown error"))
        }
    }

    fun detailStories(id: String) = liveData<ResultState<DetailStoryResponse>> {
        emit(ResultState.Loading)
        try {
            val user = userPreference.getSession().first()
            val apiService = ApiConfig.getApiService(user.token)
            val client = apiService.detailStory(id)
            emit(ResultState.Success(client))
        } catch (e: HttpException) {
            val errorBody = parseErrorBody<DetailStoryResponse>(e)
            emit(ResultState.Error(errorBody.message ?: "Unknown error"))
        }
    }

    fun uploadStory(imageFile: File, description: String) = liveData<ResultState<StoryUploadResponse>> {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)
        try {
            val user = userPreference.getSession().first()
            val apiService = ApiConfig.getApiService(user.token)
            val successResponse = apiService.uploadStory(multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = parseErrorBody<StoryUploadResponse>(e)
            emit(ResultState.Error(errorBody.message))
        }
    }

    private inline fun <reified T> parseErrorBody(e: HttpException): T {
        val jsonInString = e.response()?.errorBody()?.string()
        return Gson().fromJson(jsonInString, T::class.java)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(userPreference: UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(userPreference)
            }.also { instance = it }
    }
}

