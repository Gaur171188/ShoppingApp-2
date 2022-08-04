//package com.shoppingapp.info.repository.product
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Transformations
//import com.shoppingapp.info.data.Product
//import com.shoppingapp.info.local.api.ProductApi
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import com.shoppingapp.info.utils.Result
//
//class LocalProductRepository(private val productApi: ProductApi) {
//
//	private val ioDispatcher = Dispatchers.IO
//
//	fun observeProducts(): LiveData<Result<List<Product>>?> {
//		return try {
//			Transformations.map(productApi.observeProducts()) {
//				Result.Success(it)
//			}
//		} catch (e: Exception) {
//			Transformations.map(MutableLiveData(e)) {
//				Result.Error(e)
//			}
//		}
//	}
//
//	fun observeProductsByOwner(ownerId: String): LiveData<Result<List<Product>>?> {
//		return try {
//			Transformations.map(productApi.observeProductsByOwner(ownerId)) {
//				Result.Success(it)
//			}
//		} catch (e: Exception) {
//			Transformations.map(MutableLiveData(e)) {
//				Result.Error(e)
//			}
//		}
//	}
//
//	suspend fun getAllProducts(): Result<List<Product>> = withContext(ioDispatcher) {
//		return@withContext try {
//			Result.Success(productApi.getAllProducts())
//		} catch (e: Exception) {
//			Result.Error(e)
//		}
//	}
//
//	suspend fun getAllProductsByOwner(ownerId: String): Result<List<Product>> = withContext(ioDispatcher) {
//			return@withContext try {
//				Result.Success(productApi.getProductsByOwnerId(ownerId))
//			} catch (e: Exception) {
//				Result.Error(e)
//			}
//		}
//
//	suspend fun getProductById(productId: String): Result<Product> = withContext(ioDispatcher) {
//			try {
//				val product = productApi.getProductById(productId)
//				if (product != null) {
//					return@withContext Result.Success(product)
//				} else {
//					return@withContext Result.Error(Exception("Product Not Found!"))
//				}
//			} catch (e: Exception) {
//				return@withContext Result.Error(e)
//			}
//		}
//
//	suspend fun insertProduct(newProduct: Product) = withContext(ioDispatcher) {
//		productApi.insert(newProduct)
//	}
//
//	suspend fun updateProduct(proData: Product) = withContext(ioDispatcher) {
//		productApi.insert(proData)
//	}
//
//	suspend fun insertMultipleProducts(data: List<Product>) = withContext(ioDispatcher) {
//		productApi.insertListOfProducts(data)
//	}
//
//	suspend fun deleteProduct(productId: String): Unit = withContext(ioDispatcher) {
//		productApi.deleteProductById(productId)
//	}
//
//	suspend fun deleteAllProducts() = withContext(ioDispatcher) {
//		productApi.deleteAllProducts()
//	}
//
//}