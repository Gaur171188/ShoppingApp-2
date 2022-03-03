package com.shoppingapp.info.screens.registration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.RegistrationBinding

class Registration : Fragment() {


    private lateinit var viewModel: RegistrationViewModel
    private lateinit var binding : RegistrationBinding

    private var _email = ""
    private var _password = ""
    private var _name = ""
    private var _phone = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.registration,container,false)




        return binding.root
    }


}