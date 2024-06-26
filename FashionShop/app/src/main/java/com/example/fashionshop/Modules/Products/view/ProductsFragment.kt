package com.example.fashionshop.Modules.Products.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionshop.Adapters.ProductAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModel
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModelFactory
import com.example.fashionshop.Modules.Products.viewModel.ProductsFactory
import com.example.fashionshop.Modules.Products.viewModel.ProductsViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentProductsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ProductsFragment : Fragment()  {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private val args: ProductsFragmentArgs by navArgs()
    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: ProductsViewModel
    private lateinit var favViewModel: FavViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val view = binding.root
        //Toast.makeText(requireContext(), "Clicked on ${args.brandName}", Toast.LENGTH_SHORT).show()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRV()
        initViewModel()

        viewModel.collectSearch()

        binding.searchBarText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.emitSearch(s.toString().lowercase())
            }
        })

        binding.filterBtn.setOnClickListener {
            showFilterMenu(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRV(){
        val onClick: (isFav: Boolean, product: Product) -> Unit = { isFav, product ->
            if (CustomerData.getInstance(requireContext()).isLogged) {
                if (isFav) {
                    favViewModel.insertFavProduct(product)
                } else {
                    product.id?.let { it1 -> favViewModel.deleteFavProduct(it1) }
                }
            }
        }
        val onCardClick: (id: Long) -> Unit = {
            val navController = NavHostFragment.findNavController(this)
            val action = ProductsFragmentDirections.actionToProductInfoFragment(it)
            navController.navigate(action)
        }
        val onStart: (id: Long, onTrue: ()->Unit, onFalse: ()->Unit) ->Unit = { id, onTrue, onFalse ->
            if (CustomerData.getInstance(requireContext()).isLogged) {
                favViewModel.isFavProduct(id, onTrue, onFalse)
            }
        }

        adapter = ProductAdapter(requireContext(), onStart, onClick, onCardClick)

        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter
    }

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = ProductsFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ProductsViewModel::class.java)
        if (CustomerData.getInstance(requireContext()).isLogged) {
            val favFactory = FavViewModelFactory(repository, CustomerData.getInstance(requireContext()).favListId)
            favViewModel = ViewModelProvider(this, favFactory).get(FavViewModel::class.java)
        }

        viewModel.getProducts(args.brandName)
        lifecycleScope.launch {
            viewModel.product.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> showLoading()
                    is NetworkState.Success -> response.data.products?.let { showSuccess(it) }
                    is NetworkState.Failure -> showError(requireContext().getString(R.string.network_error), requireContext().getString(R.string.failed_load_data))
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar2.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.INVISIBLE
    }

    private fun showSuccess(products: List<Product>) {
        binding.progressBar2.visibility = View.INVISIBLE
        binding.rvProducts.visibility = View.VISIBLE
        adapter.submitList(products)
    }

    private fun showError(title: String, message: String) {
        binding.progressBar2.visibility = View.INVISIBLE
        binding.rvProducts.visibility = View.INVISIBLE
        showAlertDialog(title, message)
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setNegativeButton(requireContext().getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun showFilterMenu(context: Context) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filter, null)
        val fromInputLayout = dialogView.findViewById<TextInputLayout>(R.id.from)
        val toInputLayout = dialogView.findViewById<TextInputLayout>(R.id.to)
        val fromInput = fromInputLayout.editText
        val toInput = toInputLayout.editText

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.dialog_filter))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                val fromValue = fromInput?.text?.toString()?.toFloatOrNull()
                val toValue = toInput?.text?.toString()?.toFloatOrNull()
                handleFilterSelection(fromValue, toValue)
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                // Handle cancel
            }
            .show()
    }

    private fun handleFilterSelection(fromValue: Float?, toValue: Float?) {
        viewModel.filterProductsByPrice(fromValue, toValue)
    }


}