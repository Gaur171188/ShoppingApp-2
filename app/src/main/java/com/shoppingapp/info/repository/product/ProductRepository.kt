package com.shoppingapp.info.repository.product

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.shoppingapp.info.data.Ad
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.utils.ERR_UPLOAD
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.utils.SharePrefManager
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await
import java.util.*


class ProductRepository(val context: Context, private val remote: RemoteProductRepository) {

//    private val sharePref = SharePrefManager(context)
//    private val userId = sharePref.getUserIdFromSession()


    suspend fun getProducts() = remote.getProducts()

    suspend fun loadProducts(): Result<List<Product>> {
        return supervisorScope {
            try {
                val task = async { remote.loadProducts() }
                Result.Success(task.await())
            }catch (ex: Exception){
                Result.Error(Exception(ex.message))
            }
        }
    }



    suspend fun insertProduct(product: Product) = remote.insertProduct(product)

    suspend fun updateProduct(proData: Product) = remote.updateProduct(proData)

    suspend fun deleteProduct(productId: String) = remote.deleteProduct(productId)



    suspend fun loadAds() : Result<List<Ad>> {
        return supervisorScope {
            val task = async { remote.loadAds() }
            try {
                Result.Success(task.await())
            }catch (ex: Exception){
                Result.Error(Exception(ex))
            }
        }
    }

    suspend fun insertAds(list: List<Ad>) : Result<Boolean> {
        return supervisorScope {
            val task = async { list.forEach { ad-> remote.insertAd(ad) } }
            try {
                task.await()
                Result.Success(true)
            }catch (ex: Exception){
                Result.Error(Exception(ex))
            }
        }
    }



    suspend fun insertAd(data: Ad) : Result<Boolean> {
        return supervisorScope {
            val task = async { remote.insertAd(data) }
            try {
                task.await()
                Result.Success(true)
            }catch (ex: Exception){
                Result.Error(Exception(ex))
            }
        }
    }

    suspend fun deleteAd(data: Ad) : Result<Boolean> {
        return supervisorScope {
            val task = async { remote.deleteAd(data) }
            try {
                task.await()
                Result.Success(true)
            }catch (ex: Exception){
                Result.Error(Exception(ex))
            }
        }
    }

    suspend fun updateAd(data: Ad,oldAdImage: String) : Result<Boolean> {
        return supervisorScope {
            val task = async { remote.updateAd(data,oldAdImage) }
            try {
                task.await()
                Result.Success(true)
            }catch (ex: Exception){
                Result.Error(Exception(ex))
            }
        }
    }



    suspend fun uploadFile(uri: Uri, fileName: String) = remote.uploadFile(uri, fileName)

    suspend fun insertFiles(filesUri: List<Uri>) = remote.insertFiles(filesUri)


    suspend fun updateFiles(newList: List<Uri>, oldList: List<String>) = remote.updateFiles(newList, oldList)





}









