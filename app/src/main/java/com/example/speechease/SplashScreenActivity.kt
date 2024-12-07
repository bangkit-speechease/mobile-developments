package com.example.speechease

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.pref.dataStore
import com.example.speechease.databinding.ActivitySplashScreenBinding
import com.example.speechease.ui.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)

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
}
