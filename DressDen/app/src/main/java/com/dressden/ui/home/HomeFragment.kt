package com.dressden.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dressden.databinding.FragmentHomeBinding
import com.dressden.ui.home.adapters.CategoryAdapter
import com.dressden.ui.home.adapters.ProductAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    
    private val categoryAdapter = CategoryAdapter { category ->
        navigateToCategory(category)
    }
    
    private val newArrivalsAdapter = ProductAdapter(
        onProductClick = { product ->
            navigateToProductDetail(product.id)
        },
        onFavoriteClick = { product ->
            viewModel.toggleFavorite(product)
        }
    )
    
    private val popularItemsAdapter = ProductAdapter(
        onProductClick = { product ->
            navigateToProductDetail(product.id)
        },
        onFavoriteClick = { product ->
            viewModel.toggleFavorite(product)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupObservers()
        
        // Initial data load
        viewModel.loadHomeData()
    }

    private fun setupRecyclerViews() {
        binding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        binding.newArrivalsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newArrivalsAdapter
        }

        binding.popularItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularItemsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }

        viewModel.newArrivals.observe(viewLifecycleOwner) { products ->
            newArrivalsAdapter.submitList(products)
        }

        viewModel.popularItems.observe(viewLifecycleOwner) { products ->
            popularItemsAdapter.submitList(products)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToCategory(category: Category) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeToCategory(category.id)
        )
    }

    private fun navigateToProductDetail(productId: String) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeToProductDetail(productId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
