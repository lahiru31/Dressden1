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
import com.dressden.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            // Name field validation
            nameInput.addTextChangedListener { text ->
                nameLayout.error = if (text.isNullOrBlank()) {
                    getString(R.string.error_required_field)
                } else if (!viewModel.validateDisplayName(text.toString())) {
                    getString(R.string.error_invalid_name)
                } else {
                    null
                }
                updateSignUpButtonState()
            }

            // Email field validation
            emailInput.addTextChangedListener { text ->
                emailLayout.error = if (text.isNullOrBlank()) {
                    getString(R.string.error_required_field)
                } else if (!viewModel.validateEmail(text.toString())) {
                    getString(R.string.error_invalid_email)
                } else {
                    null
                }
                updateSignUpButtonState()
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
                updateSignUpButtonState()
            }

            // Confirm password field validation
            confirmPasswordInput.addTextChangedListener { text ->
                confirmPasswordLayout.error = if (text.isNullOrBlank()) {
                    getString(R.string.error_required_field)
                } else if (text.toString() != passwordInput.text.toString()) {
                    getString(R.string.error_passwords_dont_match)
                } else {
                    null
                }
                updateSignUpButtonState()
            }

            // Sign up button click
            signUpButton.setOnClickListener {
                val name = nameInput.text.toString()
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()
                viewModel.signUp(email, password, name)
            }

            // Sign in navigation
            signInText.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.signUpButton.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    // Navigation is handled by AuthActivity
                }
                is AuthState.Error -> {
                    binding.signUpButton.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                else -> {
                    binding.signUpButton.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun updateSignUpButtonState() {
        binding.signUpButton.isEnabled = binding.nameLayout.error == null &&
                binding.emailLayout.error == null &&
                binding.passwordLayout.error == null &&
                binding.confirmPasswordLayout.error == null &&
                !binding.nameInput.text.isNullOrBlank() &&
                !binding.emailInput.text.isNullOrBlank() &&
                !binding.passwordInput.text.isNullOrBlank() &&
                !binding.confirmPasswordInput.text.isNullOrBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
