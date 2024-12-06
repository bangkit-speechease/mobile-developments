package com.example.speechease

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.speechease.databinding.ActivitySplashScreenBinding
import com.example.speechease.ui.welcome.WelcomeActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

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
}
