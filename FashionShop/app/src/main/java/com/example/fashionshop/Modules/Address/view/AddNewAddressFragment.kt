package com.example.fashionshop.Modules.Address.view

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Modules.Address.viewModel.AddNewAddressFactory
import com.example.fashionshop.Modules.Address.viewModel.AddNewAddressViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.View.isNetworkConnected
import com.example.fashionshop.databinding.FragmentAddNewAddressBinding

class AddNewAddressFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var allProductFactory: AddNewAddressFactory
    private lateinit var allProductViewModel: AddNewAddressViewModel
    private var _binding: FragmentAddNewAddressBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNetworkConnected(requireContext())) {

            allProductFactory =
                AddNewAddressFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
            allProductViewModel = ViewModelProvider(
                this,
                allProductFactory
            ).get(AddNewAddressViewModel::class.java)
            arguments?.let {
                val address1 = it.getString("address1")
                val address2 = it.getString("address2")
                val city = it.getString("city")
                val zip = it.getString("zip")
                val country = it.getString("country")
                val country_code = it.getString("country_code")
                val default = it.getBoolean("default")
                val first_name = it.getString("first_name")
                val last_name = it.getString("last_name")
                val company = it.getString("company")
                val name = it.getString("name")
                val country_name = it.getString("country_name")

                if (address1 != null) binding.etAddress1.setText(address1)
                if (address2 != null) binding.etAddress2.setText(address2)
                if (city != null) binding.etCity.setText(city)
                if (zip != null) binding.etZip.setText(zip)
                if (country != null) binding.etCountry.setText(country)
                if (country_code != null) binding.etCountryCode.setText(country_code)
                if (first_name != null) binding.etFirstName.setText(first_name)
                if (last_name != null) binding.etLastName.setText(last_name)
                if (company != null) binding.etCompany.setText(company)
                if (name != null) binding.etName.setText(name)
                if (country_name != null) binding.etCountryName.setText(country_name)
            }
            binding.buttonSendRequest.setOnClickListener {
                allProductViewModel.sendAddressRequest(
                    address1 = binding.etAddress1.text.toString(),
                    address2 = binding.etAddress2.text.toString(),
                    city = binding.etCity.text.toString(),
                    company = binding.etCompany.text.toString(),
                    first_name = binding.etFirstName.text.toString(),
                    last_name = binding.etLastName.text.toString(),
                    phone = binding.etPhone.text.toString(),
                    province = "",
                    country = binding.etCountry.text.toString(),
                    zip = binding.etZip.text.toString(),
                    name = binding.etName.text.toString(),
                    province_code = "", // Adjust as needed
                    country_code = binding.etCountryCode.text.toString(),
                    country_name = binding.etCountryName.text.toString(),
                    id = 3,
                    customer_id = CustomerData.getInstance(requireContext()).id,
                    default = false
                )
                showAlertDialog()
                findNavController().navigate(R.id.homeFragment)

            }

            binding.mapIcon.setOnClickListener {

                findNavController().navigate(R.id.action_from_AddnewAddress_to_Map)
            }
        }
        else
        {
            binding.scroll.visibility = View.GONE
            binding.layoutConnection.visibility = View.VISIBLE
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showAlertDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog_layout_sucessed_addresses, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        alertDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
        }, 4000)
    }
    companion object {

    }
}