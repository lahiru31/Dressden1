package com.dressden.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dressden.R
import com.dressden.databinding.FragmentSignInBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            // Email field validation
            emailInput.addTextChangedListener { text ->
                emailLayout.error = if (text.isNullOrBlank()) {
                    getString(R.string.error_required_field)
                } else if (!viewModel.validateEmail(text.toString())) {
                    getString(R.string.error_invalid_email)
                } else {
                    null
                }
                updateSignInButtonState()
            }

            // Password field validation
            passwordInput.addTextChangedListener { text ->
                passwordLayout.error = if (text.isNullOrBlank()) {
                    getString(R.string.error_required_field)
                } else if (!viewModel.validatePassword(text.toString())) {
                    getString(R.string.error_invalid_password)
                } else {
                    null
                }
                updateSignInButtonState()
            }

            // Sign in button click
            signInButton.setOnClickListener {
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()
                viewModel.signIn(email, password)
            }

            // Sign up navigation
            signUpText.setOnClickListener {
                findNavController().navigate(R.id.action_signIn_to_signUp)
            }

            // Forgot password navigation
            forgotPasswordText.setOnClickListener {
                findNavController().navigate(R.id.action_signIn_to_forgotPassword)
            }
        }
    }

    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.signInButton.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    // Navigation is handled by AuthActivity
                }
                is AuthState.Error -> {
                    binding.signInButton.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                else -> {
                    binding.signInButton.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun updateSignInButtonState() {
        binding.signInButton.isEnabled = binding.emailLayout.error == null &&
                binding.passwordLayout.error == null &&
                !binding.emailInput.text.isNullOrBlank() &&
                !binding.passwordInput.text.isNullOrBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
