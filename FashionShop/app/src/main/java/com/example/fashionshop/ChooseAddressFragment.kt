package com.example.fashionshop
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionshop.databinding.FragmentChooseAddressBinding

class ChooseAddressFragment : Fragment() {

    private var _binding: FragmentChooseAddressBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseAddressBinding.inflate(inflater, container, false)
        val view = binding.root

        // Sample data for RecyclerView (replace with your actual data)
        val addressList = listOf(
            Address("Egypt", "24 Elgeash st", "01077798865"),
            Address("USA", "123 Main St", "123-456-7890"),
            Address("UK", "1 Oxford St", "987-654-3210")
        )

        // Set up RecyclerView
        val adapter = AddressAdapter(addressList)
        binding.recyclerChooseAddrees.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerChooseAddrees.adapter = adapter
binding.buttonContinueToPayment.setOnClickListener {
    findNavController().navigate(R.id.action_AdressFragment_to_paymentFragment)
}
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

