package com.dicoding.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.api.ResultState
import com.dicoding.storyapp.data.api.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.adapter.StoryAdapter
import com.dicoding.storyapp.ui.addstory.AddStoryActivity
import com.dicoding.storyapp.ui.detail.DetailStoryActivity
import com.dicoding.storyapp.ui.login.LoginActivity
import com.dicoding.storyapp.utils.Extra.EXTRA_DETAIL
import com.dicoding.storyapp.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }
    private val mAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        getStories()
    }

    private fun setupUI() {
        loginCheck()

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.topAppBar.setOnMenuItemClickListener { handleMenuItemClick(it) }
    }

    private fun loginCheck() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                getStories()
            }
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.logOut -> {
            showLogoutDialog()
            true
        }
        R.id.menu_setting -> {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            true
        }
        else -> false
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.logout))
            setMessage(getString(R.string.logout_reminder))
            setPositiveButton(getString(R.string.logout)) { _, _ -> viewModel.logout() }
            setNegativeButton(getString(R.string.cancel)) { _, _ -> /* Do Nothing */ }
            create()
            show()
        }
    }

    private fun getStories() {
        viewModel.getStories().observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> handleSuccess(result.data.listStory)
                is ResultState.Error -> handleError(result.error)
            }
        }
    }

    private fun handleSuccess(storyItem: List<ListStoryItem>) {
        if (storyItem.isNotEmpty()) {
            binding.rvListStory.visibility = View.VISIBLE
            setupRecyclerView()
            mAdapter.submitList(storyItem)
        } else {
            binding.rvListStory.visibility = View.INVISIBLE
        }
        showLoading(false)
    }

    private fun setupRecyclerView() {
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        binding.rvListStory.setHasFixedSize(true)
        binding.rvListStory.adapter = mAdapter

        mAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                // Lakukan tindakan yang diperlukan saat item diklik di sini
                getDetailStory(data)
            }
        })
    }

    private fun getDetailStory(storyItem: ListStoryItem) {
        val intent = Intent(this, DetailStoryActivity::class.java)
        intent.putExtra(EXTRA_DETAIL, storyItem.id)
        startActivity(intent)
    }

    private fun handleError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        showLoading(false)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
