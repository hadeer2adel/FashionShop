package com.example.fashionshop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Adapters.AddressAdapter
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.viewModels.AddressFactory
import com.example.fashionshop.viewModels.AddressViewModel
import com.example.fashionshop.databinding.FragmentAddressBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddressFragment : Fragment(), OnBackPressedListener {
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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

        // Initialize RecyclerView and LayoutManager
        mAdapter = AddressAdapter() // Initialize adapter without any arguments
        binding.recyclerViewAddresses.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }

        allProductFactory = AddressFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(AddressViewModel::class.java)

        // Observe LiveData for address list updates
        allProductViewModel.products.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                Log.i("TAG", "Data updated. Size: ${value.customer.id}")
                mAdapter.setAddressList(value.customer.addresses)
                mAdapter.notifyDataSetChanged()
            }
        })


        binding.buttonAddAddress.setOnClickListener {
        findNavController().navigate(R.id.Adress_to_AddAddressFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddressFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onBackPressed() {
        parentFragmentManager.popBackStack()
    }
}
