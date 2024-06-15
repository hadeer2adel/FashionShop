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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.fashionshop.Model.AddressBody
import com.example.fashionshop.Model.CustomerBody
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.LineItemBody
import com.example.fashionshop.Model.MarketingConsent
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsFactory
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentOrderDetailsBinding
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderDetailsFragment() : Fragment()  {
    // Declare the binding object
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var allProductFactory: CartFactory
    private lateinit var cartViewModel: CartViewModel
    lateinit var allCodesFactory: OrderDetailsFactory
    private lateinit var allCodesViewModel: OrderDetailsViewModel
    val titlesList = mutableListOf<String>()
    private var lineItemsList: List<DraftOrderResponse.DraftOrder.LineItem> = emptyList() // Initialize to empty list
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
            CartFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()),CustomerData.getInstance(requireContext()).cartListId)
        cartViewModel = ViewModelProvider(this, allProductFactory).get(CartViewModel::class.java)
//        allProductViewModel.products.observe(viewLifecycleOwner, Observer { value ->
//            value?.let {
//                Log.i("TAG", "Data updated. Size: ${value.draft_orders}")
//                val subtotal = value.draft_orders.sumOf { draftOrder ->
//                    draftOrder.line_items.sumOf { it.price.toDouble() }
//                }
//                binding.subTotalValue.text = "${String.format("%.2f", subtotal)}"
//            }
//        })
        lifecycleScope.launch {
            cartViewModel.productCard.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {
                    }
                    is NetworkState.Success -> {
                        lineItemsList = response.data.draft_order.line_items.drop(1)

                        val subtotal = response.data.draft_order.line_items.drop(1).sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }
                        binding.subTotalValue.text = "Subtotal: $${String.format("%.2f", subtotal)}" }
                    is NetworkState.Failure -> {
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

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
            Log.i("getAdsCode", "titlesList: ${titlesList}")
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

        binding.paymentButtonContainer.setOnClickListener {
            placeOrder()
        }
    }
    /*private fun placeOrder() {
        // Calculate subtotal
        val subtotal = lineItemsList.sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }

        // Example of creating an OrderBody (adjust as per your actual data structure)
        val orderBody = OrderBody(
            shipping_address = AddressBody(
                first_name = "John",
                last_name = "Doe",
                address1 = "123 Main St",
                phone = "123-456-7890",
                city = "Anytown",
                zip = "12345",
                province = "Province",
                country = "Country",
                latitude = 0.0,
                longitude = 0.0,
                name = "John Doe",
                country_code = "US",
                address2 = null, // Optional, adjust as per your needs
                company = null, // Optional, adjust as per your needs
                province_code = null // Optional, adjust as per your needs
            ),
            billing_address =AddressBody(
                first_name = "John",
                last_name = "Doe",
                address1 = "123 Main St",
                phone = "123-456-7890",
                city = "Anytown",
                zip = "12345",
                province = "Province",
                country = "Country",
                latitude = 0.0,
                longitude = 0.0,
                name = "John Doe",
                country_code = "US",
                address2 = null, // Optional, adjust as per your needs
                company = null, // Optional, adjust as per your needs
                province_code = null // Optional, adjust as per your needs
            ), // Replace with actual billing address
            customer = CustomerBody(
                id = 1, // Replace with actual customer ID
                email = "customer@example.com", // Replace with actual customer email
                first_name = "John", // Replace with actual customer first name
                last_name = "Doe", // Replace with actual customer last name
                currency = "USD", // Replace with actual currency
                default_address = AddressBody(
                    first_name = "John",
                    last_name = "Doe",
                    address1 = "123 Street",
                    city = "City",
                    zip = "12345",
                    country = "Country",
                    phone = "1234567890",
                    latitude = 0.0,
                    longitude = 0.0,
                    province = null,
                    address2 = null,
                    company = null,
                    name = "John Doe",
                    country_code = "US",
                    province_code = null
                )
            ),
            line_items = lineItemsList.map { lineItem ->
                LineItemBody(
                    variant_id = lineItem.variant_id,
                    quantity = lineItem.quantity,
                    id = lineItem.id,
                    title = lineItem.title,
                    price = lineItem.price
                )
            },
            total_tax = "0", // Replace with actual total tax
            currency = "USD" // Replace with actual currency
        )

        // Example call to ViewModel method to create order
        allCodesViewModel.createOrder(orderBody,
            onSuccess = {
                Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_LONG).show()
                Log.d("placeOrder", "success")
                // Handle success, e.g., navigate to a success screen if needed
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                Log.d("placeOrder", "error: $errorMessage")
                // Handle error, e.g., show an error message to the user
            }
        )
    }*/
    private fun placeOrder() {
        // Calculate subtotal
        val subtotal = lineItemsList.sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }

        // Construct shipping address (and billing address if applicable)
        val shippingAddress = AddressBody(
            first_name = "John",
            last_name = "Doe",
            address1 = "123 Main St",
            phone = "123-456-7890",
            city = "Anytown",
            zip = "12345",
            province = "Province",
            country = "Country",
            latitude = 0.0,
            longitude = 0.0,
            name = "John Doe",
            country_code = "US",
            address2 = null,
            company = null,
            province_code = null
        )

        // Example of creating an OrderBody instance
        val orderBody = OrderBody(
            shipping_address = shippingAddress,
            billing_address = shippingAddress, // Replace with actual billing address if different
            customer = CustomerBody(
                id = CustomerData.getInstance(requireContext()).id,
                email = CustomerData.getInstance(requireContext()).email,
                first_name = CustomerData.getInstance(requireContext()).name,
                last_name = CustomerData.getInstance(requireContext()).name,
                state = "State", // Replace with actual state
                email_marketing_consent = MarketingConsent("state", "opt_in_level", "consent_updated_at"), // Replace with actual MarketingConsent
                sms_marketing_consent = MarketingConsent("state", "opt_in_level", "consent_updated_at"), // Replace with actual MarketingConsent
                tags = "tags", // Replace with actual tags
                currency = CustomerData.getInstance(requireContext()).currency,
                default_address = shippingAddress // Replace with actual default address
            ),
            line_items = lineItemsList.map { lineItem ->
                LineItemBody(
                    variant_id = lineItem.variant_id,
                    quantity = lineItem.quantity,
                    id = lineItem.id,
                    title = lineItem.title ?: "dummy",
                    price = lineItem.price?.toDouble()
                )
            },
            total_tax = 13.5, // Replace with actual total tax
            currency = CustomerData.getInstance(requireContext()).currency // Replace with actual currency
        )

        // Example call to ViewModel method to create order
        allCodesViewModel.createOrder(orderBody,
            onSuccess = {
                Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_LONG).show()
                Log.d("placeOrder", "success")
                // Handle success, e.g., navigate to a success screen if needed
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                Log.d("placeOrder", "error: $errorMessage")
                // Handle error, e.g., show an error message to the user
            }
        )
    }











}