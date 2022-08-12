package com.shoppingapp.info.screens.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.PaymentBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.Constants
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Payment: Fragment() {

    companion object{
        const val TAG = "Profile"
    }


    private lateinit var binding: PaymentBinding
    private lateinit var order: User.OrderItem

    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private var myBalance: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.payment, container, false)




        initData()
        setViews()
        setObserves()

        return binding.root

    }

    private fun setObserves() {

    }


    private fun setViews() {
        binding.apply {

            // set app bar title
            paymentAppBar.topAppBar.title = "Payment"


            // set price and balance
            payment.apply {
                price = 54
                balance = myBalance
            }


            /** button back **/
            paymentAppBar.topAppBar.setOnClickListener {
                findNavController().navigateUp()
            }



            /** button pay now **/
            btnPayNow.setOnClickListener {


            }


        }
    }


    private fun initData() {
        order = try { arguments?.getParcelable(Constants.KEY_ORDER)!! }
        catch (ex: Exception){ User.OrderItem() }

        val balance = homeViewModel.balance.value ?: 0
        myBalance = balance

    }



}