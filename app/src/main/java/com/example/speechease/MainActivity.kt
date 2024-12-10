package com.example.speechease

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.speechease.data.pref.UserModel
import com.example.speechease.data.repository.UserRepository
import com.example.speechease.databinding.ActivityMainBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.guide.GuideFragment
import com.example.speechease.ui.home.HomeFragment
import com.example.speechease.ui.profile.ProfileFragment
import com.example.speechease.ui.progress.ProgressFragment
import com.example.speechease.ui.worker.SaveSessionWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val userRepository: UserRepository by lazy {
        Injection.provideRepository(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = Injection.provideRepository(this)
        lifecycleScope.launch {
            val user = userRepository.getSession().first()
            Log.d("MainActivity", "User session: $user")

            if (savedInstanceState == null) {
                loadFragment(HomeFragment())
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_guide -> loadFragment(GuideFragment())
                R.id.nav_progress -> loadFragment(ProgressFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }

        val saveSessionRequest = PeriodicWorkRequestBuilder<SaveSessionWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "saveSession",
            ExistingPeriodicWorkPolicy.UPDATE,
            saveSessionRequest
        )

        val workInfoLiveData = WorkManager.getInstance(this).getWorkInfoByIdLiveData(saveSessionRequest.id)
        workInfoLiveData.observe(this) { workInfo ->
            if (workInfo != null) {
                Log.d("MainActivity", "WorkInfo state: ${workInfo.state}")
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Log.d("MainActivity", "SaveSessionWorker has successfully saved the session.")
                } else if (workInfo.state == WorkInfo.State.FAILED) {
                    Log.e("MainActivity", "SaveSessionWorker failed to save the session.")
                }
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

    private fun loadFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id)
        if (currentFragment != fragment) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerView.id, fragment)
                .commit()
        }
    }
}