package com.dressden.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dressden.R
import com.dressden.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import com.dressden.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        setupObservers()
        checkNetworkConnection()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Setup bottom navigation with navigation controller
        binding.bottomNav.setupWithNavController(navController)
        
        // Handle navigation visibility based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.productDetailFragment,
                R.id.checkoutFragment -> hideBottomNav()
                else -> showBottomNav()
            }
        }
    }
    
    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                showSnackbar(it)
                viewModel.errorMessageShown()
            }
        }
    }
    
    private fun checkNetworkConnection() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showSnackbar(getString(R.string.no_internet_connection))
        }
    }
    
    private fun showBottomNav() {
        binding.bottomNav.visibility = View.VISIBLE
    }
    
    private fun hideBottomNav() {
        binding.bottomNav.visibility = View.GONE
    }
    
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
