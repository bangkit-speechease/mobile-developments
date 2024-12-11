package com.example.speechease.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.speechease.data.repository.UserRepository
import com.example.speechease.data.response.ErrorResponse
import com.example.speechease.databinding.ActivitySignupBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.login.LoginActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = Injection.provideRepository(this)
        viewModel = SignupViewModel(userRepository)

        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            lifecycleScope.launch {
                binding.progressBar.visibility = View.VISIBLE

                try {
                    val response = viewModel.register(name, email, password)
                    if (!response.error) {
                        Toast.makeText(
                            this@SignupActivity,
                            "Daftar berhasil!",
                            Toast.LENGTH_SHORT
                        ).show()

                        AlertDialog.Builder(this@SignupActivity).apply {
                            setTitle("Yeah!")
                            setMessage("Akun dengan email $email sudah jadi nih.")
                            setPositiveButton("Lanjut") { _, _ ->
                                startActivity(
                                    Intent(
                                        this@SignupActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                finish()
                            }
                            create()
                            show()
                        }
                    } else {
                        Toast.makeText(
                            this@SignupActivity,
                            "Daftar gagal: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse.message
                    Toast.makeText(
                        this@SignupActivity,
                        "Daftar gagal: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Log.e("SignupActivity", "Error saat daftar", e)
                    Toast.makeText(
                        this@SignupActivity,
                        "Terjadi error saat daftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}