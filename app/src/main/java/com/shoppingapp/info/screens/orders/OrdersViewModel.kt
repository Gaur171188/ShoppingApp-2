package com.shoppingapp.info.screens.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppingapp.info.data.User
import com.shoppingapp.info.utils.orderStatusFilters
import kotlinx.coroutines.launch
import java.util.*

class OrdersViewModel(): ViewModel() {

    companion object{
        const val TAG = "OrderViewModel"
    }



    private val _filterOrderStatus = MutableLiveData<String>()
    val filterOrderStatus: LiveData<String> = _filterOrderStatus



    fun setFilterStatus(status: String){
        _filterOrderStatus.value = status
    }


    fun search(query: String){
        viewModelScope.launch {

        }
    }


    fun applyFilter(filter: String, orders: List<User.OrderItem>): List<User.OrderItem> {
        val f = filter.uppercase(Locale.ROOT)
        val apply = if (filter == orderStatusFilters.first()) { // it will return all orders
            return orders
        }else{
            orders.filter { it.status == f }
        }
        return apply
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