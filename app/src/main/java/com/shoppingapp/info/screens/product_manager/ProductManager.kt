package com.shoppingapp.info.screens.product_manager

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.ProductManagerBinding

class ProductManager : Fragment() {


    private lateinit var binding: ProductManagerBinding
    private lateinit var viewModel: ProductManagerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this)[ProductManagerViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater,R.layout.product_manager, container, false)


        setViews()


        return binding.root
    }

    private fun setViews() {
        binding.apply {



        }
    }



    private fun setAppBar(){
        binding.apply {

            /** App bar title **/
            productManagerAppBar.topAppBar.title = resources.getString(R.string.product_manager)


            /** back button **/
            productManagerAppBar.topAppBar.setOnClickListener {
                findNavController().navigateUp()
            }


        }
    }
}