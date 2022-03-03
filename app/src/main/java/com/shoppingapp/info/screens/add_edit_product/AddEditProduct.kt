package com.shoppingapp.info.screens.add_edit_product

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.AccountBinding
import com.shoppingapp.info.databinding.AddEditProductBinding
import com.shoppingapp.info.screens.account.AccountViewModel

class AddEditProduct : Fragment() {


    private lateinit var viewModel: AddEditProductViewModel
    private lateinit var binding: AddEditProductBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this)[AddEditProductViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater,R.layout.add_edit_product, container, false)




        return binding.root


    }



}