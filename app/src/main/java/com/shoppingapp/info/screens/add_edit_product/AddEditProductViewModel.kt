package com.shoppingapp.info.screens.add_edit_product

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.product.RemoteProductRepository
import com.shoppingapp.info.utils.*
import kotlinx.coroutines.launch


class AddEditProductViewModel(private val productRepo: ProductRepository): ViewModel() {

    companion object{
        private const val TAG = "AddEditProductViewModel"
    }

//
//    private val sharePrefManager = SharePrefManager(application)
//    private val productRepo = RemoteProductRepository()

    private val _productSubmitStatus = MutableLiveData<DataStatus?>()
    val productSubmitStatus: LiveData<DataStatus?> = _productSubmitStatus

    private val _productDeleteStatus = MutableLiveData<DataStatus?>()
    val productDeleteStatus: LiveData<DataStatus?> = _productDeleteStatus

    private val _productUpdateStatus = MutableLiveData<DataStatus?>()
    val productUpdateStatus: LiveData<DataStatus?> = _productUpdateStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorStatus: LiveData<String> = _errorMessage

    private val _productData = MutableLiveData<Product?>()
    val productData: LiveData<Product?> = _productData



    fun deleteProduct(productId: String) {
        Log.d(TAG, "onLoad: Deleting product Data")
        resetStatus()
        _productDeleteStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            productRepo.deleteProduct(productId)
                .addOnSuccessListener {
                    Log.d(TAG,"onDeleteProduct: product has been deleted success")
                    _productDeleteStatus.value = DataStatus.SUCCESS
                }
                .addOnFailureListener { e ->
                    Log.d(TAG,"onDeleteProduct: delete product failed due to ${e.message}")
                    _productDeleteStatus.value = DataStatus.ERROR
                }
        }
    }




    fun submitProduct(product: Product, imgList: MutableList<Uri>) {
        Log.d(TAG, "onLoad: Submit product Data")
        resetStatus()
        _productSubmitStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            // upload image uri then save it inside product data
            val imageUris = productRepo.insertFiles(imgList)
            product.images = imageUris
//            product.owner = sharePrefManager.getName()!!
            productRepo.insertProduct(product)
                .addOnSuccessListener {
                    _productSubmitStatus.value = DataStatus.SUCCESS
                    Log.d(TAG,"OnAddingProduct: Product has been added")
                }
                .addOnFailureListener { e ->
                    _productSubmitStatus.value = DataStatus.ERROR
                    Log.d(TAG,"OnAddingProduct: Failed")
                }
        }
    }


    fun updateProduct(product: Product, newImages: List<Uri>,oldImages: List<String>) {
        Log.d(TAG, "onLoad: update product Data")
        resetStatus()
        _productUpdateStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            val newImagesUri = productRepo.updateFiles(newImages,oldImages)
            product.images = newImagesUri
            Log.d(TAG,product.productId)

            productRepo.updateProduct(product)
                .addOnSuccessListener {
                    Log.d(TAG,"OnAddingProduct: Product has been Update")
                    _productUpdateStatus.value = DataStatus.SUCCESS
                }
                .addOnFailureListener { e ->
                    _productUpdateStatus.value = DataStatus.ERROR
                    Log.d(TAG,"OnAddingProduct: Failed to update due to ${e.message}")
                }
        }
    }


    fun resetStatus(){
        _productUpdateStatus.value = null
        _productSubmitStatus.value = null
        _productDeleteStatus.value = null
    }


}