package com.example.fashionshop.Modules.Payment.view

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsFactory
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PaymentSheetFragment : BottomSheetDialogFragment() {
    private lateinit var allProductViewModel: CartViewModel
    private lateinit var allCodesViewModel: OrderDetailsViewModel
    lateinit var addressFactory: AddressFactory
    lateinit var addressViewModel: AddressViewModel
    lateinit var filteredAddresses : Addresse
    private lateinit var webView: WebView
    private val successUrl = "https://example.com/"
    private var lineItemsList: List<DraftOrderResponse.DraftOrder.LineItem> = emptyList()
    private lateinit var allProductFactory: CartFactory
    var subtotalInt = 0.0
    private var currencyConversionRate: Double = 1.0
    lateinit var allCodesFactory: OrderDetailsFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allCodesFactory =
            OrderDetailsFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCodesViewModel =
            ViewModelProvider(this, allCodesFactory).get(OrderDetailsViewModel::class.java)
        allProductFactory = CartFactory(
            RepositoryImp.getInstance(NetworkManagerImp.getInstance()),
            CustomerData.getInstance(requireContext()).cartListId
        )
        allProductViewModel =
            ViewModelProvider(this, allProductFactory).get(CartViewModel::class.java)
        allProductViewModel.getCardProducts()
        lifecycleScope.launch {
            allProductViewModel.productCard.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> {
                    }

                    is NetworkState.Success -> {
                        lineItemsList = response.data.draft_order.line_items.drop(1)
                        var subtotal = response.data.draft_order.line_items.drop(1)
                            .sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }
                        val customer = CustomerData.getInstance(requireContext())
                        if (customer.currency == "USD") {
                            subtotalInt = convertCurrency(subtotal)

                        } else {
                            subtotalInt = subtotal

                        }
                    }
                    is NetworkState.Failure -> {

                    }
                }
            }
        }
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
        webView = view.findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.startsWith(successUrl)) {
                    handleSuccess()
                    return true
                }
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.startsWith(successUrl)) {
                    handleSuccess()
                    return true
                }
                return false
            }
        }
        webView.webChromeClient = WebChromeClient()

        val paymentUrl = arguments?.getString("paymentUrl")
        paymentUrl?.let {
            webView.loadUrl(it)
        }
    }

    private fun handleSuccess() {
        Snackbar.make(requireView(),"Payment successful", Snackbar.LENGTH_SHORT).show()
        placeOrder()
        dismiss()
        allProductViewModel.deleteAllCartProducts()
        findNavController().navigate(R.id.actiomfromSheet_to_order)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Snackbar.make(requireView(),"Payment process finished", Snackbar.LENGTH_SHORT).show()
    }
    companion object {
        fun newInstance(paymentUrl: String): PaymentSheetFragment {
            val fragment = PaymentSheetFragment()
            val args = Bundle()
            args.putString("paymentUrl", paymentUrl)
            fragment.arguments = args
            return fragment
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
                sku = lineItem.sku,
                properties = lineItem.properties.map { draftProperty ->
                    LineItemBody.Property(
                        name = draftProperty.name,
                        value = draftProperty.value
                    )
                }


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
                showSnackbar("Order placed successfully")
                Log.d("placeOrder", "success")
                allProductViewModel.deleteAllCartProducts()
            },
            onError = { errorMessage ->
                showSnackbar(errorMessage)
                Log.d("placeOrder", "error: $errorMessage")
            }
        )
    }
    private fun showSnackbar(message: String) {
        val parentView = view?.findViewById<View>(android.R.id.content) ?: requireActivity().findViewById(android.R.id.content)
        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show()
    }
    private fun convertCurrency(amount: Double?): Double {
        amount ?: return 0.0
        return amount / currencyConversionRate
    }


}
