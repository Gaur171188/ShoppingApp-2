package com.shoppingapp.info.screens.home


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.CartBinding
import com.shoppingapp.info.databinding.HomeBinding
import com.shoppingapp.info.screens.cart.CartViewModel


class Home : Fragment() {

    companion object{
        const val TAG = "Home"
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.home, container, false)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]



        return binding.root

    }

}