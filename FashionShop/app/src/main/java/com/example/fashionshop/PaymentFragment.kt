package com.example.fashionshop
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fashionshop.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private val args : PaymentFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        val view = binding.root

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
            findNavController().navigate(R.id.action_Payment_to_orderDetailsFragment)

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val selectedAddress = args.dynamicAddresse
       // Toast.makeText(activity, "${selectedAddress.city}", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}