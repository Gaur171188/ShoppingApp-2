package com.shoppingapp.info.screens.orders

import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.utils.StoreDataStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.utils.getRandomString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class OrdersViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val allProducts: LiveData<List<Product>>,
): ViewModel() {

    companion object{
        const val TAG = "OrderViewModel"
    }


    init {

    }



//    private val sessionManager = SharePrefManager(application.applicationContext)
//    private val currentUser = sessionManager.getUserIdFromSession()
//    private val appShop = ShoppingApplication(application.applicationContext)

//    private val userRepository by lazy {
//        appShop.userRepository
//    }

//    private val productRepository by lazy {
//        appShop.productRepository
//    }


//    private val _userLikes = MutableLiveData<List<String>>()
//    val userLikes: LiveData<List<String>> get() = _userLikes

    private val _cartItems = MutableLiveData<List<User.CartItem>>()
    val cartItems: LiveData<List<User.CartItem>> = _cartItems

    private val _priceList = MutableLiveData<Map<String, Double>>()
    val priceList: LiveData<Map<String, Double>> = _priceList

    private val _cartProducts = MutableLiveData<List<Product>>()
    val cartProducts: LiveData<List<Product>> = _cartProducts

    private val _dataStatus = MutableLiveData<StoreDataStatus>()
    val dataStatus: LiveData<StoreDataStatus> = _dataStatus

    private val _orderStatus = MutableLiveData<StoreDataStatus>()
    val orderStatus: LiveData<StoreDataStatus> = _orderStatus

    private val _selectedAddress = MutableLiveData<String>()
    private val _selectedPaymentMethod = MutableLiveData<String>()
    private val newOrderData = MutableLiveData<User.OrderItem>()


    // TODO: 4/19/2022 enhance this function
    fun getCartItems() {
        viewModelScope.launch {
            val user = userRepository.getUser()
                if(user != null){
                    _cartItems.value = user.cart
                    val priceRes = async { getAllProductsInCart() }
                    priceRes.await()
                } else {
                    _cartItems.value = emptyList()
                }
        }
    }


//    fun getUserAddresses() {
//        Log.d(TAG, "Getting Addresses")
//        _dataStatus.value = StoreDataStatus.LOADING
//        viewModelScope.launch {
//            val res = authRepository.getAddressesByUserId(currentUser!!)
//            if (res is Success) {
//                _userAddresses.value = res.data ?: emptyList()
//                _dataStatus.value = StoreDataStatus.DONE
//                Log.d(TAG, "Getting Addresses: Success")
//            } else {
//                _userAddresses.value = emptyList()
//                _dataStatus.value = StoreDataStatus.ERROR
//                if (res is Error)
//                    Log.d(TAG, "Getting Addresses: Error Occurred, ${res.exception.message}")
//            }
//        }
//    }


//    fun deleteAddress(addressId: String) {
//        viewModelScope.launch {
//            val delRes = async { authRepository.deleteAddressById(addressId, currentUser!!) }
//            when (val res = delRes.await()) {
//                is Success -> {
//                    Log.d(TAG, "onDeleteAddress: Success")
//                    val addresses = _userAddresses.value?.toMutableList()
//                    addresses?.let {
//                        val pos =
//                            addresses.indexOfFirst { address -> address.addressId == addressId }
//                        if (pos >= 0)
//                            it.removeAt(pos)
//                        _userAddresses.value = it
//                    }
//                }
//                is Error -> Log.d(TAG, "onDeleteAddress: Error, ${res.exception}")
//                else -> Log.d(TAG, "onDeleteAddress: Some error occurred!")
//            }
//        }
//    }

    fun getItemsPriceTotal(): Double {
        var totalPrice = 0.0
        _priceList.value?.forEach { (itemId, price) ->
            totalPrice += price * (_cartItems.value?.find { it.itemId == itemId }?.quantity ?: 1)
        }
        return totalPrice
    }

//    fun toggleLikeProduct(productId: String) {
//        Log.d(TAG, "toggling Like")
//        viewModelScope.launch {
////			_dataStatus.value = StoreDataStatus.LOADING
//            val isLiked = _userLikes.value?.contains(productId) == true
//            val allLikes = _userLikes.value?.toMutableList() ?: mutableListOf()
//            val deferredRes = async {
//                if (isLiked) {
//                    authRepository.removeProductFromLikes(productId, currentUser!!)
//                } else {
//                    authRepository.insertProductToLikes(productId, currentUser!!)
//                }
//            }
//            val res = deferredRes.await()
//            if (res is Result.Success) {
//                if (isLiked) {
//                    allLikes.remove(productId)
//                } else {
//                    allLikes.add(productId)
//                }
//                _userLikes.value = allLikes
//                _dataStatus.value = StoreDataStatus.DONE
//            } else {
//                _dataStatus.value = StoreDataStatus.ERROR
//                if (res is Error)
//                    Log.d(TAG, "onUpdateQuantity: Error Occurred: ${res.message}")
//            }
//        }
//    }

    fun getItemsCount(): Int {
        var totalCount = 0
        _cartItems.value?.forEach {
            totalCount += it.quantity
        }
        return totalCount
    }

    fun setQuantityOfItem(itemId: String, value: Int) {
        viewModelScope.launch {
//			_dataStatus.value = StoreDataStatus.LOADING
            var cartList: MutableList<User.CartItem>
            _cartItems.value?.let { items ->
                val item = items.find { it.itemId == itemId }
                val itemPos = items.indexOfFirst { it.itemId == itemId }
                cartList = items.toMutableList()
                if (item != null) {
                    item.quantity = item.quantity + value
                    val deferredRes = async {
                        userRepository.updateCartItemByUserId(item)
                    }
                    val res = deferredRes.await()
                    if (res is Result.Success) {
                        cartList[itemPos] = item
                        _cartItems.value = cartList
                        _dataStatus.value = StoreDataStatus.DONE
                    } else {
                        _dataStatus.value = StoreDataStatus.ERROR
                        if (res is Error)
                            Log.d(TAG, "onUpdateQuantity: Error Occurred: ${res.message}")
                    }
                }
            }
        }
    }

    fun deleteItemFromCart(itemId: String) {
        viewModelScope.launch {
//			_dataStatus.value = StoreDataStatus.LOADING
            var cartList: MutableList<User.CartItem>
            _cartItems.value?.let { items ->
                val itemPos = items.indexOfFirst { it.itemId == itemId }
                cartList = items.toMutableList()
                val deferredRes = async {
                    userRepository.deleteCartItemByUserId(itemId)
                }
                val res = deferredRes.await()
                if (res is Result.Success) {
                    cartList.removeAt(itemPos)
                    _cartItems.value = cartList
                    val priceRes = async { getAllProductsInCart() }
                    priceRes.await()
                } else {
                    _dataStatus.value = StoreDataStatus.ERROR
                    if (res is Error)
                        Log.d(TAG, "onUpdateQuantity: Error Occurred: ${res.message}")
                }
            }
        }
    }

    fun setSelectedAddress(addressId: String) {
        _selectedAddress.value = addressId
    }

    fun setSelectedPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun finalizeOrder(userId: String) {
        _orderStatus.value = StoreDataStatus.LOADING
//        val deliveryAddress = _userAddresses.value?.find { it.addressId == _selectedAddress.value }
        val paymentMethod = _selectedPaymentMethod.value
        val currDate = Date()
        val orderId = getRandomString(6, currDate.time.toString(), 1)
        val items = _cartItems.value
        val itemPrices = _priceList.value
        val shippingCharges = 0.0
        if (paymentMethod != null && !items.isNullOrEmpty() && !itemPrices.isNullOrEmpty()) {
            val newOrder = User.OrderItem(
                orderId,
                userId,
                items,
                itemPrices,
                shippingCharges,
                paymentMethod,
                currDate,
            )
//            newOrderData.value = newOrder
            insertOrder(newOrder)
        } else {
            Log.d(TAG, "orFinalizeOrder: Error, data null or empty")
            _orderStatus.value = StoreDataStatus.ERROR
        }
    }

    private fun insertOrder(newOrder: User.OrderItem) {
        viewModelScope.launch {

                _orderStatus.value = StoreDataStatus.LOADING
                val deferredRes = async {
                    userRepository.placeOrder(newOrder)
                }
                val res = deferredRes.await()
                if (res is Result.Success) {
                    Log.d(TAG, "onInsertOrder: Success")
                    _cartItems.value = emptyList()
                    _cartProducts.value = emptyList()
                    _priceList.value = emptyMap()
                    _orderStatus.value = StoreDataStatus.DONE
                } else {
                    _orderStatus.value = StoreDataStatus.ERROR
                    if (res is Error) {
                        Log.d(TAG, "onInsertOrder: Error, ${res.message}")
                    }
                }

        }
    }

    private suspend fun getAllProductsInCart() {
        viewModelScope.launch {
//			_dataStatus.value = StoreDataStatus.LOADING
            val priceMap = mutableMapOf<String, Double>()
            val proList = mutableListOf<Product>()
            var res = true
            _cartItems.value?.let { itemList ->
                itemList.forEach label@{ item ->
                    val productDeferredRes = async {
                        this@OrdersViewModel.productRepository.getProductById(item.productId, true)
                    }
                    val proRes = productDeferredRes.await()
                    if (proRes is Result.Success) {
                        val proData = proRes.data
                        proList.add(proData)
                        priceMap[item.itemId] = proData.price
                    } else {
                        res = false
                        return@label
                    }
                }
            }
            _priceList.value = priceMap
            _cartProducts.value = proList
            if (!res) {
                _dataStatus.value = StoreDataStatus.ERROR
            } else {
                _dataStatus.value = StoreDataStatus.DONE
            }
        }
    }
}