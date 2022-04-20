
package com.shoppingapp.info.screens.product_details

import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.utils.AddItemErrors
import com.shoppingapp.info.utils.AddObjectStatus
import com.shoppingapp.info.utils.StoreDataStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.utils.Result.Error
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        viewModelScope.launch {
//            Log.d(TAG, "init: productId: $productId")
//            getProductDetails()

//            checkIfInCart()
//            setLike()
        }

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
        val newItem = User.CartItem(itemId, productId, productData.value!!.owner, 1, color, size)
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