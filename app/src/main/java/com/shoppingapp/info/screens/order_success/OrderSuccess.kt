package com.shoppingapp.info.screens.order_success


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.OrderSuccessBinding


class OrderSuccess: Fragment() {

    companion object{
        const val TAG = "OrderSuccess"
    }


    private lateinit var binding: OrderSuccessBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.order_success, container, false)



        setViews()


        return binding.root

    }

    private fun setViews() {
        binding.apply {


            /** button back to home **/
            btnBackToHome.setOnClickListener {
                findNavController().navigate(R.id.action_success_to_home)
            }


        }
    }

}