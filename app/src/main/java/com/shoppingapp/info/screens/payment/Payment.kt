package com.shoppingapp.info.screens.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.PaymentBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.Constants
import com.shoppingapp.info.utils.DataStatus
import com.shoppingapp.info.utils.hide
import com.shoppingapp.info.utils.show
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Payment: Fragment() {

    companion object{
        const val TAG = "Profile"
    }


    private lateinit var binding: PaymentBinding
    private lateinit var order: User.OrderItem
    private var orderPrice = 0

    private val hViewModel by sharedViewModel<HomeViewModel>()
    private lateinit var viewModel: PaymentViewModel
    private var myBalance: Int? = null

    private var userId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.payment, container, false)
        viewModel = ViewModelProvider(this)[PaymentViewModel::class.java]



        initData()
        setViews()
        setObserves()

        return binding.root

    }

    private fun setObserves() {

        binding.apply {


            /** pay status **/
            viewModel.payStatus.observe(viewLifecycleOwner){ status->
                if (status != null){
                    when(status){
                        DataStatus.LOADING -> { loader.root.show() }
                        DataStatus.SUCCESS -> {
                            loader.root.hide()
                            findNavController().navigate(R.id.action_payment_to_orderSuccess)
                        }
                        DataStatus.ERROR -> { loader.root.hide() }
                    }
                }else{
                    loader.root.hide()
                }
            }

            /** error message **/
            viewModel.errorMessage.observe(viewLifecycleOwner){ message->
                if (message != null){
                    paymentError.text = message
                    paymentError.show()
                }else{
                    paymentError.hide()
                }
            }


        }



    }


    private fun setViews() {
        binding.apply {

            // set app bar title
            paymentAppBar.topAppBar.title = "Payment"


            // set price and balance
            payment.apply {
                payment.homeViewModel = hViewModel
//                price = orderPrice
//                balance = myBalance
            }


            /** button back **/
            paymentAppBar.topAppBar.setOnClickListener {
                findNavController().navigateUp()
            }


            /** button pay now **/
            btnPayNow.setOnClickListener {
                viewModel.pay(myBalance!!, orderPrice, order, userId)
            }


        }
    }


    private fun initData() {
        order = try { arguments?.getParcelable(Constants.KEY_ORDER)!! }
        catch (ex: Exception){ User.OrderItem() }

        val balance = hViewModel.balance.value ?: 0
        myBalance = balance

        orderPrice = (hViewModel.totalItemsPrice.value ?: 0.0).toInt()
        userId = hViewModel.userData.value?.userId!!

    }



}