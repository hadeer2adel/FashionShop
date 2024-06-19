package com.example.fashionshop
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsFactory
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsViewModel
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentPaymentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PaymentFragment : Fragment() {
    val titlesList = mutableListOf<String>()
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!


    lateinit var allCodesFactory: OrderDetailsFactory
    private lateinit var allCodesViewModel: OrderDetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allCodesFactory = OrderDetailsFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCodesViewModel = ViewModelProvider(this, allCodesFactory).get(OrderDetailsViewModel::class.java)
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
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.visaCardCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cashOnDeliveryCheckBox.isChecked = false
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
                    Toast.makeText(activity, "Visa Card selected", Toast.LENGTH_SHORT).show()
                }
                binding.cashOnDeliveryCheckBox.isChecked -> {
                    Toast.makeText(activity, "Cash on Delivery selected", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(activity, "Please select a payment method", Toast.LENGTH_SHORT).show()
                }
            }
        }



    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}