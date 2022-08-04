//package com.shoppingapp.info.screens.cart
//
//import androidx.lifecycle.*
//import com.shoppingapp.info.data.Product
//import com.shoppingapp.info.data.User
//import com.shoppingapp.info.repository.product.ProductRepository
//import com.shoppingapp.info.repository.user.UserRepository
//import com.shoppingapp.info.utils.Result
//import com.shoppingapp.info.utils.Result.Success
//
//
//class CartViewModel(val userRepository: UserRepository,
//                    private val productRepository: ProductRepository): ViewModel() {
//
//    companion object{
//        const val TAG = "CartViewModel"
//    }
//
//    private var _user = MutableLiveData<User>()
//    val user: LiveData<User> get() = _user
//
//    private var _products = MutableLiveData<List<Product>>()
//    val products: LiveData<List<Product>> = _products
//
////    private val _likes = MutableLiveData<List<String>>()
////    val likes: LiveData<List<String>> = _likes
//
////    private val _cartItems = MutableLiveData<List<User.CartItem>?>()
////    val cartItems: LiveData<List<User.CartItem>?> = _cartItems
////
////    private val _priceList = MutableLiveData<Map<String, Double>?>()
////    val priceList: LiveData<Map<String, Double>?> = _priceList
////
////    private val _cartProducts = MutableLiveData<List<Product>>()
////    val cartProducts: LiveData<List<Product>> = _cartProducts
//
//
//    init {
//        observeUserData()
//
////        refreshCartData()
//    }
//
//
////    fun deleteItemFromCart(itemId: String) {
////        viewModelScope.launch {
////            var cartList: MutableList<User.CartItem>
////            _cartItems.value?.let { items ->
////                val itemPos = items.indexOfFirst { it.itemId == itemId }
////                cartList = items.toMutableList()
////                val deferredRes = async { userRepository.deleteCartItemByUserId(itemId) }
////                val res = deferredRes.await()
////                if (res is Success) {
////                    cartList.removeAt(itemPos)
////                    _cartItems.value = cartList
//////                    val priceRes = async { getAllProductsInCart() }
//////                    priceRes.await()
////                } else {
////                    Log.d(OrdersViewModel.TAG, "onUpdateQuantity: Error Occurred: ${res}")
////                }
////            }
////        }
////    }
//
//
////    fun getItemsPriceTotal(price: Map<String, Double>): Double {
////        var totalPrice = 0.0
////        price.forEach { (itemId, price) ->
////            totalPrice += price * (_cartItems.value?.find { it.itemId == itemId }?.quantity ?: 1)
////        }
////        return totalPrice
////    }
////
////    fun getItemsCount(): Int {
////        var totalCount = 0
////        _cartItems.value?.forEach {
////            totalCount += it.quantity
////        }
////        return totalCount
////    }
//
////    fun getCartItems() {
////        viewModelScope.launch {
////            val user = userRepository.getUser()
////            if(user != null){
////                _cartItems.value = user.cart
////                val priceRes = async { getAllProductsInCart() }
////                priceRes.await()
////            } else {
////                _cartItems.value = emptyList()
////            }
////        }
////    }
//
//
//    private fun getUserLiveData(result: Result<User>): LiveData<User?> {
//        val res = MutableLiveData<User?>()
//        if (result is Success) {
//            res.value = result.data
//        } else {
//            res.value = null
//        }
//
//        return res
//    }
//
//
//    private fun observeUserData() {
//        _user =  Transformations.switchMap(userRepository.observeLocalUser()) {
//            getUserLiveData(it!!)
//        } as MutableLiveData<User>
//    }
//
//
//
////    fun refreshLocalCartItems() {
////        viewModelScope.launch {
////            val res = productRepository.getProducts()
////            if(res.)
////            userRepository.refreshCartItems(cart)
////        }
////    }
//
////    private suspend fun getAllProductsInCart() {
////        viewModelScope.launch {
////            val priceMap = mutableMapOf<String, Double>()
////            val proList = mutableListOf<Product>()
////
////            _cartItems.value?.let { itemList ->
////                itemList.forEach label@ { item ->
////                    val productDeferredRes = async {
////                        productRepository.getProductById(item.productId, true)
////                    }
////                    val proRes = productDeferredRes.await()
////                    if (proRes is Success) {
////                        val proData = proRes.data
////                        proList.add(proData)
////                        priceMap[item.itemId] = proData.price
////                    } else {
////                        return@label
////                    }
////                }
////            }
////            _priceList.value = priceMap
////            _cartProducts.value = proList
////        }
////    }
////
//
////    fun refreshCartData() {
////        viewModelScope.launch {
////            val priceMap = mutableMapOf<String, Double>()
////            val products = mutableListOf<Product>()
////            val cart = userRepository.getUser()?.cart
////            cart?.forEach { cartItem ->
////                val res = productRepository.getProductById(cartItem.productId, false)
////                if (res is Success){
////                    val data = res.data
////                    products.add(data)
////                    priceMap[cartItem.itemId] = data.price
////                }
////            }
////            _cartItems.value = cart
////            _priceList.value = priceMap
////            _cartProducts.value = products
////        }
////    }
//
//
////    fun deleteItemFromCart(itemId: String) {
////        viewModelScope.launch {
////            var cartList: MutableList<User.CartItem>
////            _cartItems.value?.let { items ->
////                val itemPos = items.indexOfFirst { it.itemId == itemId }
////                cartList = items.toMutableList()
////                val deferredRes = async { userRepository.deleteCartItemByUserId(itemId) }
////                val res = deferredRes.await()
////                if (res is Success) {
////                    cartList.removeAt(itemPos)
////                    _cartItems.value = cartList
//////                    val priceRes = async { getAllProductsInCart() }
//////                    priceRes.await()
////                } else {
////                    Log.d(OrdersViewModel.TAG, "onUpdateQuantity: Error Occurred: ${res}")
////                }
////            }
////        }
////    }
//
//
//
//
//}