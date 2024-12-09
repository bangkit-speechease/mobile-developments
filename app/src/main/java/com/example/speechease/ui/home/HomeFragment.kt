package com.example.speechease.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.speechease.MainActivity
import com.example.speechease.R
import com.example.speechease.databinding.FragmentHomeBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.interactive.InteractiveActivity
import com.example.speechease.ui.interactive.InteractiveOneActivity
import com.example.speechease.ui.practicedetail.PracticeDetailActivity
import com.example.speechease.ui.practice.PracticeActivity
import com.example.speechease.ui.progress.ProgressFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = Injection.provideRepository(requireContext())
        lifecycleScope.launch {
            val user = userRepository.getSession().first()
            binding.welcome.text = getString(R.string.welcome, user.name)
        }

        viewModel.navigateToFragment.observe(viewLifecycleOwner) { destinationId ->
            destinationId?.let {
                when (it) {
                    R.id.nav_progress -> {
                        val fragment = ProgressFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, fragment)
                            .commit()
                        (requireActivity() as MainActivity).binding.bottomNavigationView.selectedItemId = it
                    }
                }
                viewModel.resetNavigation()
            }
        }

        binding.progress.setOnClickListener {
            viewModel.navigateTo(R.id.nav_progress)
        }

        binding.cardLatihan.setOnClickListener {
            val intent = Intent(requireContext(), PracticeActivity::class.java)
            startActivity(intent)
        }

        binding.cardInteraktif.setOnClickListener {
            val intent = Intent(requireContext(), InteractiveOneActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}