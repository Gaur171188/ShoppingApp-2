package com.shoppingapp.info.screens.order_details


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.OrderDetailsBinding


class OrderDetails : Fragment() {

    companion object{
        const val TAG = "Order Details"
    }


    private lateinit var binding: OrderDetailsBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.order_details, container, false)



        return binding.root

    }

}