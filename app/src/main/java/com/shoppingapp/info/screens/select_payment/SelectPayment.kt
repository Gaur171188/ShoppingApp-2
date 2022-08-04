package com.shoppingapp.info.screens.select_payment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.ProfileBinding
import com.shoppingapp.info.databinding.SelectPaymentBinding
import com.shoppingapp.info.screens.profile.ProfileViewModel


class SelectPayment: Fragment() {

    companion object{
        const val TAG = "Profile"
    }


    private lateinit var binding: SelectPaymentBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.select_payment, container, false)





        return binding.root

    }

}