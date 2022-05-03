package com.shoppingapp.info.screens.home

import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.screens.orders.OrdersViewModel
import com.shoppingapp.info.utils.StoreDataStatus
import java.time.Month
import com.shoppingapp.info.utils.Result
import kotlinx.coroutines.*
import java.util.*



@Suppress("DeferredResultUnused")
class HomeViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val allProducts: LiveData<List<Product>>
//    private val ownerProducts: LiveData<List<Product>>,
): ViewModel() {

    companion object{
        private const val TAG = "HomeViewModel"
    }

//    private val shopApp = ShoppingApplication(application.applicationContext)
//    private val userRepository by lazy{ shopApp.userRepository }
//    private val productsRepository by lazy { shopApp.productRepository }

    private val userId = userRepository.getUserId()


    private var _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _isItemInCart = MutableLiveData<Boolean>()
    val isItemInCart: LiveData<Boolean> get() = _isItemInCart


    private var _userOrders = MutableLiveData<List<User.OrderItem>>()
    val userOrders: LiveData<List<User.OrderItem>> get() = _userOrders

    private var _selectedOrder = MutableLiveData<User.OrderItem?>()
    val selectedOrder: LiveData<User.OrderItem?> get() = _selectedOrder

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

    private val _quantity = MutableLiveData<Int?>()
    val quantity: LiveData<Int?> = _quantity

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    private val _cartProducts = MutableLiveData<List<Product>>()
    val cartProducts: LiveData<List<Product>> = _cartProducts

    private val _cartItems= MutableLiveData<List<User.CartItem>?>()
    val cartItems: LiveData<List<User.CartItem>?> = _cartItems

    private val _itemsPrice = MutableLiveData<Map<String, Double>>()
    val itemsPrice: LiveData<Map<String, Double>> = _itemsPrice

    private val _orders= MutableLiveData<List<User.OrderItem>?>()
    val orders: LiveData<List<User.OrderItem>?> = _orders




    init {


        viewModelScope.launch {
            userRepository.hardRefreshUserData()
            refreshUser()
            refreshOrders()
        }


        _products = allProducts as MutableLiveData<List<Product>>
        if (!isUserSeller()) {
            Log.i(TAG,"customer")
            refreshStuckLikesAndCartItems()
            refreshCartData()
            getUserLikes()

        }

    }


    fun refreshOrders() {
       refreshStuckOrders()
    }



    fun set(productId: String) {
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


    fun addToCart(size: Int?, color: String?,productId: String) {
        viewModelScope.launch {
            val res = productRepository.getProductById(productId,false)
            if (res is Result.Success) {
                val update = _cartProducts.value?.toMutableList()
                update?.add(res.data)
                _cartProducts.value = update!!
                val itemId = UUID.randomUUID().toString()
                val newItem = User.CartItem(itemId, productId, res.data.owner, _quantity.value!!, color, size)
                insertCartItem(newItem)
            }
        }
    }


    private fun insertCartItem(item: User.CartItem) {
        viewModelScope.launch {
            val deferredRes = async { userRepository.insertCartItemByUserId(item) }
            val res = deferredRes.await()
            if (res is Result.Success) {
                Log.d(TAG, "onAddItem: Success")
            } else {
                if (res is Result.Error) {
                    Log.d(TAG, "onAddItem: Error, ${res}")
                }
            }
        }
    }



    fun refreshCartData() {
        viewModelScope.launch {
            val priceMap = mutableMapOf<String, Double>()
            val products = mutableListOf<Product>()
            val cart = userRepository.getUser()?.cart

                cart?.forEach { cartItem ->
                    val res = productRepository.getProductById(cartItem.productId, false)
                    if (res is Result.Success){
                        val data = res.data
                        products.add(data)
                        priceMap[cartItem.itemId] = data.price
                    }
                }
            if (!cart.isNullOrEmpty()){
                Log.d(TAG,"cart list: ${cart.size}")
                _cartItems.value = cart
            }
            _itemsPrice.value = priceMap
            _cartProducts.value = products
            Log.d(TAG,"itemsPrice list: ${priceMap.size}")
            Log.d(TAG,"cartProducts list: ${products.size}")

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


    fun isUserSeller() = userRepository.isUserSeller()

    fun setConnectivityState(b: Boolean) {
        _isConnected.value = b
    }


    // TODO: 4/23/2022  make this function work on swipe to refresh data
    fun refreshProducts() {
        viewModelScope.launch {
            val res = productRepository.refreshProducts()
            if (res is Result.Success) { // data is refreshed

            }
            getUserLikes()
        }
    }

    fun refreshStuckLikesAndCartItems(){
        viewModelScope.launch {
            val productRes = productRepository.getProducts()
            if (productRes is Result.Success){
                userRepository.refreshProductLikes(productRes.data) // refresh likes
                userRepository.refreshCartItems(productRes.data) // refresh cartItems
            }
        }
    }

    fun refreshStuckOrders(){
        viewModelScope.launch {
            val ordersRes = userRepository.getOrders()
            if (ordersRes is Result.Success){
                val orders = ordersRes.data
                _orders.value = orders
                orders?.let { userRepository.refreshOrderItems(it) }
            }
        }
    }

    fun setDataLoaded() {
        _storeDataStatus.value = StoreDataStatus.DONE
    }

    fun isProductLiked(productId: String): Boolean {
        return _userLikes.value?.contains(productId) == true
    }

    fun isProductInCart(productId: String,onComplete:(Boolean) -> Unit){
        viewModelScope.launch {
            val cart = userRepository.getUser()?.cart
            val product = cart?.find { it.productId == productId }
            if (product != null){
                onComplete(true)
            }else{
             onComplete(false)
            }
        }
    }

    fun getItemsPriceTotal(price: Map<String, Double>): Double {
        var totalPrice = 0.0
        price.forEach { (itemId, price) ->
            totalPrice += price * (_cartItems.value?.find { it.itemId == itemId }?.quantity ?: 1)
        }
        return totalPrice
    }

    fun getQuantityCount(): Int {
        var totalCount = 0
        _cartItems.value?.forEach {
            totalCount += it.quantity
        }
        return totalCount
    }


    fun deleteItemFromCart(itemId: String) {
        viewModelScope.launch {
            var cartList: MutableList<User.CartItem>

            _cartItems.value?.let { items ->
                val itemPos = items.indexOfFirst { it.itemId == itemId }
                cartList = items.toMutableList()
                val deferredRes = async { userRepository.deleteCartItem(itemId) }
                val res = deferredRes.await()
                if (res is Result.Success) {
                    cartList.removeAt(itemPos)
                    _cartItems.value = cartList
                    refreshCartData()
                } else {
                    Log.d(TAG, "onUpdateQuantity: Error Occurred: ${res}")
                }
            }
        }
    }




    fun toggleLikeByProductId(productId: String) {
        Log.d(TAG, "Toggling Like")
        viewModelScope.launch {
//            getUserLikes(200)
            val isLiked = isProductLiked(productId)
            val allLikes = _userLikes.value?.toMutableList() ?: mutableListOf()
            val deferredRes = async {
                if (isLiked) {
                    userRepository.removeProductFromLikes(productId)
                } else {
                    userRepository.insertProductToLikes(productId)
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
            getUserLikes()
        }

    }





//    fun isProductInCart(productId: String): Boolean{
//        return false
//    }

//    fun isProductInCart(productId: String): Boolean {
//        var p = ""
//        val userCart = _userData.value?.cart
//        Log.i("setImageButton","${userCart!!.size}")
//        userCart?.forEach {
//            if (it.productId == productId){
//             p = it.productId
//            }
//        }
//        return p == productId
//    }


//    // add product in cart
//    fun toggleProductInCart(product: Product,onSuccess:(String?)-> Unit,onError:(String?)-> Unit) {
//        var itemId = ""
//        return supervisorScope {
//
//            if(_user.value != null){
//                _user.value!!.cart.forEach { cartItem ->
//                    if (cartItem.productId == product.productId){ // cart item does is exist in user cart
//                        itemId = cartItem.productId
//                    }
//                }
//                if (itemId != product.productId){ // you can add this item in cart
//                    val uniqueId = UUID.randomUUID().toString()
//                    val cartItem = User.CartItem(uniqueId,product.productId,product.owner,0,"",0)
//                    async {
//                        userRepository.insertCartItemByUserId(cartItem,userId!!)
//                        onSuccess("item is added")
//                    }
//                }else{ // item already exist in cart
//                    async {
//                        onError("item is exist in your cart")
//                    }
//                }
//            }else{ // user is not found
//
//            }
//
//
//
//
////            j.await()
//        }
//
//    }


//    // add product in cart
// fun toggleProductInCart(product: Product) {
//        viewModelScope.launch {
//            if(_user.value != null){
//                val item = _user.value!!.cart.find { it.productId == product.productId }
//                if (item != null){ // you can add this item in cart
//                    val uniqueId = UUID.randomUUID().toString()
//                    val cartItem = User.CartItem(uniqueId,product.productId,product.owner,0,"",0)
//                    Result.Success( userRepository.insertCartItemByUserId(cartItem))
//                }
//
//            }
//        }
// }
//


// add product in cart
fun toggleProductInCart(product: Product,onComplete: (Result<Boolean>) -> Unit) {
        isProductInCart(product.productId){
            if (it){
                Log.d(TAG,"item is removing from cart")
                viewModelScope.launch {
                    val carts = userRepository.getUser()?.cart
                    val cart = carts?.find { product.productId == it.productId }
                    if (cart != null){
                        onComplete(userRepository.deleteCartItem(cart.itemId))

                    }
                }
            }else{
                Log.d(TAG,"item is adding to cart")
                viewModelScope.launch {
                    val uniqueId = UUID.randomUUID().toString()
                    val cartItem = User.CartItem(uniqueId,product.productId,product.owner,0,"",0)
                    onComplete(userRepository.insertCartItemByUserId(cartItem))
                }
            }

        }

}







    //
    fun setDataLoading() {
        _dataStatus.value = StoreDataStatus.LOADING
    }

//    private fun getProducts() {
//        _products = Transformations.switchMap(productRepository.observeProducts()) {
//            getProductsLiveData(it)
//        } as MutableLiveData<List<Product>>
//        viewModelScope.launch {
//            _storeDataStatus.value = StoreDataStatus.LOADING
//            val res = async { productRepository.refreshProducts() }
//            res.await()
//            Log.d(TAG, "getAllProducts: status = ${_storeDataStatus.value}")
//        }
//    }


    fun getUserLikes() {
        viewModelScope.launch {
            val correctLikesIds = arrayListOf<String>() // for correction error
            val res = userRepository.getLikesByUserId()
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
                    _products.value?.find { it.productId == proId } ?: Product()
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

//    private fun getProductsByOwner() {
//        _products = Transformations.switchMap(productsRepository.observeProductsByOwner(userId!!)) {
//                Log.d(TAG, it.toString())
//                getProductsLiveData(it)
//            } as MutableLiveData<List<Product>>
//        viewModelScope.launch {
//            _storeDataStatus.value = StoreDataStatus.LOADING
//            val res = async { productsRepository.refreshProducts() }
//            res.await()
//            Log.d(TAG, "getProductsByOwner: status = ${_storeDataStatus.value}")
//        }
//    }

//    fun refreshProducts() {
//        getProducts()
//    }

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
            "All" -> _products.value
            else -> _products.value?.filter { product ->
                product.category == filterType
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val delRes = async { productRepository.deleteProductById(productId) }
            when (val res = delRes.await()) {
                is Result.Success -> Log.d(TAG, "onDelete: Success")
                is Error -> Log.d(TAG, "onDelete: Error, ${res.message}")
                else -> Log.d(TAG, "onDelete: Some error occurred!")
            }
        }
    }




    fun getAllOrders() {
        viewModelScope.launch {
            _storeDataStatus.value = StoreDataStatus.LOADING
            val deferredRes = async { userRepository.getOrders() }
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
                            _products.value?.find { pro -> pro.productId == it.productId }
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
                userRepository.setStatusOfOrder(orderId, userId!!, statusString)
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

    private fun refreshUser() {
        viewModelScope.launch {
            val user = userRepository.getUser()
            if (user != null){
                _user.value = user
                _cartItems.value = user.cart
            }else{
                _user.value = null
            }
        }
    }


}