package com.example.speechease.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.speechease.MainActivity
import com.example.speechease.data.pref.UserModel
import com.example.speechease.databinding.ActivityWelcomeBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.login.LoginActivity
import com.example.speechease.ui.signup.SignupActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val userRepository = Injection.provideRepository(this@WelcomeActivity)
            val user: UserModel = userRepository.getUser().first()

            if (user.isLogin && user.token.isNotEmpty()) {
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                finish()
            } else {
                Log.d("WelcomeActivity", "Pengguna belum masuk atau token kosong.")
            }
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivMic, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.tvDescription, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(desc, together)
            start()
        }
    }
}