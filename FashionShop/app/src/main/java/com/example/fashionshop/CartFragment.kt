package com.example.fashionshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionshop.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val view = binding.root

        // Sample cart items
        val cartItems = listOf(
            CartItem(name = "Item 1", price = 19.99, quantity = 1),
            CartItem(name = "Item 2", price = 9.99,quantity = 1),
            CartItem(name = "Item 3", price = 14.99,quantity = 1)
        )

        // Setup RecyclerView
        binding.recyclerViewCartItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCartItems.adapter = CartAdapter(cartItems)

        // Handle checkout button click
        binding.buttonCheckout.setOnClickListener {
            // Handle checkout action
            findNavController().navigate(R.id.action_cartFragment_to_paymentFragment)


        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
