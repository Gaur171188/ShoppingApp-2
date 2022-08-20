package com.shoppingapp.info.screens.statistics

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.users.Users
import com.shoppingapp.info.utils.DataStatus
import com.shoppingapp.info.utils.Result
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class StatisticsViewModel(
    private val userRepo: UserRepository,
    private val productRepo: ProductRepository): ViewModel() {

    companion object { private const val TAG = "StatisticsViewModel" }

    /** progress **/
    private val _productsStatus = MutableLiveData<DataStatus?>()
    val productsStatus: LiveData<DataStatus?> = _productsStatus

    /** progress **/
    private val _usersStatus = MutableLiveData<DataStatus?>()
    val usersStatus: LiveData<DataStatus?> = _usersStatus

    private val _updateUserState = MutableLiveData<DataStatus?>()
    val updateUserState: LiveData<DataStatus?> = _updateUserState

    private val _products = MutableLiveData<List<Product>?>()
    val products: LiveData<List<Product>?> = _products

    private val _users = MutableLiveData<List<User>?>()
    val users: LiveData<List<User>?> = _users

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _bindingOrders = MutableLiveData<List<User.OrderItem>>()
    val bindingOrders: LiveData<List<User.OrderItem>?> = _bindingOrders

    private val _confirmedOrders = MutableLiveData<List<User.OrderItem>?>()
    val confirmedOrders: LiveData<List<User.OrderItem>?> = _confirmedOrders

    private val _rejectedOrders = MutableLiveData<List<User.OrderItem>?>()
    val rejectedOrders: LiveData<List<User.OrderItem>?> = _rejectedOrders

    private val _deliveringOrders = MutableLiveData<List<User.OrderItem>?>()
    val deliveringOrders: LiveData<List<User.OrderItem>?> = _deliveringOrders

    private val _arrivingOrders = MutableLiveData<List<User.OrderItem>?>()
    val arrivingOrders: LiveData<List<User.OrderItem>?> = _arrivingOrders

    private val _usersCount = MutableLiveData<Int?>()
    val usersCount: LiveData<Int?> = _usersCount

    private val _orders = MutableLiveData<List<User.OrderItem?>>()
    val orders: LiveData<List<User.OrderItem?>> = _orders

    private val _cartItems = MutableLiveData<List<User.CartItem>?>()
    val cartItems: LiveData<List<User.CartItem>?> = _cartItems

    private val _usersCountries = MutableLiveData<List<String>?>()
    val usersCountries: LiveData<List<String>?> = _usersCountries

    private val _cartsCountries = MutableLiveData<List<String>?>()
    val cartsCountries: LiveData<List<String>?> = _cartsCountries


    private val _productsCountries = MutableLiveData<List<String>?>()
    val productsCountries: LiveData<List<String>?> = _productsCountries




    private val scopeIO = Dispatchers.IO



    init {
        loadUsers()
        loadProducts()
    }

    fun resetUserStatus(){
        _usersStatus.value = null
    }

    fun loadProducts() {
        Log.d(TAG,"OnLoading Products...")
        restProductsStatus()
        _productsStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            val res = withContext(scopeIO) { productRepo.loadProducts() }
            if (res is Result.Success){
                Log.d(TAG,"OnSuccess: products has been loaded success")
                val products = res.data
                _products.value = products
                setProductsCountries()
                _productsStatus.value = DataStatus.SUCCESS
            }else if (res is Result.Error){
                Log.d(TAG,"OnFailed: Products load failed due to ${res.exception.message}")
                _products.value = emptyList()
                _errorMessage.value = res.exception.message
                _productsStatus.value = DataStatus.ERROR
            }
        }
    }



    fun loadUsers() {
        Log.d(TAG,"OnLoading Users...")
        restUsersStatus()
        _usersStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            val res = withContext(scopeIO){ userRepo.loadUsers() }
            if (res is Result.Success){
                Log.d(TAG,"OnSuccess: users has been loaded success")
                val users = res.data
                _users.value = users
                _usersCount.value = users.size
                setOrders()
                setCarts()
                setUsersCountries()
                _usersStatus.value = DataStatus.SUCCESS
            }else if (res is Result.Error){
                Log.d(TAG,"OnFailed: users load failed due to ${res.exception.message}")
                _users.value = emptyList()
                _errorMessage.value = res.exception.message
                _usersStatus.value = DataStatus.ERROR
            }
        }
    }



    fun filter(userType: String, city: String, country: String, rate: String): List<User> {
        var users = _users.value
        if (userType.isNotEmpty()) {
            users = users?.filter { it.userType == userType }
        }
        if (city.isNotEmpty()){ // no city
//            users = users?.filter { it. == userType }
        }
        if (country.isNotEmpty()) {
            users = users?.filter { it.country == country }
        }
        if (rate.isNotEmpty()){
            val r = rate.toFloat()
        }

        return if(userType.isEmpty() && city.isEmpty() && country.isEmpty() && rate.isEmpty()){
            _users.value!!
        }else {
            users!!
        }
    }



    fun updateUser(user: User, index: Int) {
        Log.d(TAG,"OnUpdateUser...")
        resetUserStatus()
        _updateUserState.value = DataStatus.LOADING
        viewModelScope.launch {
            val repository = RemoteUserRepository()
            repository.updateUser(user)
                .addOnSuccessListener {
                    _updateUserState.value = DataStatus.SUCCESS
                    val users = _users.value?.toMutableList()
                    users?.set(index, user)
                    _users.value = users

                }
                .addOnFailureListener { e ->
                    Log.d(TAG,"OnUpdateBalance: update balance failed due to ${e.message}")
                    _updateUserState.value = DataStatus.ERROR
//               _errorMessage.value = updateTask.exception.message
                }

        }
    }


