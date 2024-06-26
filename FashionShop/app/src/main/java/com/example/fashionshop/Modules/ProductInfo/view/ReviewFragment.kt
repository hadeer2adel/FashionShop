package com.example.fashionshop.Modules.ProductInfo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.fashionshop.Adapters.ProductAdapter
import com.example.fashionshop.Adapters.ReviewAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Reviews
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModel
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModelFactory
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentReviewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReviewFragment : Fragment() {

    private lateinit var binding: FragmentReviewBinding
    private lateinit var adapter: ReviewAdapter
    private lateinit var viewModel: ProductInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initViewModel() {
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = ProductInfoViewModelFactory(repository, CustomerData.getInstance(requireContext()).cartListId)
        viewModel = ViewModelProvider(this, factory).get(ProductInfoViewModel::class.java)

        lifecycleScope.launch {
            viewModel.reviews.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> {}
                    is NetworkState.Success -> {
                        adapter.submitList(response.data)
                    }
                    is NetworkState.Failure -> {}
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReviewAdapter()
        adapter.submitList(emptyList())
        binding.recycleView.adapter = adapter
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        initViewModel()
        viewModel.getReviews()
    }


}