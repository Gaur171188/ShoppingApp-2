
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
//    private val productId: String,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val allProducts: LiveData<List<Product>>): ViewModel() {

//    private val shopApp = ShoppingApplication(application.applicationContext)

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


//    private val currentUserId = sessionManager.getUserIdFromSession()

    init {

        _isProductLiked.value = false
        _errorStatus.value = null





    }

//    fun setProductId(){
//        _productId.value = productId
//    }

    fun getProductDetails(productId: String) {
        viewModelScope.launch {
//            setProductId()
//            Log.d(TAG,"product id : ${_productId.value}")
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

//    fun setQuantityOfItem(productId: String, value: Int) {
//        viewModelScope.launch {
//
//            val user = userRepository.getUser()
//            if (user != null){
//                val itemId = user.cart.find { it.productId == productId }?.itemId
//                var cartList: MutableList<User.CartItem>
//                var q = 0
//                _cartItems.value?.let { items ->
//                    val item = items.find { it.itemId == itemId }
//
//                    val itemPos = items.indexOfFirst { it.itemId == itemId }
////                cartList = items.toMutableList()
//                    if (item != null) {
//                        q = item.quantity + value
//                        _quantity.value = q
//                        item.quantity = q
//                        val deferredRes = async { userRepository.updateCartItemByUserId(item) }
//                        val res = deferredRes.await()
//                        if (res is Result.Success) {
////                        cartList[itemPos] = item
////                        _cartItems.value = cartList
//
//                        } else {
//                            if (res is Error)
//                                Log.d(OrdersViewModel.TAG, "onUpdateQuantity: Error Occurred: ${res}")
//                        }
//
//                    }
//                }
//            }
//        }
////
//    }


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
//
//
////            val user = userRepository.getUser()
//            if (user != null){
//                val itemId = user.cart.find { it.productId == productId }?.itemId
//                var cartList: MutableList<User.CartItem>
//                var q = 0
//                _cartItems.value?.let { items ->
//                    val item = items.find { it.itemId == itemId }
//
//                    val itemPos = items.indexOfFirst { it.itemId == itemId }
////                cartList = items.toMutableList()
//                    if (item != null) {
//                        q = item.quantity + value
//                        _quantity.value = q
//                        item.quantity = q
//                        val deferredRes = async { userRepository.updateCartItemByUserId(item) }
//                        val res = deferredRes.await()
//                        if (res is Result.Success) {
////                        cartList[itemPos] = item
////                        _cartItems.value = cartList
//
//                        } else {
//                            if (res is Error)
//                                Log.d(OrdersViewModel.TAG, "onUpdateQuantity: Error Occurred: ${res}")
//                        }
//
//                    }
//                }
//            }
//            }
//




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

    fun toggleLikeProduct(productId: String) {
        Log.d(TAG, "toggling Like")
        viewModelScope.launch {
            val deferredRes = async {
                if (_isProductLiked.value == true) {
                    userRepository.removeProductFromLikes(productId)
                } else {
                    userRepository.insertProductToLikes(productId)
                }
            }
            val res = deferredRes.await()
            if (res is Result.Success) {
                _isProductLiked.value = !_isProductLiked.value!!
            } else{
                if(res is Error)
                    Log.d(TAG, "Error toggling like, ${res}")
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

//    fun checkIfInCart() {
//        viewModelScope.launch {
//            val deferredRes = async { authRepository.getUserData(currentUserId!!) }
//            val userRes = deferredRes.await()
//            if (userRes is Success) {
//                val uData = userRes.data
//                if (uData != null) {
//                    val cartList = uData.cart
//                    val idx = cartList.indexOfFirst { it.productId == productId }
//                    _isItemInCart.value = idx >= 0
//                    Log.d(TAG, "Checking in Cart: Success, value = ${_isItemInCart.value}, ${cartList.size}")
//                } else {
//                    _isItemInCart.value = false
//                }
//            } else {
//                _isItemInCart.value = false
//            }
//        }
//    }


    fun addToCart(size: Int?, color: String?,productId: String) {

        Log.d(TAG, "item is adding to cart ..")
        val itemId = UUID.randomUUID().toString()
        val newItem = User.CartItem(itemId, productId, productData.value!!.owner, _quantity.value!!, color, size)
        insertCartItem(newItem)

    }

//    fun addToCart(size: Int?, color: String?,productId: String) {
//        val errList = mutableListOf<AddItemErrors>()
//        if (size == null) errList.add(AddItemErrors.ERROR_SIZE)
//        if (color.isNullOrBlank()) errList.add(AddItemErrors.ERROR_COLOR)
//
//        if (errList.isEmpty()) {
//            val itemId = UUID.randomUUID().toString()
//            val newItem = User.CartItem(itemId, productId, productData.value!!.owner, 1, color, size)
//            insertCartItem(newItem)
//        }
//    }

//    fun addToCart(size: Int?, color: String?,onError:(String)-> Unit) {
//
//        if (size != null && color!!.isNotEmpty()){
//            val itemId = UUID.randomUUID().toString()
//            val newItem = UserData.CartItem(
//                itemId, productId, productData.value!!.owner, 1, color, size
//            )
//            insertCartItem(newItem)
//        }else{
//            onError("some info is required!")
//        }
//
//    }




    private fun insertCartItem(item: User.CartItem) {
        viewModelScope.launch {
            _addItemStatus.value = AddObjectStatus.ADDING
            val deferredRes = async {
                userRepository.insertCartItemByUserId(item)
            }
            val res = deferredRes.await()
            if (res is Result.Success) {
                Log.d(TAG, "onAddItem: Success")
                _addItemStatus.value = AddObjectStatus.DONE
            } else {
                _addItemStatus.value = AddObjectStatus.ERR_ADD
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






//package com.shoppingapp.info.screens.product_details
//
//import android.util.Log
//import androidx.lifecycle.*
//import com.shoppingapp.info.utils.Result
//import com.shoppingapp.info.data.Product
//import com.shoppingapp.info.data.User
//import com.shoppingapp.info.repository.product.ProductRepository
//import com.shoppingapp.info.repository.user.UserRepository
//import com.shoppingapp.info.utils.AddItemErrors
//import com.shoppingapp.info.utils.AddObjectStatus
//import com.shoppingapp.info.utils.StoreDataStatus
//import kotlinx.coroutines.async
//import kotlinx.coroutines.launch
//import java.util.*
//
//
//class ProductDetailsViewModel(
//    private val productRepository: ProductRepository,
//    private val userRepository: UserRepository,
//    private val productId: String,
////    private val user: LiveData<User>,
//    private val allProducts: LiveData<Product>
//): ViewModel() {
//
////    private val shopApp = ShoppingApplication(application.applicationContext)
//
//    companion object {
//        private const val TAG = "ProductDetailsViewModel"
//    }
//
////    private var productId = MutableLiveData<String>()
//
//    private var _products = MutableLiveData<List<Product>>()
//    val products: LiveData<List<Product>> get() = _products
//
//    private val _productData = MutableLiveData<Product?>()
//    val productData: LiveData<Product?> get() = _productData
//
//    private var _userData = MutableLiveData<User>()
//    val userData: LiveData<User> get() = _userData
//
//    private val _dataStatus = MutableLiveData<StoreDataStatus>()
//    val dataStatus: LiveData<StoreDataStatus> get() = _dataStatus
//
//    private val _errorStatus = MutableLiveData<String?>()
//    val errorStatus: LiveData<String?> get() = _errorStatus
//
////    private val _errorStatus = MutableLiveData<List<AddItemErrors>>()
////    val errorStatus: LiveData<List<AddItemErrors>> get() = _errorStatus
//
//    private val _addItemStatus = MutableLiveData<AddObjectStatus?>()
//    val addItemStatus: LiveData<AddObjectStatus?> get() = _addItemStatus
//
//    private val _isProductLiked = MutableLiveData<Boolean>()
//    val isProductLiked: LiveData<Boolean> get() = _isProductLiked
//
//    private val _isItemInCart = MutableLiveData<Boolean>()
//    val isItemInCart: LiveData<Boolean> get() = _isItemInCart
//
////    private val productsRepository by lazy { shopApp.productRepository }
////    private val userRepository by lazy { shopApp.userRepository }
//
////    private val sessionManager = SharePrefManager(application.applicationContext)
////    private val currentUserId = sessionManager.getUserIdFromSession()
//
//    init {
//        _isProductLiked.value = false
//        _errorStatus.value = null
//
////        _userData = user as MutableLiveData<User>
//
//
//
//        initData()
//
//    }
//
//    fun initData(){
//        Log.d(TAG,"products: ${_products.value?.size}")
//        Log.d(TAG,"productId is : $productId")
//        _products = allProducts as MutableLiveData<List<Product>>
//        getProductDetails()
//        checkIfInCart()
//        setLike()
//    }
//
//
//    private fun getProductDetails() {
//        val product = _products.value?.find { it.productId == productId }
//        _productData.value = product
//    }
//
//    private fun getUserLiveData(result: Result<User>): LiveData<User?> {
//        val res = MutableLiveData<User?>()
//        if (result is Result.Success) {
//            res.value = result.data
//        } else {
//            res.value = null
//        }
//        return res
//    }
//
//
//    fun observeLocalUser() {
//        _userData = Transformations.switchMap(userRepository.observeLocalUser()) {
//            getUserLiveData(it!!)
//        } as MutableLiveData<User>
//    }
//
//
//
////    private fun getProductDetails() {
////        viewModelScope.launch {
////            _dataStatus.value = StoreDataStatus.LOADING
////            try {
////                Log.d(TAG, "getting product Data")
////                val res = productsRepository.getProductById(productId, false)
////                if (res is Result.Success) {
////                    _productData.value = res.data
////                    _dataStatus.value = StoreDataStatus.DONE
////                } else if (res is Error) {
////                    throw Exception("Error getting product")
////                }
////            } catch (e: Exception) {
////                _productData.value = Product()
////                _dataStatus.value = StoreDataStatus.ERROR
////                _errorStatus.value = "Error happening!"
////            }
////        }
////    }
//
//
//    fun setLike() {
//        _dataStatus.value = StoreDataStatus.LOADING
//        viewModelScope.launch {
//            val res = userRepository.getLikesByUserId()
//            if (res is Result.Success) {
//                val userLikes = res.data ?: emptyList()
//                _isProductLiked.value = userLikes.contains(productId)
//                _dataStatus.value = StoreDataStatus.DONE
//            } else {
//                if (res is Error) {
//                    Log.d(TAG, "Getting Likes: Error Occurred, ${res.message}")
//                    _dataStatus.value = StoreDataStatus.ERROR
//                }
//
//            }
//        }
//    }
//
//
//    fun toggleLikeProduct() {
//        Log.d(TAG, "toggling Like")
//        viewModelScope.launch {
//            val deferredRes = async {
//                if (_isProductLiked.value == true) {
//                    userRepository.removeProductFromLikes(productId)
//                } else {
//                    userRepository.insertProductToLikes(productId)
//                }
//            }
//            val res = deferredRes.await()
//            if (res is Result.Success) {
//                _isProductLiked.value = !_isProductLiked.value!!
//            } else {
//                if (res is Error)
//                    Log.d(TAG, "Error toggling like, ${res.message}")
//            }
//        }
//    }
//    fun isUserSeller() = userRepository.isUserSeller()
//
//    fun checkIfInCart() {
//        viewModelScope.launch {
//            val user = userRepository.getUser()!!
//            val cartList = user.cart
//            Log.d(TAG,"cart size: ${cartList.size}")
//            val idx = cartList.indexOfFirst { it.productId == productId }
//            _isItemInCart.value = idx >= 0
//        }
//    }
//
//
////    fun checkIfInCart() {
////        viewModelScope.launch {
////            val deferredRes = async { authRepository.getUserData(currentUserId!!) }
////            val userRes = deferredRes.await()
////            if (userRes is Success) {
////                val uData = userRes.data
////                if (uData != null) {
////                    val cartList = uData.cart
////                    val idx = cartList.indexOfFirst { it.productId == productId }
////                    _isItemInCart.value = idx >= 0
////                    Log.d(TAG, "Checking in Cart: Success, value = ${_isItemInCart.value}, ${cartList.size}")
////                } else {
////                    _isItemInCart.value = false
////                }
////            } else {
////                _isItemInCart.value = false
////            }
////        }
////    }
//
//    fun addToCart(size: Int?, color: String?,productId: String) {
//        val errList = mutableListOf<AddItemErrors>()
//        if (size == null) errList.add(AddItemErrors.ERROR_SIZE)
//        if (color.isNullOrBlank()) errList.add(AddItemErrors.ERROR_COLOR)
//        if (errList.isEmpty()) {
//            val itemId = UUID.randomUUID().toString()
//            val newItem = User.CartItem(
//                itemId, productId, _productData.value!!.owner, 1, color, size
//            )
//            insertCartItem(newItem)
//        }
//    }
//
//
//
//    private fun insertCartItem(item: User.CartItem) {
//        viewModelScope.launch {
//            _addItemStatus.value = AddObjectStatus.ADDING
//            val deferredRes = async {
//                userRepository.insertCartItemByUserId(item)
//            }
//            val res = deferredRes.await()
//            if (res is Result.Success) {
//                Log.d(TAG, "onAddItem: Success")
//                _addItemStatus.value = AddObjectStatus.DONE
//            } else {
//                _addItemStatus.value = AddObjectStatus.ERR_ADD
//                if (res is Error) {
//                    Log.d(TAG, "onAddItem: Error, ${res.message}")
//                }
//            }
//        }
//    }
//
//
//
//
//}