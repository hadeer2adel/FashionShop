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
import com.example.fashionshop.Model.DefaultAddressBody
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.EmailMarketingConsentBody
import com.example.fashionshop.Model.LineItemBody
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Model.SmsMarketingConsentBody
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
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

class OrderDetailsFragment() : Fragment() {
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
    private var currencyConversionRate: Double = 1.0
    private lateinit var allCategoryFactory: CategoryFactory
    private lateinit var allCategoryViewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customer = CustomerData.getInstance(requireContext())
        binding.currency.text = customer.currency
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
        allCodesFactory = OrderDetailsFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCodesViewModel = ViewModelProvider(this, allCodesFactory).get(OrderDetailsViewModel::class.java)
        allCodesViewModel.getAdsCode()
        fetchTitlesList()

        allProductFactory = CartFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()), CustomerData.getInstance(requireContext()).cartListId)
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(CartViewModel::class.java)

        allCategoryFactory = CategoryFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCategoryViewModel = ViewModelProvider(this, allCategoryFactory).get(CategoryViewModel::class.java)
        allCategoryViewModel.getLatestRates()
        lifecycleScope.launch {
            allCategoryViewModel.productCurrency.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> "showLoading()"
                    is NetworkState.Success -> {
                        Log.i("initViewModel", "initViewModel:${response.data}")
                        currencyConversionRate = response.data.rates?.EGP ?: 1.0
                    }
                    is NetworkState.Failure -> ""
                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            allProductViewModel.productCard.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is NetworkState.Success -> {
                        lineItemsList = response.data.draft_order.line_items.drop(1)

                        binding.progressBar.visibility = View.GONE
                        val subtotal = response.data.draft_order.line_items.drop(1).sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }
                        val customer = CustomerData.getInstance(requireContext())
                        if (customer.currency == "USD") {
                            binding.subTotalValue.text = "${String.format("%.2f", convertCurrency(subtotal))}"
                        } else {
                            binding.subTotalValue.text = "${String.format("%.2f", subtotal)}"
                        }
                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.validate.setOnClickListener {
            validateCoupon()
        }
    }
    private fun convertCurrency(amount: Double?): Double {
        amount ?: return 0.0 // Handle null or undefined amount gracefully
        return amount / currencyConversionRate
    }
