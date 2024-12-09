package com.example.speechease.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.speechease.databinding.FragmentProfileBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.ViewModelFactory
import com.example.speechease.ui.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    companion object;

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val logoutButton = binding.logout

        logoutButton.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val userRepository = Injection.provideRepository(requireContext())
        lifecycleScope.launch {
            val user = userRepository.getSession().first()
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
        }

        return view
    }
}