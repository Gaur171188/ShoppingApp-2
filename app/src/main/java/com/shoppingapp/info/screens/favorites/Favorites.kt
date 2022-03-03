package com.shoppingapp.info.screens.favorites

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.FavoritiesBinding
import com.shoppingapp.info.screens.cart.CartViewModel





class Favorites : Fragment() {

    companion object{
        const val TAG = "Favorites"
    }

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var binding: FavoritiesBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.favorities, container, false)

        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]





        return binding.root

    }

}