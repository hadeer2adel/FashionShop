package com.example.fashionshop.Modules.Address.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.databinding.FragmentAddNewAddressBinding
import com.example.fashionshop.viewModels.AddNewAddressFactory
import com.example.fashionshop.viewModels.AddNewAddressViewModel

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

            // Set retrieved values to the input fields if they exist
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
//                address1 = "1 Mahada St",
//                address2 = "34 building",
//                city = "Cairo",
//                company = "Fancy Co.",
//                first_name = "John",
//                last_name = "Doe",
//                phone = "819-555-5555",
//                province = "",
//                country = "Egypt",
//                zip = "G1R 4P5",
//                name = "John Doe",
//                province_code = "", // Adjust if needed
//                country_code = "EG", // Adjust if needed
//                country_name = "Egypt", // Adjust if needed
//                id = 3 ,
//                customer_id = 7371713577180 ,
//                default = false

                address1 = binding.etAddress1.text.toString(),
                address2 = binding.etAddress2.text.toString(),
                city = binding.etCity.text.toString(),
                company = binding.etCompany.text.toString(),
                first_name = binding.etFirstName.text.toString(),
                last_name = binding.etLastName.text.toString(),
                phone = binding.etPhone.text.toString(), // Adjust as needed
                province = "", // Adjust as needed
                country = binding.etCountry.text.toString(),
                zip = binding.etZip.text.toString(),
                name = binding.etName.text.toString(),
                province_code = "", // Adjust as needed
                country_code = binding.etCountryCode.text.toString(),
                country_name = binding.etCountryName.text.toString(),
                id = 3, // Adjust as needed
                customer_id = 7371713577180, // Adjust as needed
                default = false
            )
            findNavController().navigate(R.id.action_from_map_to_newAddresses)

        }

        binding.mapIcon.setOnClickListener {

            findNavController().navigate(R.id.action_from_AddnewAddress_to_Map)


        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}