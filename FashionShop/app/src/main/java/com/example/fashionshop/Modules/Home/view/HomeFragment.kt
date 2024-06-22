package com.example.fashionshop.Modules.Home.view
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fashionshop.Adapters.BrandAdapter
import com.example.fashionshop.Adapters.BrandClickListener
import com.example.fashionshop.Adapters.SliderAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.PriceRuleX
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.Modules.Address.viewModel.AddressFactory
import com.example.fashionshop.Modules.Address.viewModel.AddressViewModel
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.Home.viewModel.HomeFactory
import com.example.fashionshop.Modules.Home.viewModel.HomeViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() , BrandClickListener ,HomeListener{

    private lateinit var brandAdapter: BrandAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var sliderView: SliderView
    private lateinit var allProductFactory: HomeFactory
    private lateinit var allProductViewModel: HomeViewModel
    private lateinit var allCategoryFactory: CategoryFactory
    private lateinit var allCategoryViewModel: CategoryViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allCategoryFactory =
            CategoryFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCategoryViewModel = ViewModelProvider(this, allCategoryFactory).get(CategoryViewModel::class.java)











        sliderView = view.findViewById(R.id.imageSlider)
        setUpRV()
        initViewModel()
     //   setUpSlider(sliderView)
        allProductFactory =
            HomeFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(HomeViewModel::class.java)
        Log.i(

            "onViewCreated", "onViewCreated: ${CustomerData.getInstance(requireContext()).cartListId}"

        )


        Log.i("onViewCreated", "onViewCreated:${CustomerData.getInstance(requireContext()).currency} ")
        Log.i("onViewCreated", "onViewCreated:${CustomerData.getInstance(requireContext()).id} ")

//        viewModel.getAdsCount()
//        viewModel.products.observe(viewLifecycleOwner, Observer { value ->
//            value?.let {
//                val count = value.count
//                setUpSlider(sliderView,count)
//            }
//        })


        viewModel.getAdsCode()
        lifecycleScope.launch {
            viewModel.products.collectLatest { response ->
                when(response) {
                    is NetworkState.Loading -> {}
                    is NetworkState.Success -> {
                        val count = response.data.price_rules
                        setUpSlider(sliderView, count.size,count)

                    }
                    is NetworkState.Failure -> {
                        Snackbar.make(binding.root, response.error.message.toString(), Snackbar.LENGTH_SHORT).show()
                        //  Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun setUpRV(){
        brandAdapter = BrandAdapter(this)
        binding.rvBrands.apply {
            adapter = brandAdapter
        }
    }

    private fun setUpSlider(sliderView: SliderView ,count:Int , list:List<PriceRuleX> ) {
        val imageResourceIds = listOf(
            R.drawable.fiveoff,
            R.drawable.twentyfive,
            R.drawable.fivety  ,
            R.drawable.ten
        )

        val sliderAdapter = SliderAdapter(requireContext(),count, imageResourceIds, true,this)
        sliderAdapter.setCartList(list)
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
                when (response) {
                    is NetworkState.Loading -> showLoading()
                    is NetworkState.Success -> response.data.smart_collections?.let {
                        showSuccess(
                            it
                        )
                    }
                    is NetworkState.Failure -> showError(
                        "Network Error",
                        "Failed to load data. Please try again."
                    )

                    else -> {}
                }
            }
        }
    }


    override fun onItemClicked(brand: SmartCollection) {
        //Toast.makeText(requireContext(), "Clicked on ${brand.title}", Toast.LENGTH_SHORT).show()
        Snackbar.make(binding.root, "Clicked on ${brand.title}", Snackbar.LENGTH_SHORT).show()

        val title: String? = brand.title
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

    override fun getDiscountCodeLongPreesed(discount: String) {

        val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = ClipData.newPlainText("Discount Code", discount)

        clipboardManager.setPrimaryClip(clip)

        Snackbar.make(binding.root, "Discount code copied to clipboard", Snackbar.LENGTH_SHORT).show()
        //Toast.makeText(context, "Discount code copied to clipboard", Toast.LENGTH_SHORT).show()

}
    }