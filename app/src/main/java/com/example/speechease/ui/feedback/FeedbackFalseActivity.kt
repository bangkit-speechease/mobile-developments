package com.example.speechease.ui.feedback

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.speechease.databinding.ActivityFeedbackFalseBinding
import com.example.speechease.ui.practice.PracticeActivity

class FeedbackFalseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackFalseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackFalseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExit.setOnClickListener {
            val intent = Intent(this, PracticeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}