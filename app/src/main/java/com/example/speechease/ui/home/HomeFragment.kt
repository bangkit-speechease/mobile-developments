package com.example.speechease.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.speechease.MainActivity
import com.example.speechease.R
import com.example.speechease.databinding.FragmentHomeBinding
import com.example.speechease.ui.interactive.InteractiveActivity
import com.example.speechease.ui.practicedetail.PracticeDetailActivity
import com.example.speechease.ui.progress.ProgressFragment

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
            val intent = Intent(requireContext(), PracticeDetailActivity::class.java)
            startActivity(intent)
        }

        binding.cardInteraktif.setOnClickListener {
            val intent = Intent(requireContext(), InteractiveActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}