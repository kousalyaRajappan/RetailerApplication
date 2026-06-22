package com.za.toptitup.loginlibrary.retailerFragments.fragments.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.za.toptitup.loginlibrary.databinding.FragmentProductDetailBinding
import com.za.toptitup.loginlibrary.retailerFragments.RetailerDashboardActivity

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val productName: String by lazy { arguments?.getString("productName") ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = productName
        (activity as? RetailerDashboardActivity)?.showBackButton(true, productName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
