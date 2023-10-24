package com.dicoding.storyapp.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.api.ResultState
import com.dicoding.storyapp.data.api.response.Story
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.utils.Extra.EXTRA_DETAIL
import com.dicoding.storyapp.utils.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel by viewModels<DetailStoryViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_DETAIL).toString()
        viewModel.detailStory(storyId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> showStoryDetails(result.data.story)
                is ResultState.Error -> showError(result.error)
            }
        }
    }

    private fun showStoryDetails(story: Story) {
        val binding = this.binding

        Glide.with(this)
            .load(story.photoUrl)
            .skipMemoryCache(true)
            .into(binding.ivDetailPhoto)

        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
        showLoading(false)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        showLoading(false)
    }
}
