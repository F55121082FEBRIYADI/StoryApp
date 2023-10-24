package com.dicoding.storyapp.ui.detail

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class DetailStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun detailStory(id: String) = repository.detailStories(id)
}