package com.example.fashionshop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionshop.databinding.FragmentOrderDetailsBinding
import com.example.fashionshop.databinding.FragmentProfileBinding

class OrderDetailsFragment : Fragment() {

    // Declare the binding object
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


}