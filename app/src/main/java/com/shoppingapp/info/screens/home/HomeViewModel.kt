package com.shoppingapp.info.screens.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.shoppingapp.info.ShoppingApplication
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.UserData
import com.shoppingapp.info.utils.StoreDataStatus
import java.time.Month
import com.shoppingapp.info.Result
import com.shoppingapp.info.utils.ShoppingAppSessionManager
import kotlinx.coroutines.*
import java.util.*


@Suppress("DeferredResultUnused")
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        private const val TAG = "HomeViewModel"
    }

    private val shopApp = ShoppingApplication(application.applicationContext)
    private val authRepository by lazy{ shopApp.authRepository }
    private val productsRepository by lazy { shopApp.productsRepository }

    private val sessionManager = ShoppingAppSessionManager(application.applicationContext)
    private val userId by lazy {FirebaseAuth.getInstance().uid}
    val isUserASeller = sessionManager.isUserSeller()

    private var _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private lateinit var _allProducts: MutableLiveData<List<Product>>
    val allProducts: LiveData<List<Product>> get() = _allProducts

    private var _userProducts = MutableLiveData<List<Product>>()
    val userProducts: LiveData<List<Product>> get() = _userProducts

    private var _userOrders = MutableLiveData<List<UserData.OrderItem>>()
    val userOrders: LiveData<List<UserData.OrderItem>> get() = _userOrders

    private var _selectedOrder = MutableLiveData<UserData.OrderItem?>()
    val selectedOrder: LiveData<UserData.OrderItem?> get() = _selectedOrder

    private var _orderProducts = MutableLiveData<List<Product>>()
    val orderProducts: LiveData<List<Product>> get() = _orderProducts

    private var _likedProducts = MutableLiveData<List<Product>?>()
    val likedProducts: LiveData<List<Product>?> get() = _likedProducts

    private var _userLikes = MutableLiveData<List<String>?>()
    val userLikes: LiveData<List<String>?> get() = _userLikes

    private var _filterCategory = MutableLiveData("All")
    val filterCategory: LiveData<String> get() = _filterCategory

    private val _storeDataStatus = MutableLiveData<StoreDataStatus>()
    val storeDataStatus: LiveData<StoreDataStatus> get() = _storeDataStatus

    private val _dataStatus = MutableLiveData<StoreDataStatus>()
    val dataStatus: LiveData<StoreDataStatus> get() = _dataStatus

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isConnected = MutableLiveData<Boolean?>()
    val isConnected: LiveData<Boolean?> get() = _isConnected


    // TODO: create live data for check from the connection of network

    init {
        viewModelScope.launch {
            authRepository
            authRepository.hardRefreshUserData()
            getUserLikes(0L)

            if (isUserASeller){
                Log.i(TAG,"Seller")
            }else{
                Log.i(TAG,"customer")
            }
            Log.i(TAG,"userId = $userId")

        }

        if (isUserASeller)
            getProductsByOwner()
        else
            getProducts()
    }


    fun setConnectivityState(b: Boolean){
        _isConnected.value = b
    }

    fun setDataLoaded() {
        _storeDataStatus.value = StoreDataStatus.DONE
    }

    fun isProductLiked(productId: String): Boolean {
        return _userLikes.value?.contains(productId) == true
    }


    fun toggleLikeByProductId(productId: String) {
        Log.d(TAG, "Toggling Like")
        viewModelScope.launch {
            getUserLikes(200)
            val isLiked = isProductLiked(productId)
            val allLikes = _userLikes.value?.toMutableList() ?: mutableListOf()
            val deferredRes = async {
                if (isLiked) {
                    authRepository.removeProductFromLikes(productId, userId!!)
                } else {
                    authRepository.insertProductToLikes(productId, userId!!)
                }
            }
            val res = deferredRes.await()
            if (res is Result.Success) {
                if (isLiked) {
                    allLikes.remove(productId)
                } else {
                    allLikes.add(productId)
                }
                _userLikes.value = allLikes
                val proList = _likedProducts.value?.toMutableList() ?: mutableListOf()
                val pro = proList.find { it.productId == productId }
                if (pro != null) {
                    proList.remove(pro)
                }
                _likedProducts.value = proList
                Log.d(TAG, "onToggleLike: Success")
            } else {
                if (res is Error) {
                    Log.d(TAG, "onToggleLike: Error, ${res.message}")
                }
            }
            getUserLikes(0L)
        }

    }

    fun isProductInCart(productId: String): Boolean {
        return false
    }

    fun toggleProductInCart(product: Product,onSuccess:(String?)-> Unit,onError:(String?)-> Unit) {
        var itemId = ""
        viewModelScope.launch {
            async {
                authRepository.getUserDataById(userId!!){ user ->
                    if(user != null){
                        user.cart.forEach { cartItem ->
                            if (cartItem.productId == product.productId){ // cart item does is exist in user cart
                                itemId = cartItem.productId
                            }
                        }
                        if (itemId != product.productId){ // you can add this item in cart
                            val uniqueId = UUID.randomUUID().toString()
                            val cartItem = UserData.CartItem(uniqueId,product.productId,product.owner,0,"",0)
                            async {
                                authRepository.insertCartItemByUserId(cartItem,userId!!)
                                onSuccess("item is added")
                            }
                        }else{ // item already exist in cart
                            async {
                                onError("item is exist in your cart")
                            }
                        }
                    }else{ // user is not found

                    }

                }
            }

//            j.await()
        }

    }

    fun setDataLoading() {
        _dataStatus.value = StoreDataStatus.LOADING
    }

    private fun getProducts() {
        _allProducts = Transformations.switchMap(productsRepository.observeProducts()) {
            getProductsLiveData(it)
        } as MutableLiveData<List<Product>>
        viewModelScope.launch {
            _storeDataStatus.value = StoreDataStatus.LOADING
            val res = async { productsRepository.refreshProducts() }
            res.await()
            Log.d(TAG, "getAllProducts: status = ${_storeDataStatus.value}")
        }
    }


    fun getUserLikes(d: Long) {
        viewModelScope.launch {
            delay(d)
            val correctLikesIds = arrayListOf<String>() // for correction error
            val res = authRepository.getLikesByUserId(userId!!)
            if (res is Result.Success){
                val likes = res.data
                if (likes != null){
                    likes.forEach {
                        if (it != ""){
                            correctLikesIds.add(it)
                        }
                    }
                    _userLikes.value = correctLikesIds
                    getLikedProducts()
                }else{
                    _userLikes.value = emptyList()
                }
            }
        }
    }


