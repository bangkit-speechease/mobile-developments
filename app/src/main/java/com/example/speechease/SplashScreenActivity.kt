package com.example.speechease

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.speechease.data.pref.UserModel
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.repository.UserRepository
import com.example.speechease.databinding.ActivitySplashScreenBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var userPreference: UserPreference
    private val userRepository: UserRepository by lazy {
        Injection.provideRepository(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this) // Inisialisasi dengan context

        // Memuat animasi zoom-in
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        binding.logoImageView.startAnimation(zoomInAnimation)

        lifecycleScope.launch {
            val session = userPreference.getSession().first()
            if (session.isLogin && session.token != null) {
                // Sesi valid, arahkan ke MainActivity
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            } else {
                // Sesi tidak valid, lanjutkan ke WelcomeActivity
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 3000)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lifecycleScope.launch {
            val user = userRepository.getSession().first()
            outState.putString("userId", user.userId)
            outState.putString("name", user.name)
            outState.putString("email", user.email)
            outState.putString("token", user.token)
            outState.putBoolean("isLogin", user.isLogin)
            Log.d("MainActivity", "onSaveInstanceState: Saving session: $user")
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val userId = savedInstanceState.getString("userId", "")
        val name = savedInstanceState.getString("name", "")
        val email = savedInstanceState.getString("email", "")
        val token = savedInstanceState.getString("token", "")
        val isLogin = savedInstanceState.getBoolean("isLogin", false)
        val user = UserModel(userId, name, email, token, isLogin)
        lifecycleScope.launch {
            userRepository.saveSession(user)
            Log.d("MainActivity", "onRestoreInstanceState: Restoring session: $user")
        }
    }
}