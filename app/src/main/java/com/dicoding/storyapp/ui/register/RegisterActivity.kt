package com.dicoding.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.api.ResultState
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.ui.login.LoginActivity
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.utils.animateVisibility

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        setupAction()
        playAnimation()
        setActions()
    }

    private fun setupAction() {
        binding.register.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                name.isEmpty() -> showError(binding.nameEditText, R.string.name_required)
                email.isEmpty() -> showError(binding.emailEditText, R.string.email_required)
                password.isEmpty() -> showError(binding.passwordEditText, R.string.password_required)
                else -> register(name, email, password)
            }
        }
    }

    private fun register(name: String, email: String, password: String) {
        viewModel.register(name, email, password).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> showRegistrationSuccess()
                is ResultState.Error -> handleRegistrationError(result.error)
            }
        }
    }

    private fun showRegistrationSuccess() {
        showLoading(false)
        AlertDialog.Builder(this).apply {
            setTitle("Congrats!")
            setMessage("You have successfully registered! 🎉")
            setPositiveButton("Next") { _, _ -> startLoginActivity() }
            create()
            show()
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun handleRegistrationError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        showLoading(false)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivSignup, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val textName = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val textEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val textPassword =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val textEditName =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val textEditEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val textEditPassword =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.register, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.title, View.ALPHA, 1f).setDuration(500)

        val name = AnimatorSet().apply {
            playTogether(textName, textEditName)
        }
        val email = AnimatorSet().apply {
            playTogether(textEmail, textEditEmail)
        }
        val password = AnimatorSet().apply {
            playTogether(textPassword, textEditPassword)
        }
        AnimatorSet().apply {
            playSequentially(title, name, email, password, button)
            start()
        }
    }

    private fun setActions() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        val viewsToDisable = listOf(
            binding.edRegisterName, binding.edRegisterEmail,
            binding.edRegisterPassword, binding.register
        )

        viewsToDisable.forEach { it.isEnabled = !isLoading }
        binding.viewLoading.animateVisibility(isLoading)
    }

    private fun showError(view: EditText, @StringRes messageRes: Int) {
        view.error = getString(messageRes)
    }
}
