
package com.shoppingapp.info.screens.product_details

import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.product.RemoteProductRepository
import com.shoppingapp.info.repository.user.RemoteUserRepository

import com.shoppingapp.info.utils.DataStatus
import kotlinx.coroutines.launch
import java.util.*


class ProductDetailsViewModel(): ViewModel() {

    companion object{
        private const val TAG = "ProductDetailsViewModel"
    }
    private val userRepository = RemoteUserRepository()
    private val productRepository = RemoteProductRepository()

    private val _productId = MutableLiveData<String>()


    /** progress **/
    private val _loadStatus = MutableLiveData<DataStatus>()
    val loadStatus: LiveData<DataStatus> = _loadStatus


    /** progress **/
    private val _updateCartStatus = MutableLiveData<DataStatus>()
    val updateCartStatus: LiveData<DataStatus> = _updateCartStatus

    /** progress **/
    private val _removeCartStatus = MutableLiveData<DataStatus>()
    val removeCartStatus: LiveData<DataStatus> = _removeCartStatus

    /** progress **/
    private val _insertCartStatus = MutableLiveData<DataStatus>()
    val insertCartStatus: LiveData<DataStatus> = _insertCartStatus


    private val _cartItems = MutableLiveData<List<User.CartItem>>()
    val cartItems: LiveData<List<User.CartItem>> = _cartItems

    private val _quantity = MutableLiveData<Int?>()
    val quantity: LiveData<Int?> = _quantity


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

    fun loadCartItems(cartItem: List<User.CartItem>) {
        _cartItems.value = cartItem
    }

    fun isItemInCart(b: Boolean){
        _isItemInCart.value = b
    }







    fun addToCart(product: Product,userId: String) {
        Log.d(TAG, "onAddingCartItem: Loading..")
        _insertCartStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            val itemId = UUID.randomUUID().toString()
            val newItem = User.CartItem(itemId, product.productId, product.owner, _quantity.value!!)
            userRepository.insertCartItem(newItem,userId)
                .addOnSuccessListener {
                    Log.d(TAG, "onAddingCartItem: Item has been added success")
                    _isItemInCart.value = true
                    _insertCartStatus.value = DataStatus.SUCCESS
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "onAddingCartItem: failed to add due to ${e.message}")
                    _insertCartStatus.value = DataStatus.ERROR
                }
        }
    }


    fun removeCartItem(itemId: String, userId: String) {
        Log.d(TAG, "onRemovingCartItem: Loading..")
        _removeCartStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            userRepository.removeCartItem(itemId,userId)
                .addOnSuccessListener {
                    Log.d(TAG, "onRemovingCartItem: Item has been removed success")
                    _isItemInCart.value = false
                    _removeCartStatus.value = DataStatus.SUCCESS
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "onRemovingCartItem: failed to remove due to ${e.message}")
                    _removeCartStatus.value = DataStatus.ERROR
                }
        }
    }



    fun updateCartItem(productId: String,userId: String) {
        Log.d(TAG, "onUpdatingCartItem: Loading..")
        _updateCartStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            val item = _cartItems.value?.find { it.productId == productId }
            if (item != null){
                // update the element that you want from here
                item.quantity = _quantity.value!!
                userRepository.updateCartItem(item,userId)
                    .addOnSuccessListener {
                        Log.d("ProductDetails",item.toString())
                        Log.d(TAG, "onUpdatingCartItem: Item has been update success")
                        _updateCartStatus.value = DataStatus.SUCCESS
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "onUpdatingCartItem: failed to update due to ${e.message}")
                        _updateCartStatus.value = DataStatus.ERROR
                    }
            }

        }
    }




    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onClear: view model details")
    }


}

