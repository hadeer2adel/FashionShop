package com.example.fashionshop.Modules.Address.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fashionshop.R
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.viewModels.AddNewAddressFactory
import com.example.fashionshop.viewModels.AddNewAddressViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    private val egyptLatLng = LatLng(26.8206, 30.8025) // Center of Egypt
    private lateinit var address1: String
    private lateinit var address2: String
    private lateinit var city: String
    private lateinit var company: String
    private lateinit var first_name: String
    private lateinit var last_name: String
    private lateinit var phone: String
    private var province: String = ""
    private lateinit var country: String
    private lateinit var zip: String
    private lateinit var name: String
    private var province_code: String = ""
    private lateinit var country_code: String
    private lateinit var country_name: String
    private var id: Long = 1
    private var customer_id: Long = 7371713577180
    private var default: Boolean = false
    private lateinit var allProductFactory: AddNewAddressFactory
    private lateinit var allProductViewModel: AddNewAddressViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: com.google.android.gms.maps.GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } else {
                Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(egyptLatLng, 6f))

        allProductFactory = AddNewAddressFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(AddNewAddressViewModel::class.java)

        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f))
            val selectedLatitude = latLng.latitude
            val selectedLongitude = latLng.longitude
            val geocoder = Geocoder(requireContext())
            val addresses = geocoder.getFromLocation(selectedLatitude, selectedLongitude, 2)
            Log.i("TAGMap", addresses.toString())
            Log.i("TAGMap", addresses?.get(0).toString() ?: "null")
            if (addresses != null && addresses.isNotEmpty()) {
                address1 = addresses[0].getAddressLine(0)
                address2 = addresses[1].getAddressLine(0)
                city = addresses[0].adminArea
                zip = addresses[0].postalCode
                country = addresses[0].countryName
                country_code = addresses[0].countryCode
                default = false
                first_name = "hader"
                last_name = "mo"
                company = addresses[0].featureName
                name = addresses[0].adminArea
                country_name = addresses[0].countryName
                val cityName = addresses[0].getAddressLine(0)
                val lat = addresses[0].latitude
                val lon = addresses[0].longitude
                Toast.makeText(requireContext(), "City: $cityName, Longitude: $lat", Toast.LENGTH_LONG).show()
            }
            val bundle = Bundle().apply {
                putString("address1", address1)
                putString("address2", address2)
                putString("city", city)
                putString("zip", zip)
                putString("country", country)
                putString("country_code", country_code)
                putBoolean("default", default)
                putString("first_name", first_name)
                putString("last_name", last_name)
                putString("company", company)
                putString("name", name)
                putString("country_name", country_name)
            }
            findNavController().navigate(R.id.action_from_map_to_addnewAddresses, bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getCurrentLocation()
    }

    private fun showPhoneNumberDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Phone Number")

        // Set up the input
        val input = EditText(requireContext())
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { dialog, _ ->
            val phoneNumber = input.text.toString()
            allProductViewModel.sendAddressRequest(
                address1,
                address2,
                city,
                company,
                first_name,
                last_name,
                phoneNumber,
                province,
                country,
                zip,
                name,
                province_code,
                country_code,
                country_name,
                id,
                customer_id,
                default
            )
            findNavController().navigate(R.id.action_from_map_to_newAddresses)
            Toast.makeText(requireContext(), "Address Added Successfully", Toast.LENGTH_LONG).show()

            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}
