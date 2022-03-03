package com.shoppingapp.info.screens.account

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.AccountBinding

class Account : Fragment() {


    private lateinit var viewModel: AccountViewModel
    private lateinit var binding: AccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater,R.layout.account, container, false)



        return binding.root


    }



}