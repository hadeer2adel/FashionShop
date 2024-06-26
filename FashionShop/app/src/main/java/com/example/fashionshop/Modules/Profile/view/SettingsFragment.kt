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
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.fashionshop.MainActivity
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.OnBackPressedListener
import com.example.fashionshop.R
import com.example.fashionshop.Service.Caching.SharedPreferenceManager
import com.example.fashionshop.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
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
            navController.navigate(R.id.action_profileFragment_to_addressFragment)

        }

        binding.CurrencyButton.setOnClickListener {
            showCurrencyDialog()
        }

        binding.AboutUsButton.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_AboutFragment)

        }
        binding.LanguagesButton.setOnClickListener {
            showLanguageDialog()
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
        builder.setTitle(getString(R.string.choose_currency_title))
            .setItems(options) { dialog, which ->
                val selectedCurrency = options[which]
                CustomerData.getInstance(requireContext()).currency = selectedCurrency
                Snackbar.make(requireView(),requireContext().getString(R.string.selected)+": $selectedCurrency", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.homeFragment)
            }
        builder.create().show()
    }
    private fun showLanguageDialog() {
        val options = arrayOf("Arabic", "English")
        val currentLanguage = CustomerData.getInstance(requireContext()).languageCode

        //    SharedPreferenceManager.getInstance(requireContext()).getLanguageUnit()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(requireContext().getString(R.string.choose_language))
            .setSingleChoiceItems(options, options.indexOf(getLanguageLabel(currentLanguage))) { dialog, which ->
                val selectedLanguage = when (which) {
                    0 -> "ar"
                    1 -> "en"
                    else -> "en" // Default to English if unexpected selection
                }
                CustomerData.getInstance(requireContext()).languageCode = selectedLanguage
               // SharedPreferenceManager.getInstance(requireContext()).saveLanguageUnit(selectedLanguage)
                setAppLanguage(selectedLanguage)
                startActivity(Intent(requireContext(), MainActivity::class.java))

                dialog.dismiss() // Dismiss dialog after selection
            }
        val dialog = builder.create()
        dialog.show()

        // Highlight the current language
        dialog.listView?.setOnItemClickListener { parent, view, position, id ->
            val selectedLanguage = when (position) {
                0 -> "ar"
                1 -> "en"
                else -> "en" // Default to English if unexpected selection
            }
            CustomerData.getInstance(requireContext()).languageCode = selectedLanguage

          //  SharedPreferenceManager.getInstance(requireContext()).saveLanguageUnit(selectedLanguage)
            setAppLanguage(selectedLanguage)
            startActivity(Intent(requireContext(), MainActivity::class.java))

            dialog.dismiss() // Dismiss dialog after selection
        }
    }

    private fun getLanguageLabel(languageCode: String?): String {
        return when (languageCode) {
            "ar" -> "Arabic"
            "en" -> "English"
            else -> "English" // Default to English if unexpected language code
        }
    }

    private fun setAppLanguage(languageCode: String) {
        val currentLocale = Locale.getDefault()
        val newLocale = Locale(languageCode)

        CustomerData.getInstance(requireContext()).language = languageCode
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