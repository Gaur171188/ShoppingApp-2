
package com.shoppingapp.info.screens.product_details

import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.product.RemoteProductRepository
import com.shoppingapp.info.repository.user.RemoteUserRepository

import com.shoppingapp.info.utils.AddObjectStatus
import com.shoppingapp.info.utils.DataStatus
import kotlinx.coroutines.async
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.utils.Result.Error
import kotlinx.coroutines.launch
import java.util.*


class ProductDetailsViewModel(): ViewModel() {

    companion object{
        private const val TAG = "ProductDetailsViewModel"
    }
    private val userRepository = RemoteUserRepository()
    private val productRepository = RemoteProductRepository()

    private val _productId = MutableLiveData<String>()

    private val _cartItems = MutableLiveData<List<User.CartItem>>()
    val cartItems: LiveData<List<User.CartItem>> = _cartItems

    private val _quantity = MutableLiveData<Int?>()
    val quantity: LiveData<Int?> = _quantity

    private val _loadStatus = MutableLiveData<DataStatus>()
    val loadStatus: LiveData<DataStatus> = _loadStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isProductLiked = MutableLiveData<Boolean>()
    val isProductLiked: LiveData<Boolean> = _isProductLiked

    private val _isItemInCart = MutableLiveData<Boolean>()
    val isItemInCart: LiveData<Boolean> = _isItemInCart




    fun setQuantityOfItem (value: Int) {
        viewModelScope.launch {
            val newQuantity = value + _quantity.value!!
            _quantity.value = newQuantity
        }
    }



//    fun setQuantityOfItem (productId: String,userId: String, value: Int) {
//        viewModelScope.launch {
//
//            val newQuantity = value + _quantity.value!!
//            _quantity.value = newQuantity
//
//            val user = userRepository.getUserById(userId)
//
//
//            if (user != null) {
//                val item = user.cart.find { it.productId == productId }
//                if (item != null) {
//                    item.quantity = newQuantity
//                }
//            }
//
//        }
//    }


    // check if item in cart.
    // load cart data.
    // load quantity.

    fun loadData(user: User,product: Product) {
        val cart = user.cart
        val likes = user.likes
        val productId = product.productId
        val productIds = cart.map { it.productId }
        val itemInCart = productIds.contains(productId)

        loadCartItems(cart)
        loadQuantity(productId,cart)
        isItemInCart(itemInCart)
    }


    fun loadQuantity(productId: String, cartItem: List<User.CartItem>) {
        val quantity = cartItem.find { it.productId == productId }?.quantity
        if (quantity != null) {
            _quantity.value = quantity
        }else{
            _quantity.value = 1
        }
    }

    fun loadCartItems(cartItem: List<User.CartItem>){
        _cartItems.value = cartItem
    }

    fun isItemInCart(b: Boolean){
        _isItemInCart.value = b
    }







    fun addToCart(product: Product,size: Int?, color: String?,userId: String) {
        Log.d(TAG, "onAddingCartItem: Loading..")
        viewModelScope.launch {
            val itemId = UUID.randomUUID().toString()
            val newItem = User.CartItem(itemId, product.productId, product.owner, _quantity.value!!, color, size)
            userRepository.insertCartItem(newItem,userId)
                .addOnSuccessListener {
                    Log.d(TAG, "onAddingCartItem: Item has been added success")
                    _isItemInCart.value = true
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "onAddingCartItem: failed to add due to ${e.message}")
                }
        }
    }


    fun removeCartItem(itemId: String, userId: String) {
        Log.d(TAG, "onRemovingCartItem: Loading..")
        viewModelScope.launch {
            userRepository.removeCartItem(itemId,userId)
                .addOnSuccessListener {
                    Log.d(TAG, "onRemovingCartItem: Item has been removed success")
                    _isItemInCart.value = false
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "onRemovingCartItem: failed to remove due to ${e.message}")
                }
        }
    }



    fun updateCartItem(product: Product,size: Int?, color: String?,userId: String){
        viewModelScope.launch {

        }
    }




    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onClear: view model details")
    }


}