//package com.shoppingapp.info.repository.product
//
//import android.net.Uri
//import android.util.Log
//import androidx.core.net.toUri
//import com.shoppingapp.info.utils.Result.Success
//import com.shoppingapp.info.utils.Result.Error
//import com.shoppingapp.info.utils.Result
//import com.shoppingapp.info.data.Product
//import com.shoppingapp.info.utils.ERR_UPLOAD
//import com.shoppingapp.info.utils.SharePrefManager
//import kotlinx.coroutines.*
//import java.util.*
//
//class ProductRepository(
//	private val remoteProductRepository: RemoteProductRepository,
//	val sharePrefManager: SharePrefManager) {
//
//	private val userId = sharePrefManager.getUserIdFromSession()!!
//
//	companion object {
//		private const val TAG = "ProductsRepository"
//	}
//
//
////	suspend fun refreshProducts(): Result<Boolean> {
////		Log.d(TAG, "Updating Products in Room")
////		return updateProductsFromRemoteSource()
////	}
//
//
//
////	 fun observeProducts() = localProductRepository.observeProducts()
//
//	 suspend fun getProducts() = remoteProductRepository.getAllProducts()
//
////	 fun observeProductsByOwner(ownerId: String): LiveData<Result<List<Product>>?> {
////		return localProductRepository.observeProductsByOwner(ownerId)
////	}
//
////	 suspend fun getAllProductsByOwner(): Result<List<Product>> {
////		return localProductRepository.getAllProductsByOwner(userId)
////	}
//
////	 suspend fun getProductById(productId: String, forceUpdate: Boolean): Result<Product> {
////		if (forceUpdate) {
////			updateProductFromRemoteSource(productId)
////		}
////		return localProductRepository.getProductById(productId)
////	}
//
//	 suspend fun insertProduct(newProduct: Product): Result<Boolean> {
//		return supervisorScope {
////			val localRes = async {
////				Log.d(TAG, "onInsertProduct: adding product to local source")
////				localProductRepository.insertProduct(newProduct)
////			}
//			val remoteRes = async {
//				Log.d(TAG, "onInsertProduct: adding product to remote source")
//				remoteProductRepository.insertProduct(newProduct)
//			}
//
//			try {
////				localRes.await()
//				remoteRes.await()
//				Success(true)
//			} catch (e: Exception) {
//				Error(e)
//			}
//		}
//	}
//
//
//
////	 suspend fun insertImages(imgList: List<Uri>): List<String> {
////		var urlList = mutableListOf<String>()
////		imgList.forEach label@{ uri ->
////			val uniId = UUID.randomUUID().toString()
////			val fileName = uniId + uri.lastPathSegment?.split("/")?.last()
////			try {
////				val downloadUrl = remoteProductRepository.uploadFile(uri, fileName)
////				urlList.add(downloadUrl.toString())
////			} catch (e: Exception) {
////				remoteProductRepository.revertUpload(fileName)
////				Log.d(TAG, "exception: message = $e")
////				urlList = mutableListOf()
////				urlList.add(ERR_UPLOAD)
////				return@label
////			}
////		}
////		return urlList
////	}
//
//	 suspend fun updateProduct(product: Product): Result<Boolean> {
//		return supervisorScope {
//			val remoteRes = async {
//				Log.d(TAG, "onUpdate: updating product in remote source")
//				remoteProductRepository.updateProduct(product)
//			}
////			val localRes = async {
////				Log.d(TAG, "onUpdate: updating product in local source")
////				localProductRepository.insertProduct(product)
////			}
//			try {
//				remoteRes.await()
////				localRes.await()
//				Success(true)
//			} catch (e: Exception) {
//				Error(e)
//			}
//		}
//	}
//
//	 suspend fun updateImages(newList: List<Uri>, oldList: List<String>): List<String> {
//		var urlList = mutableListOf<String>()
//		newList.forEach label@{ uri ->
//			if (!oldList.contains(uri.toString())) {
//				val uniId = UUID.randomUUID().toString()
//				val fileName = uniId + uri.lastPathSegment?.split("/")?.last()
//				try {
//					val downloadUrl = remoteProductRepository.uploadFile(uri, fileName)
//					urlList.add(downloadUrl.toString())
//				} catch (e: Exception) {
//					remoteProductRepository.revertUpload(fileName)
//					Log.d(TAG, "exception: message = $e")
//					urlList = mutableListOf()
//					urlList.add(ERR_UPLOAD)
//					return@label
//				}
//			} else {
//				urlList.add(uri.toString())
//			}
//		}
//		oldList.forEach { imgUrl ->
//			if (!newList.contains(imgUrl.toUri())) {
//				remoteProductRepository.deleteFile(imgUrl)
//			}
//		}
//		return urlList
//	}
//
//	 suspend fun deleteProductById(productId: String): Result<Boolean> {
//		return supervisorScope {
//
////              val localRes = async {
////				Log.d(TAG, "onDelete: deleting product from local source")
////				localProductRepository.deleteProduct(productId)
////			}
//
//			val remoteRes = async {
//				Log.d(TAG, "onDelete: deleting product from remote source")
//				remoteProductRepository.deleteProduct(productId)
//			}
//
//			try {
//				// localRes.await()
//				remoteRes.await()
//				Success(true)
//			} catch (e: Exception) {
//				Error(e)
//			}
//		}
//	}
//
////	private suspend fun deleteAllProductsFromLocal(): Result<Boolean> {
////		 return supervisorScope {
////			 val localRes = async {  localProductRepository.deleteAllProducts() }
////			 try {
////			     localRes.await()
////				 Success(true)
////			 }catch (ex: Exception){
////			 	Error(ex)
////			 }
////		 }
////	}
//
////	private suspend fun deleteAllUserProduct(): Result<Boolean> {
////		return supervisorScope {
////			val localRes = async {  localProductRepository.deleteAllProducts() }
////			try {
////				localRes.await()
////				Success(true)
////			}catch (ex: Exception){
////				Error(ex)
////			}
////		}
////	}
//
////	// make this function return result ..
////	private suspend fun updateProductsFromRemoteSource(): Result<Boolean> {
////		return supervisorScope {
////			val remoteProducts = remoteProductRepository.getAllProducts()
////			if (remoteProducts is Success) {
////				Log.d(TAG, "pro list = ${remoteProducts.data}")
////				deleteAllProductsFromLocal()
////				localProductRepository.insertMultipleProducts(remoteProducts.data)
////				Success(true)
////			}else{
////				Success(false)
////			}
////		}
////	}
////
////	private suspend fun updateProductsFromRemoteSource(): StoreDataStatus? {
////		var res: StoreDataStatus? = null
////		try {
////			val remoteProducts = remoteProductRepository.getAllProducts()
////			if (remoteProducts is Success) {
////				Log.d(TAG, "pro list = ${remoteProducts.data}")
////				deleteAllProductsFromLocal()
////				localProductRepository.insertMultipleProducts(remoteProducts.data)
////				res = StoreDataStatus.DONE
////			} else {
////				res = StoreDataStatus.ERROR
////				if (remoteProducts is Error)
////					throw remoteProducts.exception
////			}
////		} catch (e: Exception) {
////			Log.d(TAG, "onUpdateProductsFromRemoteSource: Exception occurred, ${e.message}")
////		}
////
////		return res
////	}
//
//
//
////	private suspend fun updateProductFromRemoteSource(productId: String): StoreDataStatus? {
////		var res: StoreDataStatus? = null
////		try {
////			val remoteProduct = remoteProductRepository.getProductById(productId)
////			if (remoteProduct is Success) {
////				localProductRepository.insertProduct(remoteProduct.data)
////				res = StoreDataStatus.DONE
////			} else {
////				res = StoreDataStatus.ERROR
////				if (remoteProduct is Error)
////					throw remoteProduct.exception
////			}
////		} catch (e: Exception) {
////			Log.d(TAG, "onUpdateProductFromRemoteSource: Exception occurred, ${e.message}")
////		}
////		return res
////	}
//
//}