package com.shoppingapp.info.screens.order_details


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.OrderDetailsBinding
import com.shoppingapp.info.screens.cart.CartController
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.getItemsPriceTotal
import org.koin.android.viewmodel.ext.android.sharedViewModel


class OrderDetails : Fragment() {

    companion object{
        const val TAG = "Order Details"
    }

    private lateinit var orderId: String
    private lateinit var binding: OrderDetailsBinding
    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private val cartController by lazy { CartController() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.order_details, container, false)
        orderId = arguments?.get("orderId") as String



        setViews()
        setObserves()




        return binding.root

    }

    private fun setObserves() {


        // orders
        homeViewModel.orders.observe(viewLifecycleOwner) {
            val order = it?.find { it.orderId == orderId }
            val items = order?.items
            cartController.setData(items)
        }


    }



    private fun setAdapter(order: User.OrderItem?) {
        val products = homeViewModel.products.value ?: emptyList()
        val isSeller = homeViewModel.isUserSeller()
        cartController.products = products
        cartController.isSeller = isSeller
        if (isSeller) {
            cartController.setData(order?.items)
        }


        cartController.clickListener = object: CartController.OnClickListener {

            override fun onDeleteClick(cartItem: User.CartItem, position: Int) {
                homeViewModel.deleteItemFromCart(cartItem.itemId)
            }

            override fun onItemClick(cartItem: User.CartItem, position: Int) {}

        }

        binding.productsRecyclerView.adapter = cartController.adapter


    }


    private fun setViews() {
        val orders = homeViewModel.orders.value ?: emptyList()
        val order = orders.find { it.orderId == orderId }
        setAdapter(order)

        binding.apply {

            if (order != null){

                // order details layout
                orderDetails.apply {
                    orderDate = order.orderDate
                    orderAddress = order.address.streetAddress
                    orderStatus = order.status
                }


                // paymentDetails layout
                paymentDetails.apply {
                    itemsCount = order.items.size
                    itemsPriceTotal = getItemsPriceTotal(order.itemsPrices,order.items)
                    totalPrice = 0.0
                }

            }



        }
    }

}