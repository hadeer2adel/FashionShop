package com.example.fashionshop

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.fashionshop.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() , OnBackPressedListener{
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Set up the toolbar
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        binding.AddressButton.setOnClickListener {
//            val secondFragment = AddressFragment()
//            fragmentManager?.beginTransaction()?.apply {
//                replace(R.id.fragmentContainerView, secondFragment)
//                addToBackStack(null)
//                commit()
//            }
            navController.navigate(R.id.action_profileFragment_to_addressFragment)

        }

        binding.CurrencyButton.setOnClickListener {
            showCurrencyDialog()
        }

        binding.AboutUsButton.setOnClickListener {
//            val secondFragment = AboutFragment()
//            fragmentManager?.beginTransaction()?.apply {
//                replace(R.id.fragmentContainerView, secondFragment)
//                addToBackStack(null)
//                commit()
//            }
            navController.navigate(R.id.action_profileFragment_to_AboutFragment)

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressed() {
        parentFragmentManager.popBackStack()
    }
    private fun showCurrencyDialog() {
        val options = arrayOf("EGY", "USD")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Currency")
            .setItems(options) { dialog, which ->
                val selectedCurrency = options[which]
                Toast.makeText(requireContext(), "Selected: $selectedCurrency", Toast.LENGTH_SHORT).show()
                // Handle the selection (e.g., store the selected currency or update the UI)
            }
        builder.create().show()
    }
}
