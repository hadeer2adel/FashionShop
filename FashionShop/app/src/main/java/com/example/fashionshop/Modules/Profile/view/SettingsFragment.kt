package com.example.fashionshop.Modules.Profile.view

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.fashionshop.MainActivity
import com.example.fashionshop.OnBackPressedListener
import com.example.fashionshop.R
import com.example.fashionshop.Service.Caching.SharedPreferenceManager
import com.example.fashionshop.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : Fragment() , OnBackPressedListener {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var isLanguageChanging = false

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

        binding.switchLanguageArabic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SharedPreferenceManager.getInstance(requireContext()).saveLanguageUnit("ar")
                setAppLanguage("ar")
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        }
        binding.switchLanguageEnglish.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SharedPreferenceManager.getInstance(requireContext()).saveLanguageUnit("en")
                setAppLanguage("en")
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
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
    private fun setAppLanguage(languageCode: String) {
        val currentLocale = Locale.getDefault()
        val newLocale = Locale(languageCode)
        val sharedPreferencesManager = SharedPreferenceManager.getInstance(requireContext())
        sharedPreferencesManager.saveLanguage(languageCode)
        if (currentLocale != newLocale && !isLanguageChanging) {
            isLanguageChanging = true
            Locale.setDefault(newLocale)
            val configuration = Configuration()
            configuration.locale = newLocale
            val resources: Resources = requireContext().resources
            resources.updateConfiguration(configuration, resources.displayMetrics)
            requireActivity().recreate()
        }
    }

}