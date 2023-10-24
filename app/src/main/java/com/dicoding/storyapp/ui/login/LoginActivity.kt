package com.dicoding.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.api.ResultState
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.preference.UserModel
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.ui.register.RegisterActivity
import com.dicoding.storyapp.utils.Extra.EXTRA_TOKEN
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.utils.animateVisibility
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.login.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty()) {
                showError(binding.emailEditText, R.string.email_required)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                showError(binding.passwordEditText, R.string.password_required)
                return@setOnClickListener
            }

            login(email, password)
        }

        playAnimation()
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> handleLoginSuccess(result.data.loginResult.token)
                is ResultState.Error -> handleLoginError()
            }
        }
    }

    private fun handleLoginSuccess(token: String) {
        viewModel.saveSessionToken(UserModel(binding.emailEditText.text.toString(), token))
        showLoading(false)
        showSuccessDialog(token)
    }

    private fun showSuccessDialog(token: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Congrats!")
            setMessage("You have successfully logged in! 🎉")
            setPositiveButton("Next") { _, _ ->
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(EXTRA_TOKEN, token)
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun handleLoginError() {
        showErrorSnackbar(getString(R.string.login_error_message))
        showLoading(false)
    }

    private fun showError(view: EditText, @StringRes messageRes: Int) {
        view.error = getString(messageRes)
        view.requestFocus()
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            edLoginEmail.isEnabled = !isLoading
            edLoginPassword.isEnabled = !isLoading
            login.isEnabled = !isLoading

            viewLoading.animateVisibility(isLoading)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val textEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val textPassword =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val textEditEmail =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val textEditPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.login, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        val email = AnimatorSet().apply {
            playTogether(textEmail, textEditEmail)
        }
        val password = AnimatorSet().apply {
            playTogether(textPassword, textEditPassword)
        }
        AnimatorSet().apply {
            playSequentially(title, email, password, button, btnRegister)
            start()
        }
    }
}

