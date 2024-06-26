package com.example.fashionshop.Modules.Payment.view
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsFactory
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsViewModel
import com.example.fashionshop.Modules.Payment.viewModel.PaymentFactory
import com.example.fashionshop.Modules.Payment.viewModel.PaymentViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentPaymentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PaymentFragment : Fragment() {
    val titlesList = mutableListOf<String>()
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    lateinit var allCodesFactory: OrderDetailsFactory
    private lateinit var allCodesViewModel: OrderDetailsViewModel
    private lateinit var allPaymentFactory: PaymentFactory
    private lateinit var allPaymentViewModel: PaymentViewModel
    private lateinit var allProductFactory: CartFactory
    private lateinit var allProductViewModel: CartViewModel
    var subtotal = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     //   binding.paymentOptionsLayout.visibility = View.VISIBLE

        allPaymentFactory =
            PaymentFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allPaymentViewModel = ViewModelProvider(this, allPaymentFactory).get(PaymentViewModel::class.java)
        allCodesFactory = OrderDetailsFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCodesViewModel = ViewModelProvider(this, allCodesFactory).get(OrderDetailsViewModel::class.java)
        allProductFactory =
            CartFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()),CustomerData.getInstance(requireContext()).cartListId)
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(CartViewModel::class.java)

        lifecycleScope.launch {
            allProductViewModel.productCard.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> {
                    }
                    is NetworkState.Success -> {
                        subtotal = response.data.draft_order.line_items.drop(1).sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }

                    }
                    is NetworkState.Failure -> {
                        Snackbar.make(binding.root, response.error.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }







        lifecycleScope.launch {
            allCodesViewModel.productCode.collectLatest { response ->
                when(response) {
                    is NetworkState.Loading -> {}
                    is NetworkState.Success -> {
                        val value = response.data.price_rules
                        titlesList.addAll(value.map { it.title })
                        Log.i("getAdsCode", "titlesList: $titlesList")

                    }
                    is NetworkState.Failure -> {
                        Snackbar.make(binding.root, response.error.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }


        binding.visaCardCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cashOnDeliveryCheckBox.isChecked = false
                paymentVisa()
            }




        }

        binding.cashOnDeliveryCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.visaCardCheckBox.isChecked = false
            }
        }

        binding.paymentButton.setOnClickListener {
            val bundle = Bundle().apply {
                putStringArrayList("titlesList", ArrayList(titlesList))
            }
            findNavController().navigate(R.id.action_Payment_to_orderDetailsFragment,bundle)

            when {
                binding.visaCardCheckBox.isChecked -> {
                    Snackbar.make(binding.root, requireContext().getString(R.string.visa_select), Snackbar.LENGTH_SHORT).show()
                }
                binding.cashOnDeliveryCheckBox.isChecked -> {
                    Snackbar.make(binding.root, requireContext().getString(R.string.cash_select), Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    Snackbar.make(binding.root, requireContext().getString(R.string.select_payment_method), Snackbar.LENGTH_SHORT).show()
                }
            }
        }


    }

    fun paymentVisa(){
        var customer = CustomerData.getInstance(requireContext())

        allPaymentViewModel.getPaymentProducts("https://example.com/cancel","https://example.com/cancel",customer.email,customer.currency,"Your Order","Please Write your Card Information",
            subtotal.toInt()*100,1,"payment","card")

        lifecycleScope.launch {
            allPaymentViewModel.productPayment.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> "showLoading()"
                    is NetworkState.Success -> {
                        val paymentUrl = response.data.url
                       // loadPaymentUrl(paymentUrl)
                        showPaymentSheet(paymentUrl , "")
                    }
                    is NetworkState.Failure -> ""
                    else -> { }
                }
            }}












    }


    private fun showPaymentSheet(paymentUrl: String , discountValueBody: String) {
        val paymentSheetFragment = PaymentSheetFragment.newInstance(paymentUrl, discountValueBody)
        paymentSheetFragment.show(childFragmentManager, "PaymentSheetFragment")
    }


//    private fun loadPaymentUrl(paymentUrl: String) {
//        binding.paymentOptionsLayout.visibility = View.GONE
//        binding.webView.apply {
//            visibility = View.VISIBLE
//            settings.javaScriptEnabled = true
//            webViewClient = WebViewClient()
//            webChromeClient = WebChromeClient()
//            loadUrl(paymentUrl)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}