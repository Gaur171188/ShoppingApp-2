package com.shoppingapp.info.screens.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.OrdersBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.Constants
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Orders: Fragment() {

    companion object{
        const val TAG = "Orders"
    }

    private val homeViewModel by sharedViewModel<HomeViewModel>()

    private lateinit var binding: OrdersBinding
    private val orderController by lazy { OrderController() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.orders, container, false)



        setViews()
        setObservers()

        return binding.root

    }

    private fun navigateToOrderDetails(order: User.OrderItem) {
        val data = bundleOf(Constants.KEY_ORDER to order)
        findNavController().navigate(R.id.action_orders_to_orderDetails,data)
    }

    private fun setAdapter(){
//        val orders = homeViewModel.orders.value ?: emptyList()
        orderController.setData(emptyList())

        orderController.clickListener = object : OrderController.OnClickListener {

            override fun onItemClick(order: User.OrderItem) {
                navigateToOrderDetails(order)

//                if(homeViewModel.){
//                    navigateToOrderDetails(order.orderId)
//                }else{
//                    Toast.makeText(requireContext(),"click",Toast.LENGTH_SHORT).show()
//                }

            }

        }


        binding.orderRecyclerView.adapter = orderController.adapter
    }


    private fun setViews() {

        setAdapter()
        binding.apply {

            ordersAppBar.topAppBar.title = "Orders"

            /** refresh layout **/
            swipeRefreshLayout.setOnRefreshListener {
                homeViewModel.loadData()
            }


        }


    }


    private fun setObservers() {


        /** orders live data **/
        homeViewModel.orders.observe(viewLifecycleOwner){ orders->
            if (!orders.isNullOrEmpty()) {
                orderController.setData(orders)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }


    }


}