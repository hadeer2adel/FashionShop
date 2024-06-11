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
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.Addresse
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

        binding.buttonSendRequest.setOnClickListener {
            allProductViewModel.sendAddressRequest(
                address1 = "1 Mahada St",
                address2 = "34 building",
                city = "Cairo",
                company = "Fancy Co.",
                first_name = "John",
                last_name = "Doe",
                phone = "819-555-5555",
                province = "",
                country = "Egypt",
                zip = "G1R 4P5",
                name = "John Doe",
                province_code = "", // Adjust if needed
                country_code = "EG", // Adjust if needed
                country_name = "Egypt", // Adjust if needed
                id = 3 ,
                customer_id = 7371713577180 ,
                default = false
            )
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
