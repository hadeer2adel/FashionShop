package com.example.fashionshop.Modules.FavProductList.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionshop.Adapters.ProductAdapter
import com.example.fashionshop.R
import com.example.fashionshop.databinding.FragmentFavoriteBinding


class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onClick: () -> Unit = {}
        val onCardClick: (id: Long) -> Unit = {
            val navController = NavHostFragment.findNavController(this)
            val action = FavoriteFragmentDirections.actionToProductInfoFragment(it)
            navController.navigate(action)
        }


        adapter = ProductAdapter(requireContext(), true, onClick, onCardClick)
        adapter.submitList(emptyList())
        binding.recycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleView.adapter = adapter

    }

}