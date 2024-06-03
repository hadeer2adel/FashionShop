package com.example.fashionshop

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import com.example.fashionshop.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    var isAllFabsVisible: Boolean? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAllFabsVisible = false
        binding.fabCategory.setOnClickListener {
            isAllFabsVisible = if (isAllFabsVisible == false) {
                showAllFabButtons()
                binding.fabCategory.setImageResource(R.drawable.ic_close)
                decreaseBrightness()
                true
            } else {
                increaseBrightness()
                binding.fabCategory.setImageResource(R.drawable.ic_categories)
                hideAllFabButtons()
                false
            }
        }


    }



    private fun hideAllFabButtons() {
        binding.apply {
            fabAccessories.visibility = View.GONE
            fabShirt.visibility = View.GONE
            fabShoes.visibility = View.GONE
        }
    }

    private fun showAllFabButtons() {
        binding.apply {
            fabAccessories.visibility = View.VISIBLE
            fabShirt.visibility = View.VISIBLE
            fabShoes.visibility = View.VISIBLE
        }
    }

    private fun decreaseBrightness() {
        val animator = ObjectAnimator.ofFloat(
            binding.overlayView,
            "alpha",
            0.0f,
            0.8f
        )
        animator.setDuration(300) // Animation duration in milliseconds (e.g., 2 seconds)
        animator.interpolator = AccelerateDecelerateInterpolator()
        binding.overlayView.setVisibility(View.VISIBLE)
        animator.start()
    }

    private fun increaseBrightness() {
        val animator = ObjectAnimator.ofFloat(
            binding.overlayView,
            "alpha",
            0.8f,
            0.0f
        )
        animator.setDuration(300) // Animation duration in milliseconds (e.g., 2 seconds)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }


}