package com.example.fashionshop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.fashionshop.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    // Declare the binding object
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

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

        // Set up the toolbar
        val toolbar = binding.toolbar
        setupWithNavController(toolbar, navController, appBarConfiguration)



        binding.ordersButton.setOnClickListener {
            Toast.makeText(requireContext(), "Orders!", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.action_profileFragment_to_ordersFragment)
        }

        binding.favoriteButton.setOnClickListener {
            Toast.makeText(requireContext(), "Favorites!", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.action_profileFragment_to_favoritesFragment)
        }

        binding.settings.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_settingFragment)
        }

        binding.logout.setOnClickListener {
            Toast.makeText(requireContext(), "Logout!", Toast.LENGTH_SHORT).show()
            // Perform logout action
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