//    private fun convertCurrency(amount: Double?): String {
//        amount ?: return "" // Handle null or undefined amount gracefully
//        val convertedPrice = amount * currencyConversionRate
//        return String.format("%.2f", convertedPrice)
//    }

    private fun fetchTitlesList() {
        lifecycleScope.launch {
            allCodesViewModel.productCode.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> {}
                    is NetworkState.Success -> {
                        val value = response.data.price_rules
                        titlesList.clear()
                        titlesList.addAll(value.map { it.title })
                        binding.validate.isEnabled = true // Enable validate button
                    }
                    is NetworkState.Failure -> {
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

        binding.paymentButtonContainer.setOnClickListener {
            placeOrder()
        }
    }
    private fun placeOrder() {
        // Calculate subtotal
        val subtotal = lineItemsList.sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }

        /*val orderBody = OrderBody(
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
            billing_address = AddressBody(
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
                created_at = "2024-06-13T09:49:18-04:00", // Replace with actual creation date
                updated_at = "2024-06-14T09:54:02-04:00", // Replace with actual update date
                first_name = "John", // Replace with actual customer first name
                last_name = "Doe", // Replace with actual customer last name
                state = "disabled", // Replace with actual state
                verified_email = true, // Replace with actual verification status
                tags = "sample-tag", // Replace with actual tags
                currency = "USD", // Replace with actual currency
                admin_graphql_api_id = "gid://shopify/Customer/1", // Replace with actual GraphQL API ID
                default_address = DefaultAddressBody(
                    id = 1, // Replace with actual default address ID
                    customer_id = 1, // Replace with actual customer ID
                    first_name = "John",
                    last_name = "Doe",
                    address1 = "123 Street",
                    city = "City",
                    zip = "12345",
                    country = "Country",
                    phone = "1234567890",
                    name = "John Doe",
                    country_code = "US",
                    country_name = "Country",
                    province = null,
                    address2 = null,
                    company = null,
                    province_code = null,
                    default = true
                ),
                note = null, // Optional, adjust as per your needs
                multipass_identifier = null, // Optional, adjust as per your needs
                tax_exempt = false // Replace with actual tax exempt status
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
            total_tax = 13.5, // Replace with actual total tax
            currency = "USD" // Replace with actual currency
        )*/
        //latest version
        /* val orderBody = OrderBody(
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
            billing_address = AddressBody(
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
            customer = CustomerBody(
                id = 1, // Replace with actual customer ID
                email = "customer@example.com", // Replace with actual customer email
                created_at = "2024-06-13T09:49:18-04:00", // Replace with actual creation date
                updated_at = "2024-06-14T09:54:02-04:00", // Replace with actual update date
                first_name = "John", // Replace with actual customer first name
                last_name = "Doe", // Replace with actual customer last name
                state = "disabled", // Replace with actual state
                verified_email = true, // Replace with actual verification status
                tags = "sample-tag", // Replace with actual tags
                currency = "USD", // Replace with actual currency
                admin_graphql_api_id = "gid://shopify/Customer/1", // Replace with actual GraphQL API ID
                default_address = DefaultAddressBody(
                    id = 1, // Replace with actual default address ID
                    customer_id = 1, // Replace with actual customer ID
                    first_name = "John",
                    last_name = "Doe",
                    address1 = "123 Street",
                    city = "City",
                    zip = "12345",
                    country = "Country",
                    phone = "1234567890",
                    name = "John Doe",
                    country_code = "US",
                    country_name = "Country",
                    province = null,
                    address2 = null,
                    company = null,
                    province_code = null,
                    default = true
                ),
                note = null, // Optional, adjust as per your needs
                multipass_identifier = null, // Optional, adjust as per your needs
                tax_exempt = false // Replace with actual tax exempt status
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
            total_tax = 13.5, // Replace with actual total tax
            currency = "USD" // Replace with actual currency
        )*/

        val address = AddressBody(
            first_name = "Isaiah",
            address1 = "P.O. Box 835, 451 Vulputate Road",
            phone = "+62361752305",
            city = "Broken Arrow",
            zip = "5416",
            province = null,
            country = "Indonesia",
            last_name = "Orr",
            address2 = null,
            company = null,
            latitude = -0.789275,
            longitude = 113.921327,
            name = "Isaiah Orr",
            country_code = "ID",
            province_code = null
        )

        val emailMarketingConsent = EmailMarketingConsentBody(
            state = "not_subscribed",
            opt_in_level = "single_opt_in",
            consent_updated_at = null
        )

        val smsMarketingConsent = SmsMarketingConsentBody(
            state = "not_subscribed",
            opt_in_level = "single_opt_in",
            consent_updated_at = null,
            consent_collected_from = "OTHER"
        )

        val defaultAddress = DefaultAddressBody(
            id = 8747727093931,
            customer_id = 7395407659179,
            first_name = "Isaiah",
            last_name = "Orr",
            company = null,
            address1 = "P.O. Box 835, 451 Vulputate Road",
            address2 = null,
            city = "Broken Arrow",
            province = null,
            country = "Indonesia",
            zip = "5416",
            phone = "+62361752305",
            name = "Isaiah Orr",
            province_code = null,
            country_code = "ID",
            country_name = "Indonesia",
            default = true
        )

        val customer = CustomerBody(
            id = CustomerData.getInstance(requireContext()).id,
            email = CustomerData.getInstance(requireContext()).email,
            created_at = "2024-06-13T09:49:18-04:00",
            updated_at = "2024-06-14T09:54:02-04:00",
            first_name = CustomerData.getInstance(requireContext()).name,
            last_name = CustomerData.getInstance(requireContext()).name,
            state = "disabled",
            note = null,
            verified_email = true,
            multipass_identifier = null,
            tax_exempt = false,
            email_marketing_consent = emailMarketingConsent,
            sms_marketing_consent = smsMarketingConsent,
            tags = "egnition-sample-data, referral",
            currency = CustomerData.getInstance(requireContext()).currency,
            tax_exemptions = listOf(),
            admin_graphql_api_id = "gid://shopify/Customer/7395407659179",
            default_address = defaultAddress
        )

        val lineItem = lineItemsList.map { lineItem ->
            LineItemBody(
                variant_id = lineItem.variant_id,
                quantity = lineItem.quantity,
                id = lineItem.id,
                title = lineItem.title,
                price = lineItem.price
            )
        }

        val orderBody = OrderBody(
            billing_address = address,
            customer = customer,
            line_items = lineItem,
            total_tax = 13.5,
            currency = CustomerData.getInstance(requireContext()).currency
        )
        // Wrapping the orderBody within an "order" object
        val wrappedOrderBody = mapOf("order" to orderBody)

        // Example call to ViewModel method to create order
        allCodesViewModel.createOrder(wrappedOrderBody,
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
    private fun validateCoupon() {
        allCodesViewModel.getAdsCode()
        Log.i("getAdsCode", "titlesList: $titlesList")
        val couponCode = binding.coupon.text.toString()

        if (couponCode in titlesList) {
            val coupon = couponCode
            Toast.makeText(requireContext(), "Coupon Applied Successfully", Toast.LENGTH_LONG).show()
            lifecycleScope.launch {
                allCodesViewModel.productCode.collectLatest { response ->
                    when (response) {
                        is NetworkState.Loading -> {}
                        is NetworkState.Success -> {
                            val value = response.data
                            for (rule in value.price_rules) {
                                if (rule.title == coupon) {
                                    val valueOfDis = rule.value.toDoubleOrNull() ?: 0.0
                                    val subtotal = binding.subTotalValue.text.toString().toDoubleOrNull() ?: 0.0
                                    val discountAmount = subtotal * (valueOfDis / 100)
                                    val total = subtotal + discountAmount
                                    binding.discountValue.text = "${String.format("%.2f", kotlin.math.abs(valueOfDis))}%"
                                    binding.totalValue.text = String.format("%.2f", total)
                                    break
                                }
                            }
                        }
                        is NetworkState.Failure -> {
                            Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            showInvalidCouponDialog()
        }
    }

    private fun showInvalidCouponDialog() {
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
        Toast.makeText(requireContext(), "Coupon Code is Invalid", Toast.LENGTH_LONG).show()
        Log.i("Coupon", "False: ")
    }
}
}
