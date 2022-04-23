package com.shoppingapp.info.repository.product

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.utils.Result
import kotlinx.coroutines.tasks.await

class RemoteProductRepository() {


	private val rootStore = Firebase.firestore
	private val rootStorage = Firebase.storage

	private val observableProducts = MutableLiveData<Result<List<Product>>?>()

	private val storageRef = rootStorage.reference

	private fun productsCollectionRef() = rootStore.collection(PRODUCT_COLLECTION)

	suspend fun refreshProducts() {
		observableProducts.value = getAllProducts()
	}

	fun observeProducts() = observableProducts


	// here you must update all the user data check if the product is still in remote database then update the user cart.
	suspend fun hardRefreshData(){
	}

	suspend fun getAllProducts(): Result<List<Product>> {
		val resRef = productsCollectionRef().get().await()
		return if (!resRef.isEmpty) {
			Result.Success(resRef.toObjects(Product::class.java))
		} else {
			Result.Error(Exception("Error getting Products!"))
		}
	}

	suspend fun insertProduct(newProduct: Product) {
		productsCollectionRef().add(newProduct.toHashMap()).await()
	}

	suspend fun updateProduct(proData: Product) {
		val resRef =
			productsCollectionRef().whereEqualTo(PRODUCT_ID_FIELD, proData.productId).get().await()
		if (!resRef.isEmpty) {
			val docId = resRef.documents[0].id
			productsCollectionRef().document(docId).set(proData.toHashMap()).await()
		} else {
			Log.d(TAG, "onUpdateProduct: product with id: $proData.productId not found!")
		}
	}

	suspend fun getProductById(productId: String): Result<Product> {
		val resRef = productsCollectionRef().whereEqualTo(PRODUCT_ID_FIELD, productId).get().await()
		return if (!resRef.isEmpty) {
			Result.Success(resRef.toObjects(Product::class.java)[0])
		} else {
			Result.Error(Exception("Product with id: $productId Not Found!"))
		}
	}

	suspend fun deleteProduct(productId: String) {
		Log.d(TAG, "onDeleteProduct: delete product with Id: $productId initiated")
		val resRef = productsCollectionRef().whereEqualTo(PRODUCT_ID_FIELD, productId).get().await()
		if (!resRef.isEmpty) {
			val product = resRef.documents[0].toObject(Product::class.java)
			val imgUrls = product?.images

			//deleting images first
			imgUrls?.forEach { imgUrl ->
				deleteImage(imgUrl)
			}

			//deleting doc containing product
			val docId = resRef.documents[0].id
			productsCollectionRef().document(docId).delete().addOnSuccessListener {
				Log.d(TAG, "onDelete: DocumentSnapshot successfully deleted!")
			}.addOnFailureListener { e ->
				Log.w(TAG, "onDelete: Error deleting document", e)
			}
		} else {
			Log.d(TAG, "onDeleteProduct: product with id: $productId not found!")
		}
	}

	suspend fun uploadImage(uri: Uri, fileName: String): Uri? {
		val imgRef = storageRef.child("$SHOES_STORAGE_PATH/$fileName")
		val uploadTask = imgRef.putFile(uri)
		val uriRef = uploadTask.continueWithTask { task ->
			if (!task.isSuccessful) {
				task.exception?.let { throw it }
			}
			imgRef.downloadUrl
		}
		return uriRef.await()
	}



	fun deleteImage(imgUrl: String) {
		val ref = rootStorage.getReferenceFromUrl(imgUrl)
		ref.delete().addOnSuccessListener {
			Log.d(TAG, "onDelete: image deleted successfully!")
		}.addOnFailureListener { e ->
			Log.d(TAG, "onDelete: Error deleting image, error: $e")
		}
	}

	fun revertUpload(fileName: String) {
		val imgRef = storageRef.child("$SHOES_STORAGE_PATH/$fileName")
		imgRef.delete().addOnSuccessListener {
			Log.d(TAG, "onRevert: File with name: $fileName deleted successfully!")
		}.addOnFailureListener { e ->
			Log.d(TAG, "onRevert: Error deleting file with name = $fileName, error: $e")
		}
	}

	companion object {
		private const val PRODUCT_COLLECTION = "products"
		private const val PRODUCT_ID_FIELD = "productId"
		private const val SHOES_STORAGE_PATH = "Shoes"
		private const val TAG = "ProductsRemoteSource"
	}

}