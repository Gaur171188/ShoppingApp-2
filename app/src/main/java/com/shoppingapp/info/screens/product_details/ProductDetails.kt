package com.shoppingapp.info.screens.product_details

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.ProductDetailsBinding


class ProductDetails: Fragment() {

    companion object{
        const val TAG = "Product Details"
    }

    private lateinit var viewModel: ProductDetailsViewModel
    private lateinit var binding: ProductDetailsBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.product_details, container, false)

        viewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]






        return binding.root

    }

}