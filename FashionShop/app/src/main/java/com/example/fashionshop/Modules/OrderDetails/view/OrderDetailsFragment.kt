package com.example.fashionshop.Modules.OrderDetails.view

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.fashionshop.Model.AddressBody
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Model.CustomerBody
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.DefaultAddressBody
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.LineItemBody
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Modules.Address.viewModel.AddressFactory
import com.example.fashionshop.Modules.Address.viewModel.AddressViewModel
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsFactory
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsViewModel
import com.example.fashionshop.Modules.Payment.view.PaymentSheetFragment
import com.example.fashionshop.Modules.Payment.viewModel.PaymentFactory
import com.example.fashionshop.Modules.Payment.viewModel.PaymentViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentOrderDetailsBinding
import com.google.android.material.snackbar.Snackbar
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
    private var currencyConversionRate: Double = 1.0
    private lateinit var allCategoryFactory: CategoryFactory
    private lateinit var allCategoryViewModel: CategoryViewModel
    private lateinit var allPaymentFactory: PaymentFactory
    private lateinit var allPaymentViewModel: PaymentViewModel
    var subtotalInt = 0.0
    private var lineItemsList: List<DraftOrderResponse.DraftOrder.LineItem> = emptyList() // Initialize to empty list
    private lateinit var allProductViewModel: CartViewModel
    lateinit var addressFactory: AddressFactory
    lateinit var addressViewModel: AddressViewModel
    lateinit var filteredAddresses : Addresse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allPaymentFactory =
            PaymentFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allPaymentViewModel =
            ViewModelProvider(this, allPaymentFactory).get(PaymentViewModel::class.java)
        val customer = CustomerData.getInstance(requireContext())
        binding.currency.text = customer.currency
        navController = NavHostFragment.findNavController(this)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        allCodesFactory =
            OrderDetailsFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCodesViewModel =
            ViewModelProvider(this, allCodesFactory).get(OrderDetailsViewModel::class.java)
        allCodesViewModel.getAdsCode()
        fetchTitlesList()

        allProductFactory = CartFactory(
            RepositoryImp.getInstance(NetworkManagerImp.getInstance()),
            CustomerData.getInstance(requireContext()).cartListId
        )
        allProductViewModel =
            ViewModelProvider(this, allProductFactory).get(CartViewModel::class.java)

        allCategoryFactory =
            CategoryFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCategoryViewModel =
            ViewModelProvider(this, allCategoryFactory).get(CategoryViewModel::class.java)
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
                        binding.progressBar.visibility = View.GONE
                        lineItemsList = response.data.draft_order.line_items.drop(1)
                        var subtotal = response.data.draft_order.line_items.drop(1)
                            .sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }
