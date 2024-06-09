package com.example.fashionshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.databinding.FragmentAddNewAddressBinding
import com.example.fashionshop.viewModels.AddNewAddressFactory
import com.example.fashionshop.viewModels.AddNewAddressViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddNewAddressFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var allProductFactory: AddNewAddressFactory
    private lateinit var allProductViewModel: AddNewAddressViewModel
    private var _binding: FragmentAddNewAddressBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentAddNewAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allProductFactory = AddNewAddressFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(AddNewAddressViewModel::class.java)

//        // Access views using binding
//        binding.buttonSendRequest.setOnClickListener {
//            // Trigger ViewModel method to add new address
//            allProductViewModel.addSingleCustomerAddress(
//                binding.etAddress1.text.toString(),
//                binding.etAddress2.text.toString(),
//                binding.etCity.text.toString(),
//                binding.etCompany.text.toString(),
//                binding.etFirstName.text.toString(),
//                binding.etLastName.text.toString(),
//                binding.etPhone.text.toString(),
//                binding.etProvince.text.toString(),
//                binding.etCountry.text.toString(),
//                binding.etZip.text.toString(),
//                binding.etName.text.toString()
//            )
//        }
// Trigger ViewModel method to add new address
        binding.buttonSendRequest.setOnClickListener {
            allProductViewModel.addSingleCustomerAddress(
//                binding.etAddress1.text.toString(),
//                binding.etAddress2.text.toString(),
//                binding.etCity.text.toString(),
//                binding.etCompany.text.toString(),
//                binding.etFirstName.text.toString(),
//                binding.etLastName.text.toString(),
//                binding.etPhone.text.toString(),
//                binding.etProvince.text.toString(),
//                binding.etCountry.text.toString(),
//                binding.etZip.text.toString(),
//                binding.etName.text.toString()

                 "123 Nile Street",
             "Apartment 456",
             "Giza",
             "Fashion Co.",
             "Ahmed",
             "Saleh",
             "123-456-7890",
             "Giza Province",
             "Egypt",
             "12345",
             "Ahmed Saleh",
            "GZ",
             "EG",
            "Egypt"




            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddNewAddressFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
