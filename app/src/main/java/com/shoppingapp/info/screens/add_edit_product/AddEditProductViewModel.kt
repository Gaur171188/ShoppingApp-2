package com.shoppingapp.info.screens.add_edit_product

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.repository.product.RemoteProductRepository
import com.shoppingapp.info.utils.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.shoppingapp.info.utils.Result



class AddEditProductViewModel(application: Application): AndroidViewModel(application) {

    companion object{
        private const val TAG = "AddEditProductViewModel"
    }


    private val sharePrefManager = SharePrefManager(application)
    private val repository = RemoteProductRepository()

    private val _productSubmitStatus = MutableLiveData<DataStatus>()
    val productSubmitStatus: LiveData<DataStatus> = _productSubmitStatus

    private val _productDeleteStatus = MutableLiveData<DataStatus>()
    val productDeleteStatus: LiveData<DataStatus> = _productDeleteStatus

    private val _productUpdateStatus = MutableLiveData<DataStatus>()
    val productUpdateStatus: LiveData<DataStatus> = _productUpdateStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorStatus: LiveData<String> = _errorMessage

    private val _loadDataStatus = MutableLiveData<DataStatus>()
    val loadDataStatus: LiveData<DataStatus> = _loadDataStatus

    private val _productData = MutableLiveData<Product?>()
    val productData: LiveData<Product?> = _productData



    fun deleteProduct(productId: String) {
        Log.d(TAG, "onLoad: Getting product Data")
        _productDeleteStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            repository.deleteProduct(productId)
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
        _productSubmitStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            // upload image uri then save it inside product data
            val imageUris = repository.insertFiles(imgList)
            product.images = imageUris
            product.owner = sharePrefManager.getName()!!
            repository.insertProduct(product)
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
        _productUpdateStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            val newImagesUri = repository.updateFiles(newImages,oldImages)
            product.images = newImagesUri
            Log.d(TAG,product.productId)

            repository.updateProduct(product)
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




}