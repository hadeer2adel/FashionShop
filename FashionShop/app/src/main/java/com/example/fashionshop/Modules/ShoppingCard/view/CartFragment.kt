package com.example.fashionshop.Modules.ShoppingCard.view

import CartFragmentArgs
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Adapters.CartAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.TaxLineX
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentCartBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartFragment : Fragment() ,CartListener {
    private var currencyConversionRate: Double = 1.0
    val draftOrderIds = mutableListOf<Long>()
    private lateinit var allCategoryFactory: CategoryFactory
    private lateinit var allCategoryViewModel: CategoryViewModel
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var allProductFactory: CartFactory
    private lateinit var allProductViewModel: CartViewModel
    private lateinit var mAdapter: CartAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val view = binding.root
        mAdapter = CartAdapter(this,requireContext())
        mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerViewCartItems.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }

        val customer = CustomerData.getInstance(requireContext())
        binding.currency .text = customer.currency
        allCategoryFactory =
            CategoryFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCategoryViewModel = ViewModelProvider(this, allCategoryFactory).get(CategoryViewModel::class.java)
        var d = 0.0

        allCategoryViewModel.getLatestRates()
        lifecycleScope.launch {
            allCategoryViewModel.productCurrency.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> "showLoading()"
                    is NetworkState.Success -> {
                        d= response.data.rates.EGP
                        Log.i("initViewModel", "initViewModel:${  response.data} ")
                        currencyConversionRate = response.data.rates?.EGP ?: 1.0
                        val exchangeRate = response.data.rates?.EGP ?: 1.0 // Default to 1.0 if rate is not available
                        updateCurrencyRates(exchangeRate)


                    }
                    is NetworkState.Failure -> ""
                    else -> { }
                }
            }}
        allProductFactory =
            CartFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()),CustomerData.getInstance(requireContext()).cartListId)
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(CartViewModel::class.java)

        lifecycleScope.launch {
            allProductViewModel.productCard.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerViewCartItems.visibility = View.GONE
                    }
                    is NetworkState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerViewCartItems.visibility = View.VISIBLE
                        mAdapter.setCartList(response.data.draft_order.line_items.drop(1))
                        val subtotal = response.data.draft_order.line_items.drop(1).sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }
                        val customer = CustomerData.getInstance(requireContext())
                        if (customer.currency=="EGY"){
                         //   val priceDouble = product.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0
                           // price.text = convertCurrency(subtotal)
                            binding.textViewSubtotal.text = "${convertCurrency(subtotal)}"

                        }
                        else
                        {
                            binding.textViewSubtotal.text = "${String.format("%.2f", subtotal)}"


                        }



                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } }
            lifecycleScope.launch {
                allProductViewModel.productCardImage.collectLatest { response ->
                    when(response){
                        is NetworkState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerViewCartItems.visibility = View.GONE
                        }
                        is NetworkState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerViewCartItems.visibility = View.VISIBLE
//                            mAdapter.setCardImages(response.data.images[0].src)
//                            response.data.images[0].src
                              //  allProductViewModel.getCardProductsImages(item.id)
                            }

                        is NetworkState.Failure -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }


        binding.buttonCheckout.setOnClickListener {
            val args = CartFragmentArgs(draftOrderIds).toBundle() // Convert CartFragmentArgs to Bundle
            findNavController().navigate(R.id.action_cartFragment_to_paymentFragment, args)
        }
        binding.deleteall.setOnClickListener {
            allProductViewModel.deleteAllCartProducts()
        }
        return view
    }
    private fun updateCurrencyRates(newRate: Double) {
        mAdapter.updateCurrencyConversionRate(newRate)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun convertCurrency(amount: Double?): String {
        amount ?: return "" // Handle null or undefined amount gracefully
        val convertedPrice = amount * currencyConversionRate
        return String.format("%.2f", convertedPrice)
    }
    override fun deleteCart(id: Long) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete this item from your shopping cart?")
        builder.setPositiveButton("Yes") { dialog, which ->
            allProductViewModel.deleteCardProduct(id)
            Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { dialog, which ->
        }

        val dialog = builder.create()
        dialog.show()
    }


    override  fun sendeditChoosenQuantityRequest(id: Long, quantity: Int,price:String){
        allProductViewModel.editCardQuantityProduct(id,quantity,price)
        Toast.makeText(requireContext(), "sendeditChoosenQuantityRequest Successfully", Toast.LENGTH_LONG).show()
    }

}
