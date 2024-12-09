package com.example.speechease.ui.feedback

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.speechease.databinding.ActivityFeedbackTrueBinding
import com.example.speechease.ui.practice.PracticeActivity

class FeedbackTrueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackTrueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackTrueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExit.setOnClickListener {
            val intent = Intent(this, PracticeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}