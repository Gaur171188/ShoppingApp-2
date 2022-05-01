package com.shoppingapp.info.screens.orders

import com.airbnb.epoxy.TypedEpoxyController
import com.shoppingapp.info.data.User
import com.shoppingapp.info.data.User.OrderItem
import com.shoppingapp.info.order

class OrderController(): TypedEpoxyController<List<OrderItem>>() {

    lateinit var clickListener: OnClickListener


    companion object {
        private const val TAG = "OrderController"
    }


    override fun buildModels(data: List<OrderItem>) {

        data.forEachIndexed { index, orderItem ->

            order {
                id(orderItem.orderId)
                order(orderItem)
                totalPrice(getItemsPriceTotal(orderItem.itemsPrices,orderItem.items))
                onItemClick { v->
                    clickListener.onItemClick(orderItem)
                }
            }
        }



    }

    interface OnClickListener {
        fun onItemClick(order: OrderItem)
    }

    private  fun getItemsPriceTotal(price: Map<String, Double>, items: List<User.CartItem>): Double {
        var totalPrice = 0.0
        price.forEach { (itemId, price) ->
            totalPrice += price * (items.find { it.itemId == itemId }?.quantity ?: 1)
        }
        return totalPrice
    }

}
