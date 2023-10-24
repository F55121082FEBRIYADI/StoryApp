package com.dicoding.storyapp.data.di

import android.content.Context
import com.dicoding.storyapp.data.api.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.preference.UserPreference
import com.dicoding.storyapp.preference.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    fun provideRepository(context: Context) : UserRepository {
        val userPref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(userPref, apiService)
    }

    fun provideStoryRepository(context: Context) : StoryRepository {
        val userPref =
            UserPreference.getInstance(context.dataStore)
        return StoryRepository.getInstance(userPref)
    }

}