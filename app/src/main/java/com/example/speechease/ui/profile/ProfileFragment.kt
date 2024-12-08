package com.example.speechease.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.speechease.R
import com.example.speechease.ui.ViewModelFactory
import com.example.speechease.ui.welcome.WelcomeActivity

class ProfileFragment : Fragment() {

    companion object;

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val logoutButton = view.findViewById<Button>(R.id.logout)

        logoutButton.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return view
    }
}