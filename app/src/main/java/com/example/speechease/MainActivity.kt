package com.example.speechease

import android.os.Bundle
import android.util.Log
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.speechease.databinding.ActivityMainBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.home.HomeFragment
import com.example.speechease.ui.profile.ProfileFragment
import com.example.speechease.ui.progress.ProgressFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

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
                R.id.nav_progress -> loadFragment(ProgressFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
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