package com.shoppingapp.info.screens.select_address

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.SelectAddressBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SelectAddress : Fragment() {

    companion object { const val TAG = "SelectAddress" }

    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private lateinit var viewModel: SelectAddressViewModel
    private lateinit var binding: SelectAddressBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.select_address, container, false)
        viewModel = ViewModelProvider(this).get(SelectAddressViewModel::class.java)



        val cartProducts =  homeViewModel.cartProducts.value ?: emptyList()
        val itemsPrice =  homeViewModel.itemsPrice.value ?: emptyMap()
        val cartItems =  homeViewModel.cartItems.value ?: emptyList()
        val quantityCount = homeViewModel.quantityCount.value ?: 0


        Log.d(TAG,"cartProducts: ${cartProducts.size} \n itemsPrice: $itemsPrice \n cartItems: ${cartItems.size} \n quantityCount: $quantityCount")

        setViews()

        return binding.root
    }

    private fun setViews() {
        binding.apply {

        }

    }


}