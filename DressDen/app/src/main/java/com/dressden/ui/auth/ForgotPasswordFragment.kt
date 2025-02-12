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
import com.dressden.databinding.FragmentForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
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
                updateResetButtonState()
            }

            // Reset password button click
            resetButton.setOnClickListener {
                val email = emailInput.text.toString()
                viewModel.resetPassword(email)
            }

            // Back to sign in
            backToSignInText.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.resetButton.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is AuthState.ResetPasswordSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    showSuccessMessage()
                    findNavController().navigate(R.id.action_forgotPassword_to_signIn)
                }
                is AuthState.Error -> {
                    binding.resetButton.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                else -> {
                    binding.resetButton.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showSuccessMessage() {
        Snackbar.make(
            binding.root,
            getString(R.string.reset_password_email_sent),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun updateResetButtonState() {
        binding.resetButton.isEnabled = binding.emailLayout.error == null &&
                !binding.emailInput.text.isNullOrBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
