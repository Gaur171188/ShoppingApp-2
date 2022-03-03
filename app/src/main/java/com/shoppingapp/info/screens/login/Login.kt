package com.shoppingapp.info.screens.login


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.LoginBinding


class Login : Fragment() {

    companion object{
        const val TAG = "Login"
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginBinding

    private var e: String = ""
    private var p: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.login, container, false)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]





        return binding.root

    }

}