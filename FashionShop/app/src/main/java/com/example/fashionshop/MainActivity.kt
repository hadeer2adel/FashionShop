package com.example.fashionshop

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Modules.Authentication.view.LoginActivity
import com.example.fashionshop.Service.Caching.SharedPreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        val savedLanguage = CustomerData.getInstance(this).language
        if (!savedLanguage.isNullOrBlank()) {
            val locale = Locale(savedLanguage)
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }else {
            // If no language is saved, use the device's default language
            val deviceLocale = resources.configuration.locale
            Locale.setDefault(deviceLocale)
            val configuration = Configuration()
            configuration.locale = deviceLocale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)
    }
}