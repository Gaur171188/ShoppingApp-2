package com.shoppingapp.info.screens.favorites

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.Result
import com.shoppingapp.info.ShoppingApplication
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.orders.OrdersViewModel
import com.shoppingapp.info.utils.ShoppingAppSessionManager
import com.shoppingapp.info.utils.StoreDataStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application): AndroidViewModel(application) {

    companion object{
        const val TAG = "FavoritesViewModel"
    }




    private val sessionManager = ShoppingAppSessionManager(application.applicationContext)
    private val currentUser = sessionManager.getUserIdFromSession()
    private val appShop = ShoppingApplication(application.applicationContext)


    private val productsRepository by lazy { appShop.productsRepository }

    private val _dataStatus = MutableLiveData<StoreDataStatus>()
    val dataStatus: LiveData<StoreDataStatus> = _dataStatus



    private var _likedProducts = MutableLiveData<List<Product>>()
    val likedProducts: LiveData<List<Product>> get() = _likedProducts





    fun initData(likes: List<Product>){
        _dataStatus.value = StoreDataStatus.LOADING
        _likedProducts.value = likes
    }

    fun loadingIsDone(){
        _dataStatus.value = StoreDataStatus.DONE
    }

//    private fun getProducts() {
//        _dataStatus.value = StoreDataStatus.LOADING
//        viewModelScope.launch {
//            delay(200)
//            val res = productsRepository.getProducts()
//            if(res is Result.Success){
//                val products =  res.data

//                Log.i(TAG,"all products: ${products.size}")
//            }
//        }
//    }
//


//    fun getUserLikes(onError:(String) -> Unit){
//        Log.d(TAG,"Getting likes ids..")
//        viewModelScope.launch {
//            val res = authRepository.getLikesByUserId(currentUser!!)
//            if (res is Result.Success){
//                val likes = res.data
//                if (likes != null){
//                    // do something by likes
//                    Log.i(TAG,"likes: ${likes.size}")
//                    _userLikes.value = likes
//                }else{ // no likes
//                    onError("no likes found!")
//                    _userLikes.value = arrayListOf()
//                }
//            }else{ // error happening
//                onError("error in during getting likes!")
//                _userLikes.value = arrayListOf()
//            }
//        }
//    }

//    fun loadingIsDone(){
//        _dataStatus.value = StoreDataStatus.DONE
//    }


//    private fun isProductLiked(productId: String): Boolean {
//        return _userLikes.value?.contains(productId) == true
//    }

//
//    fun toggleLikeByProductId(productId: String) {
//        _dataStatus.value = StoreDataStatus.LOADING
//        viewModelScope.launch {
//            val isLiked = isProductLiked(productId)
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
//                val proList = _likedProducts.value?.toMutableList() ?: mutableListOf()
//                val pro = proList.find { it.productId == productId }
//                if (pro != null) {
//                    proList.remove(pro)
//                }
//                _likedProducts.value = proList
//                loadingIsDone()
//                Log.d(TAG, "onToggleLike: Success")
//            } else {
//                _dataStatus.value = StoreDataStatus.ERROR
//               Error("onToggleLike: Error ")
//            }
//        }
//    }
//


//    fun getLikedProducts(allProducts: List<Product>) {
//        val likedProducts: List<Product> = run {
//            val allLikes = _userLikes.value ?: emptyList()
//            allLikes.map { proId ->
//                allProducts.find { it.productId == proId } ?: Product()
//            }
//        }
//        _likedProducts.value = likedProducts
//        Log.i(TAG,"Products likes: ${likedProducts.size}")
//    }
//


}