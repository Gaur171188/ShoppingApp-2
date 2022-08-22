package com.shoppingapp.info.screens.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shoppingapp.info.data.User
import com.shoppingapp.info.utils.OrderStatus
import com.shoppingapp.info.utils.Sort


class OrdersViewModel(): ViewModel() {

    companion object{
        const val TAG = "OrderViewModel"
    }



    private val _filterOrderStatus = MutableLiveData<String>()
    val filterOrderStatus: LiveData<String> = _filterOrderStatus



    fun setFilterStatus(status: String){
        _filterOrderStatus.value = status
    }


    fun searchByOrderId(query: String?, orders: List<User.OrderItem>): List<User.OrderItem> {
        val result = if (!query.isNullOrEmpty()){
            orders.filter { it.orderId.contains(query) }
        }else{
            orders
        }
        return result
    }



    fun filter (
        orders: List<User.OrderItem>,
        sortedBy: String): List<User.OrderItem> {
        val list = when (val sort = sortedBy.uppercase()) {
            Sort.DATE.name -> { orders.sortedBy { it.orderId } }
            Sort.NAME.name -> { orders.sortedBy { it.orderDate } }
            else -> { orders.filter { it.status == sort } }
        }
        return list
    }


//    fun finalizeOrder(userId: String) {
//        _orderStatus.value = StoreDataStatus.LOADING
////        val deliveryAddress = _userAddresses.value?.find { it.addressId == _selectedAddress.value }
//        val paymentMethod = _selectedPaymentMethod.value
//        val currDate = Date()
//        val orderId = getRandomString(6, currDate.time.toString(), 1)
//        val items = _cartItems.value
//        val itemPrices = _priceList.value
//        val shippingCharges = 0.0
//        if (paymentMethod != null && !items.isNullOrEmpty() && !itemPrices.isNullOrEmpty()) {
//            val newOrder = User.OrderItem(
//                orderId,
//                userId,
//                items,
//                itemPrices,
//                shippingCharges,
//                paymentMethod,
//                currDate,
//            )
////            newOrderData.value = newOrder
//            insertOrder(newOrder)
//        } else {
//            Log.d(TAG, "orFinalizeOrder: Error, data null or empty")
//            _orderStatus.value = StoreDataStatus.ERROR
//        }
//    }

}