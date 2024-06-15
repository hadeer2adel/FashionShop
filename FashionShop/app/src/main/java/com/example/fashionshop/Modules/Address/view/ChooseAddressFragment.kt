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
        mAdapter = AddressAdapter(this,false)
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
           findNavController().navigate(R.id.action_AdressFragment_to_paymentFragment )
//            val action = ChooseAddressFragmentDirections.actionChooseAddressFragmentToDestinationFragment(dynamicAddresse)
//            findNavController().navigate(action)
            //error when i click
            if (dynamicAddresse != null) {
                //val action = ChooseAddressFragmentDirections.actionAdressFragmentToPaymentFragment(dynamicAddresse!!)
                //findNavController().navigate(action)
            } else {
                Log.e("ChooseAddressFragment", "dynamicAddresse is null")
                Toast.makeText(requireContext(), "Address is not available", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    private fun refreshFragment() {
        allProductViewModel.products.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                Log.i("TAG", "Data updated. Size: ${value.customer.id}")
                val (defaultAddresses, nonDefaultAddresses) = value.customer.addresses.partition {
                    it.default

                }
                dynamicAddresse = defaultAddresses.firstOrNull() ?: nonDefaultAddresses.firstOrNull()
                val filteredAddresses = defaultAddresses + nonDefaultAddresses
                mAdapter.setAddressList(filteredAddresses)
             //   mAdapter.notifyDataSetChanged()
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun deleteAddress(addressId: Long) {

    }

    override fun setAddressDefault(id:Long,default: Boolean) {
        allProductViewModel.sendeditAddressRequest(id,default)
        Toast.makeText(requireContext(), "Address Preeesed Successfully", Toast.LENGTH_LONG).show()
        refreshFragment()
    }

    override fun sendeditChoosenAddressRequest(
        idd: Long,
        address1: String,
        address2: String,
        city: String,
        company: String,
        country: String,
        country_code: String,
        first_name: String,
        last_name: String,
        latitude: Any,
        longitude: Any,
        name: String,
        phone: String,
        province: String,
        province_code: Any,
        zip: String
    ) {
        Log.i("gggg", "sendeditChoosenAddressRequest:  ${id} ")
            allProductViewModel.sendeditChoosenAddressRequest(
                CustomerData.getInstance(requireContext()).cartListId,
                address1,
                address2,
                city,
                company,
                country,
                country_code,
                first_name,
                last_name,
                latitude,
                longitude,
                name,
                phone,
                province,
                province_code,
                zip
            )


        Toast.makeText(requireContext(), "Address Preeesed Successfully", Toast.LENGTH_LONG).show()
        refreshFragment()
    }
}

