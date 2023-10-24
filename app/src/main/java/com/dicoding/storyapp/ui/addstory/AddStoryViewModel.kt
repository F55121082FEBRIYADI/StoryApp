package com.dicoding.storyapp.ui.addstory

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun uploadStory(fileImage: File, description: String) =
        repository.uploadStory(fileImage, description)
}