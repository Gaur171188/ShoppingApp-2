
package com.shoppingapp.info.screens.product_details

import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.screens.orders.OrdersViewModel
import com.shoppingapp.info.utils.AddObjectStatus
import com.shoppingapp.info.utils.StoreDataStatus
import kotlinx.coroutines.async
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.utils.Result.Error
import kotlinx.coroutines.launch
import java.util.*


class ProductDetailsViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val allProducts: LiveData<List<Product>>): ViewModel() {

    companion object{
        private const val TAG = "ProductDetailsViewModel"
    }

    private val _productId = MutableLiveData<String>()

    private val _cartItems = MutableLiveData<List<User.CartItem>>()
    val cartItems: LiveData<List<User.CartItem>> = _cartItems


    private val _quantity = MutableLiveData<Int?>()
    val quantity: LiveData<Int?> = _quantity

    private val _productData = MutableLiveData<Product?>()
    val productData: LiveData<Product?> get() = _productData

    private val _dataStatus = MutableLiveData<StoreDataStatus>()
    val dataStatus: LiveData<StoreDataStatus> get() = _dataStatus

    private val _errorStatus = MutableLiveData<String?>()
    val errorStatus: LiveData<String?> get() = _errorStatus

//    private val _errorStatus = MutableLiveData<List<AddItemErrors>>()
//    val errorStatus: LiveData<List<AddItemErrors>> get() = _errorStatus

    private val _addItemStatus = MutableLiveData<AddObjectStatus?>()
    val addItemStatus: LiveData<AddObjectStatus?> get() = _addItemStatus

    private val _isProductLiked = MutableLiveData<Boolean>()
    val isProductLiked: LiveData<Boolean> get() = _isProductLiked

    private val _isItemInCart = MutableLiveData<Boolean>()
    val isItemInCart: LiveData<Boolean> get() = _isItemInCart


    init {
        _errorStatus.value = null
    }



    fun getProductDetails(productId: String) {
        viewModelScope.launch {
            _dataStatus.value = StoreDataStatus.LOADING
            try {
                val res = productRepository.getProductById(productId,false)
                if (res is Result.Success) {
                    _productData.value = res.data
                    _dataStatus.value = StoreDataStatus.DONE
                } else if (res is Error) {
                    throw Exception("Error getting product")
                }
            } catch (e: Exception) {
                _productData.value = Product()
                _dataStatus.value = StoreDataStatus.ERROR
                _errorStatus.value = "Error happening!"
            }
        }
    }





    fun setQuantityOfItem(productId: String, value: Int) {
        viewModelScope.launch {

            val newQuantity = value + _quantity.value!!
            _quantity.value = newQuantity


            val user = userRepository.getUser()
            if (user != null) {
                val item = user.cart.find { it.productId == productId }
                if (item != null){
                    item.quantity = newQuantity

                    // if the item exist in cart it will be updated and save the cart value.
                    val deferredRes = async { userRepository.updateCartItemByUserId(item) }
                    deferredRes.await()
                }
            }


        }
    }




    fun getCartItems(productId: String) {
        viewModelScope.launch {
            val user = userRepository.getUser()
            if(user != null){
                viewModelScope.launch {
                    _cartItems.value = user.cart
                    val quantity = user.cart.find { it.productId == productId }?.quantity
                    if (quantity != null){
                        _quantity.value = quantity!!
                    }else{
                        _quantity.value = 1
                    }

                }
            } else {
                _cartItems.value = emptyList()
                Log.d(OrdersViewModel.TAG, "Getting Cart Items: User Not Found")
            }
        }
    }


    fun setLike(productId: String) {
        viewModelScope.launch {

            val res = userRepository.getLikesByUserId()
            if (res is Result.Success) {
                val userLikes = res.data ?: emptyList()
                _isProductLiked.value = userLikes.contains(productId)
            } else {
                if (res is Error){
                    Log.d(TAG, "Getting Likes: Error Occurred, ${res}")
                }

            }
        }
    }


    fun isUserSeller() = userRepository.isUserSeller()


    fun checkIfInCart(productId: String) {
        viewModelScope.launch {
            val user = userRepository.getUser()
            Log.d(TAG,"product id: $productId")
            Log.d(TAG, "products: ${allProducts.value!!.size}")
            if (user != null) {
                val cartList = user.cart
                val idx = cartList.indexOfFirst { it.productId == productId }
                _isItemInCart.value = idx >= 0
                Log.d(TAG, "Checking in Cart: Success, value = ${_isItemInCart.value}, ${cartList.size}")
            } else {
                _isItemInCart.value = false
            }
        }
    }



    fun addToCart(size: Int?, color: String?,productId: String) {

        Log.d(TAG, "item is adding to cart ..")
        val itemId = UUID.randomUUID().toString()
        val newItem = User.CartItem(itemId, productId, productData.value!!.owner, _quantity.value!!, color, size)
        insertCartItem(newItem)

    }




    private fun insertCartItem(item: User.CartItem) {
        viewModelScope.launch {
            val deferredRes = async {
                userRepository.insertCartItemByUserId(item)
            }
            val res = deferredRes.await()
            if (res is Result.Success) {
                Log.d(TAG, "onAddItem: Success")
            } else {
                if (res is Error) {
                    Log.d(TAG, "onAddItem: Error, ${res}")
                }
            }
        }
    }




    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onClear: view model details")
    }


}

