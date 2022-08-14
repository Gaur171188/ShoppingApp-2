package com.shoppingapp.info.repository.product

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.utils.ERR_UPLOAD
import kotlinx.coroutines.tasks.await
import java.util.*

class RemoteProductRepository() {

	private val fireStore = FirebaseFirestore.getInstance()
	private val storage = FirebaseStorage.getInstance()
	private val storageRef = storage.reference

	private fun productsPath() = fireStore.collection(PRODUCT_COLLECTION)

	private val _products = MutableLiveData<List<Product>?>()
	val products: LiveData<List<Product>?> = _products


//	init {
//	    observeProducts()
//	}


	private fun observeProducts() {
		productsPath().addSnapshotListener { value, error ->
			if (error == null){
				if (value != null){
					val products = value.toObjects(Product::class.java)
					_products.value = products
				}else{
					_products.value = emptyList()
				}
			}
		}
	}

	suspend fun getProducts() = productsPath().get()

	suspend fun insertProduct(product: Product) = productsPath().document(product.productId).set(product.toHashMap())

	suspend fun updateProduct(proData: Product) = productsPath().document(proData.productId).update(proData.toHashMap())

	suspend fun getProductById(productId: String) = productsPath().document(productId).get().await().toObject(Product::class.java)

	suspend fun deleteProduct(productId: String): Task<Void> {
		val product = getProductById(productId)
		product?.images?.forEach { imageUri->
			//deleting images first
			deleteFile(imageUri)
		}

		// delete product data
		return productsPath().document(productId).delete()
	}


	suspend fun uploadFile(uri: Uri, fileName: String): Uri? {
		val imgRef = storageRef.child("$PRODUCTS_IMAGES/$fileName")
		val uploadTask = imgRef.putFile(uri)
		val uriRef = uploadTask.continueWithTask { task ->
			if (!task.isSuccessful) {
				task.exception?.let { throw it }
			}
			imgRef.downloadUrl
		}
		return uriRef.await()
	}


	suspend fun insertFiles(filesUri: List<Uri>): List<String> {
		var urlList = mutableListOf<String>()
		filesUri.forEach label@{ uri ->
			val uniId = UUID.randomUUID().toString()
			val fileName = uniId + uri.lastPathSegment?.split("/")?.last()
			try {
				val downloadUrl = uploadFile(uri, fileName)
				urlList.add(downloadUrl.toString())
			} catch (e: Exception) {
				Log.d(TAG, "exception: message = $e")
				revertUpload(fileName)
				urlList = mutableListOf()
				urlList.add(ERR_UPLOAD)
				return@label
			}
		}
		return urlList
	}


	suspend fun updateFiles(newList: List<Uri>, oldList: List<String>): List<String> {
		var urlList = mutableListOf<String>()
		newList.forEach label@{ uri ->
			if (!oldList.contains(uri.toString())) {
				val uniId = UUID.randomUUID().toString()
				val fileName = uniId + uri.lastPathSegment?.split("/")?.last()
				try {
					val downloadUrl = uploadFile(uri, fileName)
					urlList.add(downloadUrl.toString())
				} catch (e: Exception) {
					Log.d(TAG, "exception: message = $e")
					revertUpload(fileName)
					urlList = mutableListOf()
					urlList.add(ERR_UPLOAD)
					return@label
				}
			} else {
				urlList.add(uri.toString())
			}
		}
		oldList.forEach { imgUrl ->
			if (!newList.contains(imgUrl.toUri())) {
				deleteFile(imgUrl)
			}
		}
		return urlList
	}

	private fun deleteFile(fileUri: String) = storage.getReferenceFromUrl(fileUri).delete()

	private fun revertUpload(fileName: String) = storageRef.child("$PRODUCTS_IMAGES/$fileName")


	companion object {
		private const val PRODUCTS_IMAGES = "products_Images"
		private const val PRODUCT_COLLECTION = "products"
		private const val PRODUCT_ID_FIELD = "productId"
		private const val TAG = "ProductsRemoteSource"
	}

}