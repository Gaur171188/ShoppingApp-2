package com.shoppingapp.info.screens.order_success

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.OrderDetailsBinding
import com.shoppingapp.info.screens.orders.OrdersViewModel


class OrderSuccess: Fragment() {

    companion object{
        const val TAG = "Order Success"
    }

    private lateinit var viewModel: OrdersViewModel
    private lateinit var binding: OrderDetailsBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.order_success, container, false)

        viewModel = ViewModelProvider(this)[OrdersViewModel::class.java]






        return binding.root

    }

}