//    // get the items in all carts of users
//    private fun setCarts() {
//        val items = ArrayList<User.CartItem>()
//        val allCarts = _users.value?.map { it.cart }
//        allCarts?.forEach { carts->
//            carts.forEach { cart ->
//                items.add(cart)
//            }
//        }
//        _cartItems.value = items
//    }
    // get the all orders of users

    private fun setCarts() {
        val items = ArrayList<User.CartItem>()
        val allOrders = _users.value?.map { it.cart }
        allOrders?.forEach { carts->
            carts.forEach { cart ->
                items.add(cart)
            }
        }
        _cartItems.value = items
        setCartCountries()
    }



    // get the all orders of users
    private fun setOrders() {
        val items = ArrayList<User.OrderItem>()
        val allOrders = _users.value?.map { it.orders }
        allOrders?.forEach { orders->
            orders.forEach { order ->
                items.add(order)
            }
        }
        _orders.value = items
    }



    fun getOrdersByStatus(status: String) = _orders.value!!.filter { it!!.status == status }


    private fun setUsersCountries() {
        val countries = _users.value?.map { it.country }
        _usersCountries.value = countries?.toSet()?.toList()
    }


    private fun setProductsCountries() {
        val countries = _products.value?.map { it.country }
        _productsCountries.value = countries?.toSet()?.toList()
    }


    private fun setCartCountries() {
        val countries = _cartItems.value?.map { it.country }
        _cartsCountries.value = countries?.toSet()?.toList()
    }



    fun getUsersCountInEachCountry(): List<Int> {
        val countriesUser = ArrayList<Int>()
        _usersCountries.value?.forEach { country ->
            val countryUsers = _users.value?.filter { it.country == country }
            countriesUser.add(countryUsers?.size!!)
        }
        return countriesUser
    }


    fun getCartsCountInEachCountry(): List<Int> {
        val cartCountries = ArrayList<Int>()
        _cartsCountries.value?.forEach { country ->
            val countryUsers = _cartItems.value?.filter { it.country == country }
            cartCountries.add(countryUsers?.size!!)
        }
        return cartCountries
    }


    fun getProductsCountInEachCountry(): List<Int> {
        val productCountries = ArrayList<Int>()
        _productsCountries.value?.forEach { country ->
            val countryUsers = _products.value?.filter { it.country == country }
            productCountries.add(countryUsers?.size!!)
        }
        return productCountries
    }



    private fun restUsersStatus() {
        _errorMessage.value = null
        _usersStatus.value = null
    }

    private fun restProductsStatus() {
        _errorMessage.value = null
        _productsStatus.value = null
    }



}