//    fun getUserLikes() {
//        viewModelScope.launch {
//            val res = authRepository.getLikesByUserId(userId!!)
//            if (res is Result.Success) {
//                val data = res.data ?: emptyList()
//                if (data[0] != "") {
//                    _userLikes.value = data
//                } else {
//                    _userLikes.value = emptyList()
//                }
//                Log.d(TAG, "Getting Likes: Success")
//            } else {
//                _userLikes.value = emptyList()
//                if (res is Error)
//                    Log.d(TAG, "Getting Likes: Error, ${res.message}")
//            }
//        }
//    }



    private fun getLikedProducts() {
//        _dataStatus.value = StoreDataStatus.LOADING

            val res: List<Product> = if (_userLikes.value != null) {
                val allLikes = _userLikes.value ?: emptyList()
                if (!allLikes.isNullOrEmpty()) {
                    Log.d(TAG, "alllikes = ${allLikes.size}")
                    _dataStatus.value = StoreDataStatus.DONE
                    allLikes.map { proId ->
                        _allProducts.value?.find { it.productId == proId } ?: Product()
                    }
                } else {
                    _dataStatus.value = StoreDataStatus.ERROR
                    emptyList()
                }
            } else {
                _dataStatus.value = StoreDataStatus.ERROR
                emptyList()
            }
        _likedProducts.value = res




    }


    private fun getProductsLiveData(result: Result<List<Product>?>?): LiveData<List<Product>> {
        val res = MutableLiveData<List<Product>>()
        if (result is Result.Success) {
            Log.d(TAG, "result is success")
            _storeDataStatus.value = StoreDataStatus.DONE
            res.value = result.data ?: emptyList()
        } else {
            Log.d(TAG, "result is not success")
            res.value = emptyList()
            _storeDataStatus.value = StoreDataStatus.ERROR
            if (result is Error)
                Log.d(TAG, "getProductsLiveData: Error Occurred: ${result.message}")
        }
        return res
    }

    private fun getProductsByOwner() {
        _allProducts =
            Transformations.switchMap(productsRepository.observeProductsByOwner(userId!!)) {
                Log.d(TAG, it.toString())
                getProductsLiveData(it)
            } as MutableLiveData<List<Product>>
        viewModelScope.launch {
            _storeDataStatus.value = StoreDataStatus.LOADING
            val res = async { productsRepository.refreshProducts() }
            res.await()
            Log.d(TAG, "getProductsByOwner: status = ${_storeDataStatus.value}")
        }
    }

    fun refreshProducts() {
        getProducts()
    }

    fun filterBySearch(queryText: String) {
        filterProducts(_filterCategory.value!!)
        _products.value = _products.value?.filter { product ->
            product.name.contains(queryText, true) or
                    ((queryText.toDoubleOrNull() ?: 0.0).compareTo(product.price) == 0)
        }
    }

    fun filterProducts(filterType: String) {
        Log.d(TAG, "filterType is $filterType")
        _filterCategory.value = filterType
        _products.value = when (filterType) {
            "None" -> emptyList()
            "All" -> _allProducts.value
            else -> _allProducts.value?.filter { product ->
                product.category == filterType
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val delRes = async { productsRepository.deleteProductById(productId) }
            when (val res = delRes.await()) {
                is Result.Success -> Log.d(TAG, "onDelete: Success")
                is Error -> Log.d(TAG, "onDelete: Error, ${res.message}")
                else -> Log.d(TAG, "onDelete: Some error occurred!")
            }
        }
    }


    // TODO: make the sign out function inside work manager and require network
    fun signOut(onComplete:(Boolean)-> Unit, onError: (String) -> Unit) {
        val isConnected = _isConnected.value
        if (isConnected != null){
            if (isConnected){
                viewModelScope.launch {
                    val deferredRes = async { authRepository.signOut() }
                    deferredRes.await()
                    async { shopApp.removeDB() }
                    async { sessionManager.logoutFromSession() }
                    onComplete(true)
                }
            }else{
                onComplete(false)
                onError("Connection is not found!")
            }
        }


    }

    fun getAllOrders() {
        viewModelScope.launch {
            _storeDataStatus.value = StoreDataStatus.LOADING
            val deferredRes = async { authRepository.getOrdersByUserId(userId!!) }
            val res = deferredRes.await()
            if (res is Result.Success) {
                _userOrders.value = res.data ?: emptyList()
                _storeDataStatus.value = StoreDataStatus.DONE
                Log.d(TAG, "Getting Orders: Success")
            } else {
                _userOrders.value = emptyList()
                _storeDataStatus.value = StoreDataStatus.ERROR
                if (res is Error)
                    Log.d(TAG, "Getting Orders: Error, ${res.message}")
            }
        }
    }

    fun getOrderDetailsByOrderId(orderId: String) {
        viewModelScope.launch {
            _storeDataStatus.value = StoreDataStatus.LOADING
            if (_userOrders.value != null) {
                val orderData = _userOrders.value!!.find { it.orderId == orderId }
                if (orderData != null) {
                    _selectedOrder.value = orderData
                    _orderProducts.value =
                        orderData.items.map {
                            _allProducts.value?.find { pro -> pro.productId == it.productId }
                                ?: Product()
                        }
                    _storeDataStatus.value = StoreDataStatus.DONE
                } else {
                    _selectedOrder.value = null
                    _storeDataStatus.value = StoreDataStatus.ERROR
                }
            }
        }
    }

    fun onSetStatusOfOrder(orderId: String, status: String) {
        val currDate = Calendar.getInstance()
        val dateString =
            "${Month.values()[(currDate.get(Calendar.MONTH))].name} ${
                currDate.get(Calendar.DAY_OF_MONTH)
            }, ${currDate.get(Calendar.YEAR)}"
        Log.d(TAG, "Selected Status is $status ON $dateString")
        setStatusOfOrder(orderId, "$status ON $dateString")
    }

    private fun setStatusOfOrder(orderId: String, statusString: String) {
        viewModelScope.launch {
            _storeDataStatus.value = StoreDataStatus.LOADING
            val deferredRes = async {
                authRepository.setStatusOfOrder(orderId, userId!!, statusString)
            }
            val res = deferredRes.await()
            if (res is Result.Success) {
                val orderData = _selectedOrder.value
                orderData?.status = statusString
                _selectedOrder.value = orderData
                getOrderDetailsByOrderId(orderId)
            } else {
                _storeDataStatus.value = StoreDataStatus.ERROR
                if (res is Error)
                    Log.d(TAG, "Error updating status, ${res.message}")
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

    fun getUserData() {
//        viewModelScope.launch {
//            _dataStatus.value = StoreDataStatus.LOADING
//            val deferredRes = async { authRepository.getUserData(currentUser!!) }
//            val res = deferredRes.await()
//            if (res is Result.Success) {
//                val uData = res.data
//                _userData.value = uData
//                _dataStatus.value = StoreDataStatus.DONE
//            } else {
//                _dataStatus.value = StoreDataStatus.ERROR
//                _userData.value = null
//            }
//        }
    }
}