package com.shoppingapp.info.screens.favorites


import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.utils.DataStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoritesViewModel(): ViewModel() {

    companion object{
        const val TAG = "FavoritesViewModel"
    }

    private val userRepository = RemoteUserRepository()

    private val _addLikeStatus = MutableLiveData<DataStatus?>()
    val addLikeStatus: LiveData<DataStatus?> = _addLikeStatus

    private val _removeLikeStatus = MutableLiveData<DataStatus?>()
    val removeLikeStatus: LiveData<DataStatus?> = _removeLikeStatus


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage



    // TODO: 4/23/2022 make function for remove all likes from favorites




    fun removeLikeByProductId (productId: String,userId: String) {
        Log.d(TAG,"OnLikeProduct: Loading..")
        resetData()
        _removeLikeStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            userRepository.dislikeProduct(productId,userId)
                .addOnSuccessListener {
                    Log.d(TAG,"OnLikeProduct: like has been removed success")
                    _removeLikeStatus.value = DataStatus.SUCCESS
                }
                .addOnFailureListener { e ->
                    Log.d(TAG,"OnLikeProduct: error happen during removing due to ${e.message}")
                    _removeLikeStatus.value = DataStatus.ERROR
                    _errorMessage.value = e.message
                }
        }
    }



    fun resetData(){
        _addLikeStatus.value = null
        _removeLikeStatus.value = null
        _errorMessage.value = null
    }


//    fun toggleLikeByProductId(productId: String) {
//
//        viewModelScope.launch {
//
//            val isLiked = isProductLiked(productId)
//            val allLikes = _userLikes.value?.toMutableList() ?: mutableListOf()
//            val deferredRes = async {
//                if (isLiked) {
//                    userRepository.removeProductFromLikes(productId)
//                } else {
//                    userRepository.insertProductToLikes(productId)
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
//                Log.d(TAG, "onToggleLike: Success")
//            } else {
//                if (res is Error) {
//                    Log.d(TAG, "onToggleLike: Error, ${res.message}")
//                }
//            }
//
//        }
//
//    }
//
//
//    private fun getProducts() {
//        _dataStatus.value = StoreDataStatus.LOADING
//        viewModelScope.launch {
//            delay(200)
//            val res = productsRepository.getProducts()
//            if(res is Result.Success){
//                val products =  res.data
//
//                Log.i(TAG,"all products: ${products.size}")
//            }
//        }
//    }
//
//
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
//
//    fun loadingIsDone(){
//        _dataStatus.value = StoreDataStatus.DONE
//    }
//
//
//    private fun isProductLiked(product: Product): Boolean {
//        return _likedProducts.value?.contains(product) == true
//    }
//
//    // TODO: 4/19/2022 enhance function
//    fun toggleLikeByProductId(product: Product,onError:(String) -> Unit) {
//        _dataStatus.value = DataStatus.LOADING
//        viewModelScope.launch {
//            val isLiked = isProductLiked(product)
//            val allLikes = _likedProducts.value?.toMutableList() ?: mutableListOf()
//            val deferredRes = async {
//                if (isLiked) {
//                    userRepository.removeProductFromLikes(product.productId)
//                } else {
//                    userRepository.insertProductToLikes(product.productId)
//                }
//            }
//            val res = deferredRes.await()
//            if (res is Result.Success) {
//                if (isLiked) {
//                    allLikes.remove(product)
//                } else {
//                    allLikes.add(product)
//                }
//                _likedProducts.value = allLikes
//            } else {
//                _dataStatus.value = DataStatus.ERROR
//                onError("sorry error happen..")
//            }
//        }
//    }
//
//
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



}