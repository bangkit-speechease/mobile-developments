package com.example.speechease.ui.feedback

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.speechease.databinding.ActivityFeedbackFalseBinding
import com.example.speechease.ui.practice.PracticeActivity
import com.example.speechease.ui.practicedetail.PracticeDetailActivity

class FeedbackFalseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackFalseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackFalseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnRetry.setOnClickListener {
            val intent = Intent(this, PracticeDetailActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnExit.setOnClickListener {
            val intent = Intent(this, PracticeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}