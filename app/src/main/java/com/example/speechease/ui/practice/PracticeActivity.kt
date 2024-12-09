package com.example.speechease.ui.practice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.databinding.ActivityPracticeBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.adapter.PracticeAdapter
import com.example.speechease.ui.login.LoginActivity

class PracticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeBinding
    private lateinit var userPreference: UserPreference
    private lateinit var adapter: PracticeAdapter

    private val viewModel: PracticeViewModel by viewModels {
        PracticeViewModelFactory(
            Injection.provideContentRepository(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this)

        setupRecyclerView()
        observeViewModel()
        viewModel.fetchPracticeList()
    }

    private fun setupRecyclerView() {
        adapter = PracticeAdapter(userPreference)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        viewModel.practiceList.observe(this) { practiceList ->
            adapter.submitList(practiceList)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()

                if (errorMessage.contains("authentication", ignoreCase = true)) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.practiceList.observe(this) { practiceList ->
            Log.d("PracticeActivity", "Received ${practiceList?.size} items")
            adapter.submitList(practiceList)
        }

    }
}