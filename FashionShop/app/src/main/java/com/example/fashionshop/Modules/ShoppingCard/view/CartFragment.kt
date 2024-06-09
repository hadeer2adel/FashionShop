package com.example.fashionshop.Modules.ShoppingCard.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Adapters.AddressAdapter
import com.example.fashionshop.Adapters.CartAdapter
import com.example.fashionshop.Model.CartItem
import com.example.fashionshop.Modules.Address.viewModel.AddressFactory
import com.example.fashionshop.Modules.Address.viewModel.AddressViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var allProductFactory: CartFactory
    private lateinit var allProductViewModel: CartViewModel
    private lateinit var mAdapter: CartAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val view = binding.root

        // Sample cart items
//        val cartItems = listOf(
//            CartItem(name = "Item 1", price = 19.99, quantity = 1),
//            CartItem(name = "Item 2", price = 9.99,quantity = 1),
//            CartItem(name = "Item 3", price = 14.99,quantity = 1)
//        )
        mAdapter = CartAdapter()
        mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerViewCartItems.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
        allProductFactory =
            CartFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(CartViewModel::class.java)
        allProductViewModel.products.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                Log.i("TAG", "Data updated. Size: ${value.draft_orders}")
                val draftOrderList = value.draft_orders.map { listOf(it) } // Wrap each DraftOrder in a list
                mAdapter.setCartList(value.draft_orders)
                mAdapter.notifyDataSetChanged()
            }
        })

        binding.buttonCheckout.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_paymentFragment)


        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
