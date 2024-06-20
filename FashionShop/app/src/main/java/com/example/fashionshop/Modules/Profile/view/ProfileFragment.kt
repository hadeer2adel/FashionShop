package com.example.fashionshop.Modules.Profile.view

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
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Modules.Address.viewModel.AddressFactory
import com.example.fashionshop.Modules.Address.viewModel.AddressViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.View.showDialog
import com.example.fashionshop.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    // Declare the binding object
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var allProductFactroy: AddressFactory
    lateinit var allProductViewModel: AddressViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using view binding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
        appBarConfiguration = AppBarConfiguration(navController.graph)

//        // Set up the toolbar
//        val toolbar = binding.toolbar
//        setupWithNavController(toolbar, navController, appBarConfiguration)


        allProductFactroy= AddressFactory(
            RepositoryImp.getInstance(
                NetworkManagerImp.getInstance()
            )
        )
        allProductViewModel= ViewModelProvider(this, allProductFactroy).get(AddressViewModel::class.java)
        allProductViewModel.getAllcustomer(CustomerData.getInstance(requireContext()).id)
        lifecycleScope.launch {
            allProductViewModel.products.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is NetworkState.Success -> {
                        Log.i("TAG", "Data updated. Size: ${response.data.customer.id}")
                        binding.nameCustomer.text = response.data.customer.first_name + " " + response.data.customer.last_name
                        binding.emailCustomer.text = response.data.customer.email

                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(requireView(),response.error.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            } }

        binding.ordersButton.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_ordersFragment)
        }


        binding.settings.setOnClickListener {

            navController.navigate(R.id.action_profileFragment_to_settingFragment)
        }

        binding.logout.setOnClickListener {
            val onAllow: () -> Unit = {
                FirebaseAuth.getInstance().signOut()
                CustomerData.getInstance(requireContext()).logOut()
                findNavController().navigate(R.id.homeFragment)
            }
            showDialog(
                requireContext(),
                R.string.Logout,
                R.string.logout_body,
                onAllow
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Nullify the binding object to avoid memory leaks
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    // Add any parameters if necessary
                }
            }
    }
}