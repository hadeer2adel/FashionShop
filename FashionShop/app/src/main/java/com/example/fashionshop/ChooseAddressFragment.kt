package com.example.fashionshop
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Adapters.AddressAdapter
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.databinding.FragmentChooseAddressBinding
import com.example.fashionshop.viewModels.AddressFactory
import com.example.fashionshop.viewModels.AddressViewModel

class ChooseAddressFragment : Fragment() {
    lateinit var allProductFactroy: AddressFactory
    lateinit var allProductViewModel: AddressViewModel
    private var _binding: FragmentChooseAddressBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdapter: AddressAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseAddressBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.recyclerChooseAddrees.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = AddressAdapter()
        mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerChooseAddrees.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }

        allProductFactroy = AddressFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allProductViewModel = ViewModelProvider(this, allProductFactroy).get(AddressViewModel::class.java)
        allProductViewModel.products.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                Log.i("TAG", "Data updated. Size: ${value.customer.id}")
                mAdapter.setAddressList(value.customer.addresses)
                mAdapter.notifyDataSetChanged()
            }
        })

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

