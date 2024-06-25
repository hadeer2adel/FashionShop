package com.example.fashionshop.Modules.ShoppingCard.view

import CartFragmentArgs
import android.app.AlertDialog
import android.content.Intent
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
import com.example.fashionshop.Model.inventoryQuantities
import com.example.fashionshop.Model.originalPrices
import com.example.fashionshop.Modules.Authentication.view.LoginActivity
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.View.isNetworkConnected
import com.example.fashionshop.databinding.FragmentCartBinding
import com.google.android.material.snackbar.Snackbar
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            if (isNetworkConnected(requireContext())) {
            mAdapter = CartAdapter(this,requireContext(),requireView())
            mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.recyclerViewCartItems.apply {
                adapter = mAdapter
                layoutManager = mLayoutManager
            }
            if (CustomerData.getInstance(requireContext()).isLogged) {
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
                allProductViewModel.getCardProducts()
                lifecycleScope.launch {
                    allProductViewModel.productCard.collectLatest { response ->
                        when(response){
                            is NetworkState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.recyclerViewCartItems.visibility = View.GONE
                                binding.emptyView.visibility = View.GONE

                            }
                            is NetworkState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.emptyView.visibility = View.GONE

                                val lineItems = response.data.draft_order.line_items
                                // val notes = response.data.draft_order.note_attributes
                                if (lineItems.size <= 1) {
                                    binding.recyclerViewCartItems.visibility = View.GONE
                                    binding.emptyView.visibility = View.VISIBLE
                                } else {
                                    binding.recyclerViewCartItems.visibility = View.VISIBLE
                                    binding.emptyView.visibility = View.GONE
                                    mAdapter.setCartList(lineItems.drop(1))
                                    // mAdapter.setCartImages(notes.drop(1))
                                    Log.i("productc", "onViewCreated: ${lineItems.drop(1)}")

                                    val subtotal = lineItems.drop(1).sumByDouble { it.properties.get(0).value.split("*").getOrNull(1)?.trim() ?.toDoubleOrNull() ?: 0.0 }
                                    val customer = CustomerData.getInstance(requireContext())
                                    if (customer.currency == "USD") {
                                        binding.textViewSubtotal.text = "${convertCurrency(subtotal)}"
                                    } else {
                                        binding.textViewSubtotal.text = "${String.format("%.2f", subtotal)}"
                                    }
                                }
                            }
                            is NetworkState.Failure -> {
                                binding.progressBar.visibility = View.GONE
                                binding.emptyView.visibility = View.GONE

                                Snackbar.make(requireView(),response.error.message.toString(), Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } }


                binding.buttonCheckout.setOnClickListener {
                    val args = CartFragmentArgs(draftOrderIds).toBundle()
                    findNavController().navigate(R.id.action_cartFragment_to_paymentFragment, args)
                }
                binding.deleteall.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.delete_all_cart_items_title))
                        .setMessage(getString(R.string.delete_all_cart_items_message))
                        .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                            allProductViewModel.deleteAllCartProducts()
                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                    Log.i("list", "onViewCreated: ${inventoryQuantities} , ////  ${originalPrices}")
                }
            }
            else{
                binding.loginBtn.setOnClickListener {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                }
                showAlertDialog("Authentication Error" , "You need to be logged in to access this feature. Please log in to continue.")
                binding.emptyView.visibility = View.GONE
                binding.emptyViewGuest.visibility = View.VISIBLE
                showAlertDialog("Authentication Error" , "You need to be logged in to access this feature. Please log in to continue.")
                binding.progressBar.visibility = View.GONE

            }
            }
            else
            {
                binding.relativeLayout.visibility = View.GONE
                binding.recyclerViewCartItems.visibility = View.GONE
                binding.mainLinear.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.layoutConnection.visibility = View.VISIBLE
            }

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
        val convertedPrice = amount / currencyConversionRate
        allProductViewModel.getCardProducts()
        return String.format("%.2f", convertedPrice)
    }
    override fun deleteCart(id: Long) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.confirm_deletion_title))
        builder.setMessage(getString(R.string.confirm_deletion_message))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            allProductViewModel.deleteCardProduct(id)
            Snackbar.make(requireView(), getString(R.string.item_deleted_successfully), Snackbar.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }


    override  fun sendeditChoosenQuantityRequest(id: Long, quantity: Int,price:String,inventoryQuantitiess:String,images:String){
        allProductViewModel.editCardQuantityProduct(id,quantity,price, inventoryQuantitiess,images)
        Snackbar.make(requireView(),"Quantity Changed Successfully", Snackbar.LENGTH_SHORT).show()
    }
    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

}