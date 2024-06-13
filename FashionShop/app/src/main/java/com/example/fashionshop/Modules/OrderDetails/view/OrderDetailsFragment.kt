package com.example.fashionshop.Modules.OrderDetails.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsFactory
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.databinding.FragmentOrderDetailsBinding

class OrderDetailsFragment() : Fragment()  {
    // Declare the binding object
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var allProductFactory: CartFactory
    private lateinit var allProductViewModel: CartViewModel
    lateinit var allCodesFactory: OrderDetailsFactory
    private lateinit var allCodesViewModel: OrderDetailsViewModel
    val titlesList = mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        allProductFactory =
            CartFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(CartViewModel::class.java)
        allProductViewModel.products.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                Log.i("TAG", "Data updated. Size: ${value.draft_orders}")
                val subtotal = value.draft_orders.sumOf { draftOrder ->
                    draftOrder.line_items.sumOf { it.price.toDouble() }
                }
                binding.subTotalValue.text = "${String.format("%.2f", subtotal)}"
            }
        })

        allCodesFactory =
            OrderDetailsFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCodesViewModel = ViewModelProvider(this, allCodesFactory).get(OrderDetailsViewModel::class.java)
        allCodesViewModel.products2.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
//                binding.discountValue.text = value.price_rules
                val value = value.price_rules
                for (i in value){
                    titlesList.add(i.title)
                    if ( binding.coupon.text.toString() == i.title){




                }
                }


            }
})
        binding.validate.setOnClickListener {
            allCodesViewModel.getAdsCode()
            for (i in titlesList){
                if ( binding.coupon.text.toString() == i){
                    val copon = i
                    Toast.makeText(requireContext(), "Copon Preeesed Successfully", Toast.LENGTH_LONG).show()
                    allCodesViewModel.products2.observe(viewLifecycleOwner, Observer { value ->
                        value?.let {
                        for (i in value.price_rules)
                            if (i.title == copon ){
                               val valueOfDis = i.value
                                val subtotal = binding.subTotalValue.text.toString().toDoubleOrNull() ?: 0.0
                                binding.discountValue.text = i.value
                               val discountAmount = (subtotal * valueOfDis.toDouble()/ 100)
                                val total = subtotal + discountAmount
                                binding.totalValue.text = total.toString()
                                break

                            }
                        }
                        })
                    break
                }
                else{
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.apply {
                        setTitle("Invalid Coupon")
                        setMessage("The coupon you entered is invalid.")
                        setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            binding.discountValue.text = "0"
                            binding.totalValue.text = "0"
                        }
                    }

                    alertDialogBuilder.create().show()

                    Toast.makeText(requireContext(), "Coupon Code False", Toast.LENGTH_LONG).show()
                    Log.i("Coupon", "False: ")
                }
                break

        }
    }

        }}