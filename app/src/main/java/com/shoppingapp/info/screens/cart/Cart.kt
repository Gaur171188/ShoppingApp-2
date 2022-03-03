package com.shoppingapp.info.screens.cart


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.CartBinding
import com.shoppingapp.info.databinding.LoginBinding
import com.shoppingapp.info.screens.login.LoginViewModel


class Cart : Fragment() {

    companion object{
        const val TAG = "Cart"
    }

    private lateinit var viewModel: CartViewModel
    private lateinit var binding: CartBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.cart, container, false)

        viewModel = ViewModelProvider(this)[CartViewModel::class.java]





        return binding.root

    }

}