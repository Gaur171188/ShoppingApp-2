//package com.shoppingapp.info.screens.product_details
//
//import android.app.Application
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//
//class ProductViewModelFactory(
//    private val productId: String,
//    private val application: Application
//) : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)) {
//            return ProductDetailsViewModel(productId, application) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel Class")
//    }
//
//}