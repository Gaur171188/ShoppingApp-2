package com.shoppingapp.info.screens.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.ProductDetailsBinding
import com.shoppingapp.info.databinding.ProfileBinding
import com.shoppingapp.info.screens.product_details.ProductDetailsViewModel


class Profile: Fragment() {

    companion object{
        const val TAG = "Profile"
    }

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ProfileBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.profile, container, false)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]






        return binding.root

    }

}