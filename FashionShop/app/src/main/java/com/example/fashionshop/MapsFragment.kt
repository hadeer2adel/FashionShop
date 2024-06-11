package com.example.fashionshop

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fashionshop.Model.customers
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.viewModels.AddNewAddressFactory
import com.example.fashionshop.viewModels.AddNewAddressViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
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
    private  var province: String = ""
    private lateinit var country: String
    private lateinit var zip: String
    private lateinit var name: String
    private  var province_code: String = ""
    private lateinit var country_code: String
    private lateinit var country_name: String
    private  var id: Long  = 1
    private  var  customer_id: Long = 7371713577180
    private var default: Boolean = false
    private lateinit var allProductFactory: AddNewAddressFactory
    private lateinit var allProductViewModel: AddNewAddressViewModel

    @SuppressLint("SuspiciousIndentation")

    private val callback = OnMapReadyCallback { googleMap ->
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
                country =addresses[0].countryName
                country_code = addresses[0].countryCode
                default = false
                first_name = "hader"
                last_name = "mo"
                company = addresses[0].featureName
                name = addresses[0].adminArea
                country_name = addresses[0].countryName
                showPhoneNumberDialog()
                val cityName = addresses[0].getAddressLine(0)
                val lat = addresses[0].latitude
                val lon = addresses[0].longitude
                Toast.makeText(requireContext(), "City: $cityName, Longitude: $lat", Toast.LENGTH_LONG).show()
            }

        //    Toast.makeText(requireContext(), "Address Added Successfully", Toast.LENGTH_LONG).show()
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
                address1 ,
                address2 ,
                city ,
                company ,
                first_name ,
                last_name ,
                phoneNumber ,
                province ,
                country ,
                zip ,
                name ,
                province_code ,
                country_code ,
                country_name ,
                id  ,
                customer_id ,
                default
            )
            findNavController().navigate(R.id.action_from_map_to_newAddresses)
//            Toast.makeText(requireContext(), "Phone number added: $phoneNumber", Toast.LENGTH_SHORT).show()
               Toast.makeText(requireContext(), "Address Added Successfully", Toast.LENGTH_LONG).show()

            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

}
