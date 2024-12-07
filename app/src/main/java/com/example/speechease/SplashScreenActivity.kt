package com.example.speechease

import android.annotation.SuppressLint
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

@SuppressLint("CustomSplashScreen")
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

        // Memuat animasi zoom-in
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        binding.logoImageView.startAnimation(zoomInAnimation)

        // Delay splash screen selama 3 detik (3000 ms)
        Handler(Looper.getMainLooper()).postDelayed({
            // Intent menuju WelcomeActivity
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish() // Menghapus activity ini dari back stack
        }, 3000) // 3000 ms = 3 detik
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val userRepository = Injection.provideRepository(this@SplashScreenActivity)
            val user: UserModel = userRepository.getUser().first()

            if (user.isLogin && user.token.isNotEmpty()) {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            } else {
                Log.d("SplashScreenActivity", "Pengguna belum login atau token kosong.")
            }
        }
    }
}