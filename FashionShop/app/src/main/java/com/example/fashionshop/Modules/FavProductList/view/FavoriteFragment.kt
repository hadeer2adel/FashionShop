package com.example.fashionshop.Modules.FavProductList.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionshop.Adapters.FavProductAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Modules.Authentication.view.LoginActivity
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModel
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModelFactory
import com.example.fashionshop.Modules.FavProductList.view.FavoriteFragmentDirections
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.View.isNetworkConnected
import com.example.fashionshop.View.showDialog
import com.example.fashionshop.databinding.FragmentFavoriteBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: FavProductAdapter
    private lateinit var viewModel: FavViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNetworkConnected(requireContext())){
            if (CustomerData.getInstance(requireContext()).isLogged) {
                setUpRecycleView()
                initViewModel()
                viewModel.getFavProducts()
            }
            else {
                binding.loginBtn.setOnClickListener {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                }
                binding.emptyView.visibility = View.GONE
                binding.emptyViewGuest.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
        else
        {
            binding.recycleView.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            binding.layoutConnection.visibility = View.VISIBLE
        }
    }

    private fun setUpRecycleView(){
        val onClick: (id: Long) -> Unit = {
            val onAllow: () -> Unit = {
                viewModel.deleteFavProduct(it)
            }
            showDialog(
                requireContext(),
                R.string.delete_title,
                R.string.delete_body,
                onAllow
            )
        }
        val onCardClick: (id: Long) -> Unit = {
            val navController = NavHostFragment.findNavController(this)
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToProductInfoFragment(it)
            navController.navigate(action)

        }

        adapter = FavProductAdapter(requireContext(), true, onClick, onCardClick)
        adapter.submitList(emptyList())
        binding.recycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleView.adapter = adapter
    }

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = FavViewModelFactory(repository, CustomerData.getInstance(requireContext()).favListId)
        viewModel = ViewModelProvider(this, factory).get(FavViewModel::class.java)

        lifecycleScope.launch {
            viewModel.product.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recycleView.visibility = View.GONE
                    }
                    is NetworkState.Success -> {
                        if (response.data.draft_order.line_items.size > 1) {
                            binding.progressBar.visibility = View.GONE
                            binding.recycleView.visibility = View.VISIBLE
                            binding.emptyView.visibility = View.GONE
                            adapter.submitList(response.data.draft_order.line_items.drop(1))
                        } else {
                            binding.recycleView.visibility = View.GONE
                            binding.emptyView.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE

                        }
                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                      //  Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                        Snackbar.make(binding.root, response.error.message.toString(), Snackbar.LENGTH_SHORT).show()

                    }

                    else -> {}
                }
            }
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(requireContext().getString(R.string.sure)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

}