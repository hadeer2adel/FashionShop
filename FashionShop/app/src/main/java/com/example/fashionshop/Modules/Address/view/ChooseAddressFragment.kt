package com.example.fashionshop.Modules.Address.view
import CartFragmentArgs
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Adapters.AddressAdapter
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.LineItem
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.databinding.FragmentChooseAddressBinding
import com.example.fashionshop.Modules.Address.viewModel.AddressFactory
import com.example.fashionshop.Modules.Address.viewModel.AddressViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Service.Networking.NetworkState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChooseAddressFragment : Fragment() , AddressListener{
    lateinit var allProductFactroy: AddressFactory
    lateinit var allProductViewModel: AddressViewModel
    private var _binding: FragmentChooseAddressBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdapter: AddressAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var draftOrderIds: List<Long>
    private var dynamicAddresse: Addresse? = null // Initialize with null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args = ChooseAddressFragmentArgs.fromBundle(requireArguments())
        draftOrderIds = args.draftOrderIds
        _binding = FragmentChooseAddressBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.recyclerChooseAddrees.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = AddressAdapter(this, false)
        mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerChooseAddrees.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }

        allProductFactroy =
            AddressFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allProductViewModel =
            ViewModelProvider(this, allProductFactroy).get(AddressViewModel::class.java)
        allProductViewModel.getAllcustomer(CustomerData.getInstance(requireContext()).id)
        lifecycleScope.launch {
            allProductViewModel.products.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerChooseAddrees.visibility = View.GONE
                    }

                    is NetworkState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerChooseAddrees.visibility = View.VISIBLE
                        Log.i("TAG", "Data updated. Size: ${response.data.customer.id}")
                        val (defaultAddresses, nonDefaultAddresses) = response.data.customer.addresses.partition { it.default }
                        val filteredAddresses = defaultAddresses + nonDefaultAddresses
                        mAdapter.setAddressList(filteredAddresses)
                        mAdapter.notifyDataSetChanged()

                    }

                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Log.i("ChooseAddressFragment", "refreshFragment: ${response.error.message}")
                        //Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.buttonContinueToPayment.setOnClickListener {
            // findNavController().navigate(R.id.action_AdressFragment_to_paymentFragment)
            findNavController().navigate(R.id.action_Payment_to_orderDetailsFragment)

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    private fun refreshFragment() {
        allProductViewModel.getAllcustomer(CustomerData.getInstance(requireContext()).id)
        lifecycleScope.launch {
            allProductViewModel.products.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerChooseAddrees.visibility = View.GONE
                    }
                    is NetworkState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerChooseAddrees.visibility = View.VISIBLE
                        Log.i("TAG", "Data updated. Size: ${response.data.customer.id}")
                        val (defaultAddresses, nonDefaultAddresses) = response.data.customer.addresses.partition { it.default }
                        val filteredAddresses = defaultAddresses + nonDefaultAddresses
                        mAdapter.setAddressList(filteredAddresses)
                        mAdapter.notifyDataSetChanged()


                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Log.i("ChooseAddressFragment", "refreshFragment: ${response.error.message}")
                        // Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun deleteAddress(addressId: Long) {

    }

    override fun setAddressDefault(id:Long,default: Boolean) {
        allProductViewModel.sendeditAddressRequest(id,default,CustomerData.getInstance(requireContext()).id)
        Snackbar.make(binding.root, requireContext().getString(R.string.d_address_success), Snackbar.LENGTH_SHORT).show()
        //Toast.makeText(requireContext(), "Address Preeesed Successfully", Toast.LENGTH_LONG).show()
        refreshFragment()
    }

}


