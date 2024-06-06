package com.example.fashionshop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.model.Addresse
import com.example.fashionshop.network.RemoteDataSourceImp
import com.example.fashionshop.repository.RepositoryImp
import com.example.fashionshop.viewModels.AddressFactory
import com.example.fashionshop.viewModels.AddressViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddressFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddressFragment : Fragment() ,OnBackPressedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var allProductFactroy:AddressFactory
    lateinit var allProductViewModel: AddressViewModel
    lateinit var mAdapter: AddressAdapter
    lateinit var rv: RecyclerView
    lateinit var mLayoutManager: LinearLayoutManager
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        // Initialize RecyclerView and LayoutManager
        rv = view.findViewById(R.id.recyclerViewAddresses) // Assuming your RecyclerView ID is "recyclerView"
        mAdapter = AddressAdapter() // Initialize adapter without any arguments
        rv.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
        allProductFactroy=AddressFactory(RepositoryImp.getInstance(
            RemoteDataSourceImp.getInstance()))
        allProductViewModel=ViewModelProvider(this,allProductFactroy).get(AddressViewModel::class.java)
        // Observe LiveData for address list updates
        allProductViewModel.products.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                Log.i("TAG", "Data updated. Size: ${value.customers.size}")
                mAdapter.setAddressList(value.customers[0].addresses)
                mAdapter.notifyDataSetChanged()
            }
        })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddressFragment.
         */
        // TODO: Rename and change types and number of parameters
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