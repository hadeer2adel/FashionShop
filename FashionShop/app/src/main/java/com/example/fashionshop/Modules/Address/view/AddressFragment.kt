package com.example.fashionshop.Modules.Address.view

import android.app.AlertDialog
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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Adapters.AddressAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Modules.Address.viewModel.AddressFactory
import com.example.fashionshop.Modules.Address.viewModel.AddressViewModel
import com.example.fashionshop.OnBackPressedListener
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentAddressBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddressFragment : Fragment(), OnBackPressedListener ,AddressListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: FragmentAddressBinding
    private lateinit var allProductFactory: AddressFactory
    private lateinit var allProductViewModel: AddressViewModel
    private lateinit var mAdapter: AddressAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Set up the toolbar
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        mAdapter = AddressAdapter(this,true)
        binding.recyclerViewAddresses.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }

        allProductFactory =
            AddressFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(AddressViewModel::class.java)
        allProductViewModel.getAllcustomer(CustomerData.getInstance(requireContext()).id)
        lifecycleScope.launch {
            allProductViewModel.products.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerViewAddresses.visibility = View.GONE
                    }
                    is NetworkState.Success -> {
                        binding.recyclerViewAddresses.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        Log.i("TAG", "Data updated. Size: ${response.data.customer.id}")
                        val (defaultAddresses, nonDefaultAddresses) = response.data.customer.addresses.partition { it.default }
                        val filteredAddresses = defaultAddresses + nonDefaultAddresses
                        mAdapter.setAddressList(filteredAddresses)
                        mAdapter.notifyDataSetChanged()


                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } }




//
//        allProductViewModel.products.observe(viewLifecycleOwner, Observer { value ->
//            value?.let {
//                Log.i("TAG", "Data updated. Size: ${value.customer.id}")
//                // Split addresses into two lists: defaultAddresses and nonDefaultAddresses
//                val (defaultAddresses, nonDefaultAddresses) = value.customer.addresses.partition { it.default }
//                // Concatenate the lists with defaultAddresses first
//
//                val filteredAddresses = defaultAddresses + nonDefaultAddresses
//                mAdapter.setAddressList(filteredAddresses)
//                mAdapter.notifyDataSetChanged()
//            }
//        })


        binding.buttonAddAddress.setOnClickListener {
        findNavController().navigate(R.id.Adress_to_AddAddressFragment)
        }
    }

    private fun refreshFragment() {
        allProductViewModel.getAllcustomer(CustomerData.getInstance(requireContext()).id)
        lifecycleScope.launch {
            allProductViewModel.products.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerViewAddresses.visibility = View.GONE
                    }
                    is NetworkState.Success -> {
                        binding.recyclerViewAddresses.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        Log.i("TAG", "Data updated. Size: ${response.data.customer.id}")
                        val (defaultAddresses, nonDefaultAddresses) = response.data.customer.addresses.partition { it.default }
                        val filteredAddresses = defaultAddresses + nonDefaultAddresses
                        mAdapter.setAddressList(filteredAddresses)
                        mAdapter.notifyDataSetChanged()

                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } }
    }
    companion object {

    }

    override fun onBackPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun deleteAddress(addressId: Long) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        val currentAddress = mAdapter.getAddressList().find { it.id == addressId }
        if (currentAddress != null && currentAddress.default) {
            alertDialogBuilder.apply {
                setTitle("Cannot Delete Default Address")
                setMessage("The default address cannot be deleted.")
                setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
            }
        } else {
            alertDialogBuilder.apply {
                setTitle("Delete Address")
                setMessage("Are you sure you want to delete this address?")
                setPositiveButton("Delete") { dialog, _ ->
                    dialog.dismiss()
                allProductViewModel.senddeleteAddressRequest(CustomerData.getInstance(requireContext()).id,addressId)
                refreshFragment()
                }
            }
        }
        alertDialogBuilder.create().show()
    }

    override fun setAddressDefault(id:Long,default: Boolean) {
        allProductViewModel.sendeditAddressRequest(id,default,CustomerData.getInstance(requireContext()).id)
        Toast.makeText(requireContext(), "Address Preeesed Successfully", Toast.LENGTH_LONG).show()
        refreshFragment()

    }


}