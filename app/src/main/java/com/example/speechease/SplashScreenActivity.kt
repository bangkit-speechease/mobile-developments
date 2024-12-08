package com.example.speechease

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.speechease.data.pref.UserModel
import com.example.speechease.databinding.ActivitySplashScreenBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Memuat animasi zoom-in
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        binding.logoImageView.startAnimation(zoomInAnimation)


        lifecycleScope.launch {
            val userRepository = Injection.provideRepository(this@SplashScreenActivity)
            val user: UserModel = userRepository.getUser().first()

            if (user.isLogin && user.token.isNotEmpty()) {
                // Langsung menuju MainActivity jika sudah login
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            } else {
                // Lanjutkan ke WelcomeActivity setelah delay jika belum login
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 3000)
            }
        }
    }
}