//                        subtotalInt=subtotal
                        val customer = CustomerData.getInstance(requireContext())
                        if (customer.currency == "USD") {
                            binding.subTotalValue.text =
                                "${String.format("%.2f", convertCurrency(subtotal))}"
                            binding.totalValue.text =
                                "${String.format("%.2f", convertCurrency(subtotal))}"
                            subtotalInt = convertCurrency(subtotal)

                        } else {
                            binding.subTotalValue.text = "${String.format("%.2f", subtotal)}"
                            binding.totalValue.text = "${String.format("%.2f", subtotal)}"
                            subtotalInt = subtotal

                        }
                    }

                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, response.error.message.toString(), Snackbar.LENGTH_SHORT).show()

                    }
                }
            }
        }
        //return address that are selected
        addressFactory =
            AddressFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        addressViewModel =
            ViewModelProvider(this, addressFactory).get(AddressViewModel::class.java)
        addressViewModel.getAllcustomer(CustomerData.getInstance(requireContext()).id)
        lifecycleScope.launch {
            addressViewModel.products.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> {
                        Log.i("NetworkState", "Loading")
                    }

                    is NetworkState.Success -> {
                        Log.i("NetworkState", "Success: ${response.data.customer.id}")
                        val (defaultAddresses, nonDefaultAddresses) = response.data.customer.addresses.partition { it.default }
                        filteredAddresses = defaultAddresses.get(0)
                        Log.i("filteredAddresses", "${filteredAddresses} ")
                    }

                    is NetworkState.Failure -> {
                        Log.i("NetworkState", "Failure${response.error.message}")
                    }
                }
            }
        }
        binding.validate.setOnClickListener {
            validateCoupon()
        }

        binding.paymentButtonContainer.setOnClickListener {
            placeOrder()
            showPaymentMethodDialog()
        }
    }

    private fun convertCurrency(amount: Double?): Double {
        amount ?: return 0.0 // Handle null or undefined amount gracefully
        return amount / currencyConversionRate
    }

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
                        Snackbar.make(binding.root, response.error.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateCoupon() {
        allCodesViewModel.getAdsCode()
        Log.i("getAdsCode", "titlesList: $titlesList")
        val couponCode = binding.coupon.text.toString()

        if (couponCode in titlesList) {
            val coupon = couponCode
           // Toast.makeText(requireContext(), "Coupon Applied Successfully", Toast.LENGTH_LONG).show()
            Snackbar.make(binding.root,"Coupon Applied Successfully", Snackbar.LENGTH_SHORT).show()

            lifecycleScope.launch {
                allCodesViewModel.productCode.collectLatest { response ->
                    when (response) {
                        is NetworkState.Loading -> {}
                        is NetworkState.Success -> {
                            val value = response.data
                            for (rule in value.price_rules) {
                                if (rule.title == coupon) {
                                    val greenColor = ContextCompat.getColor(requireContext(), R.color.green)
                                    binding.validate.backgroundTintList = ColorStateList.valueOf(greenColor)
                                    binding.validate.text = requireContext().getString(R.string.valid)
                                    val valueOfDis = rule.value.toDoubleOrNull() ?: 0.0
                                    val subtotal = binding.subTotalValue.text.toString().toDoubleOrNull() ?: 0.0
                                    val discountAmount = subtotal * (valueOfDis / 100)
                                    val total = subtotal + discountAmount
                                    binding.discountValue.text = "${String.format("%.2f", kotlin.math.abs(valueOfDis))}%"
                                    binding.totalValue.text = String.format("%.2f", total)
                                    subtotalInt= total
                                    break
                                }
                            }
                        }
                        is NetworkState.Failure -> {
                            Snackbar.make(binding.root, response.error.message.toString(), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            showInvalidCouponDialog()
            val greenColor = ContextCompat.getColor(requireContext(), R.color.gray)
            binding.validate.backgroundTintList = ColorStateList.valueOf(greenColor)
            binding.validate.text = requireContext().getString(R.string.validate)

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
        Snackbar.make(binding.root,"Coupon Code is Invalid", Snackbar.LENGTH_SHORT).show()
      //  Toast.makeText(requireContext(), "Coupon Code is Invalid", Toast.LENGTH_LONG).show()
        Log.i("Coupon", "False: ")
    }

    fun paymentVisa(){
        var customer = CustomerData.getInstance(requireContext())
        allPaymentViewModel.getPaymentProducts("https://example.com/cancel","https://example.com/cancel",customer.email,customer.currency,"Your Order","Please Write your Card Information",
            subtotalInt.toInt()*100,1,"payment","card")

        lifecycleScope.launch {
            allPaymentViewModel.productPayment.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> "showLoading()"
                    is NetworkState.Success -> {
                        val paymentUrl = response.data.url
                        // loadPaymentUrl(paymentUrl)
                        showPaymentSheet(paymentUrl)
                    }
                    is NetworkState.Failure -> ""
                    else -> { }
                }
            }}
    }

    private fun showPaymentSheet(paymentUrl: String) {
        val paymentSheetFragment = PaymentSheetFragment.newInstance(paymentUrl)
        paymentSheetFragment.show(childFragmentManager, "PaymentSheetFragment")
    }
    private fun showPaymentMethodDialog() {
        val options = arrayOf("Visa", "Cash")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Your Payment Method")
            .setSingleChoiceItems(options, -1) { dialog, which ->
                val selectedPaymentMethod = when (which) {
                    0 -> "Visa"
                    1 -> "Cash"
                    else -> ""
                }
                processPayment(selectedPaymentMethod)

                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun processPayment(paymentMethod: String) {
        when (paymentMethod) {
            "Visa" -> {
                paymentVisa()
            }
            "Cash" -> {
                findNavController().navigate(R.id.actiomfromSheet_to_order)
                Snackbar.make(binding.root,"You Choose Cash Method Succssed Orde", Snackbar.LENGTH_SHORT).show()

            }
            else -> {

            }
        }
    }

    private fun placeOrder() {
        val subtotal = lineItemsList.sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }

        val address = AddressBody(
            first_name = filteredAddresses.first_name,
            address1 = filteredAddresses.address1,
            phone = filteredAddresses.phone,
            city = filteredAddresses.city,
            zip = filteredAddresses.zip,
            country = filteredAddresses.country,
            last_name = filteredAddresses.last_name,
            name = filteredAddresses.name,
            country_code = filteredAddresses.country_code,
        )


        val defaultAddress = DefaultAddressBody(
            first_name = filteredAddresses.first_name,
            address1 = filteredAddresses.address1,
            phone = filteredAddresses.phone,
            city = filteredAddresses.city,
            zip = filteredAddresses.zip,
            country = filteredAddresses.country,
            last_name = filteredAddresses.last_name,
            name = filteredAddresses.name,
            country_code = filteredAddresses.country_code,
            default = true
        )

        val customer = CustomerBody(
            id = CustomerData.getInstance(requireContext()).id,
            email = CustomerData.getInstance(requireContext()).email,
            first_name = CustomerData.getInstance(requireContext()).name,
            last_name = CustomerData.getInstance(requireContext()).name,
            currency = CustomerData.getInstance(requireContext()).currency,
            default_address = defaultAddress
        )

        val lineItem = lineItemsList.map { lineItem ->
            LineItemBody(
                variant_id = lineItem.variant_id,
                quantity = lineItem.quantity,
                id = lineItem.id,
                title = lineItem.title,
                price = lineItem.price,
                sku = lineItem.sku

            )
        }
        val orderBody = OrderBody(
            billing_address = address,
            customer = customer,
            line_items = lineItem,
            total_tax = 13.5,
            currency = CustomerData.getInstance(requireContext()).currency
        )
        val wrappedOrderBody = mapOf("order" to orderBody)

        allCodesViewModel.createOrder(wrappedOrderBody,
            onSuccess = {
                Snackbar.make(binding.root,"Order placed successfully", Snackbar.LENGTH_SHORT).show()
                Log.d("placeOrder", "success")
                allProductViewModel.deleteAllCartProducts()
            },
            onError = { errorMessage ->
                Snackbar.make(binding.root,errorMessage, Snackbar.LENGTH_SHORT).show()
                Log.d("placeOrder", "error: $errorMessage")
            }
        )
    }

}


