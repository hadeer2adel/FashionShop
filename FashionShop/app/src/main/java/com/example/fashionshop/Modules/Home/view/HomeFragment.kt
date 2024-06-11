package com.example.fashionshop.Modules.Home.view
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fashionshop.Adapters.BrandAdapter
import com.example.fashionshop.Adapters.BrandClickListener
import com.example.fashionshop.Adapters.SliderAdapter
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.Modules.Home.viewModel.HomeFactory
import com.example.fashionshop.Modules.Home.viewModel.HomeViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentHomeBinding
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() , BrandClickListener {

    private lateinit var brandAdapter: BrandAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sliderView: SliderView = view.findViewById(R.id.imageSlider)
        setUpRV()
        initViewModel()
        setUpSlider(sliderView)





    }

    private fun setUpRV(){
        brandAdapter = BrandAdapter(this)
        binding.rvBrands.apply {
            adapter = brandAdapter
        }
    }

    private fun setUpSlider(sliderView: SliderView) {
        val imageResourceIds = listOf(
            R.drawable.coupone,
            R.drawable.coupon2,
            R.drawable.coupon3
        )
        val sliderAdapter = SliderAdapter(requireContext(), imageResourceIds)
        sliderView.setSliderAdapter(sliderAdapter)
        sliderView.startAutoCycle()

    }

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = HomeFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        viewModel.getBrands()
        lifecycleScope.launch {
            viewModel.brand.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> showLoading()
                    is NetworkState.Success -> response.data.smart_collections?.let { showSuccess(it) }
                    is NetworkState.Failure -> showError("Network Error", "Failed to load data. Please try again.")
                    else -> { }
                }
            }
        }
    }


    override fun onItemClicked(brand: SmartCollection) {
        Toast.makeText(requireContext(), "Clicked on ${brand.title}", Toast.LENGTH_SHORT).show()
        val title: String? = brand.title // Get the title from somewhere
        val action = HomeFragmentDirections.actionHomeFragmentToProductsFragment4(title ?: "Default Title")
        findNavController().navigate(action)
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.cvCoupone.visibility = View.INVISIBLE
        binding.cvBrands.visibility = View.INVISIBLE
        binding.tvDiscount.visibility = View.INVISIBLE
        binding.tvBrand.visibility = View.INVISIBLE
    }

    private fun showSuccess(smartCollections: List<SmartCollection>) {
        binding.progressBar.visibility = View.INVISIBLE
        binding.cvCoupone.visibility = View.VISIBLE
        binding.cvBrands.visibility = View.VISIBLE
        binding.tvDiscount.visibility = View.VISIBLE
        binding.tvBrand.visibility = View.VISIBLE
        brandAdapter.submitList(smartCollections)
    }

    private fun showError(title: String, message: String) {
        binding.progressBar.visibility = View.INVISIBLE
        binding.cvCoupone.visibility = View.INVISIBLE
        binding.cvBrands.visibility = View.INVISIBLE
        binding.tvDiscount.visibility = View.INVISIBLE
        binding.tvBrand.visibility = View.INVISIBLE
        showAlertDialog(title, message)
    }

}