package com.shoppingapp.info.screens.wallet

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.WalletBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.hide
import org.koin.android.viewmodel.ext.android.sharedViewModel

class Wallet : Fragment() {


    private lateinit var viewModel: WalletViewModel
    private lateinit var binding: WalletBinding

    private val hViewModel by sharedViewModel<HomeViewModel>()
//    private var myBalance = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this)[WalletViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater,R.layout.wallet, container, false)

//        initData()
        setViews()
        setObserves()


        return binding.root
    }

//    private fun initData() {
//        myBalance = homeViewModel.balance.value ?: 0
//    }


    private fun setObserves() {

    }


    private fun setViews() {
        binding.apply {
            wallet.apply {

                walletAppBar.topAppBar.title = resources.getString(R.string.my_wallet)
                wallet.homeViewModel = hViewModel

                priceLayout.hide() // hide price layout


                /** button add balance **/
                btnAddBalance.setOnClickListener {

                }

                /** button back **/
                walletAppBar.topAppBar.setOnClickListener {
                    findNavController().navigateUp()
                }



            }

        }
    }


}