package com.shoppingapp.info.screens.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.screens.orders.OrdersViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.suspendCoroutine
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.utils.Result.Success
import com.shoppingapp.info.utils.Result.Error
import kotlinx.coroutines.delay


class CartViewModel(val userRepository: UserRepository,
                    private val productRepository: ProductRepository,
                    private val allProducts: LiveData<List<Product>>): ViewModel() {

    companion object{
        const val TAG = "CartViewModel"
    }


    private var _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _likes = MutableLiveData<List<String>>()
    val likes: LiveData<List<String>> = _likes

    private val _cartItems = MutableLiveData<List<User.CartItem>?>()
    val cartItems: LiveData<List<User.CartItem>?> = _cartItems


    init {


//        viewModelScope.launch {
//            Log.d(TAG,"all products = ${_products.value?.size}")
//        }
//        refreshCartData()

//        Log.d(TAG,"carts = ${user.value?.cart?.size}")
    }

    fun refreshCartData(){
        viewModelScope.launch {
            val user = userRepository.getUser()
            val cart = user?.cart
            val likes = user?.likes
            _cartItems.value = cart!!
            _likes.value = likes!!
            _products = allProducts as MutableLiveData<List<Product>>

            Log.d(TAG,"all products = ${_products.value?.size}")
            Log.d(TAG,"cart = ${_cartItems.value!!.size}")
            Log.d(TAG,"likes = ${_likes.value!!.size}")
        }
    }




}