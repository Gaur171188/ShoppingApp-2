//package com.shoppingapp.info.local.api
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.shoppingapp.info.data.Product
//
//
///**
//  the function of this interface is interactive with room data base.
// * **/
////
//@Dao
//interface ProductApi {
//
//	@Insert(onConflict = OnConflictStrategy.REPLACE)
//	suspend fun insert(product: Product)
//
//	@Insert(onConflict = OnConflictStrategy.REPLACE)
//	suspend fun insertListOfProducts(products: List<Product>)
//
//	@Query("SELECT * FROM products")
//	suspend fun getAllProducts(): List<Product>
//
//	@Query("SELECT * FROM products")
//	fun observeProducts(): LiveData<List<Product>>
//
//	@Query("SELECT * FROM products WHERE owner = :ownerId")
//	fun observeProductsByOwner(ownerId: String): LiveData<List<Product>>
//
//	@Query("SELECT * FROM products WHERE productId = :proId")
//	suspend fun getProductById(proId: String): Product?
//
//	@Query("SELECT * FROM products WHERE owner = :ownerId")
//	suspend fun getProductsByOwnerId(ownerId: String): List<Product>
//
//	@Query("DELETE FROM products WHERE productId = :proId")
//	suspend fun deleteProductById(proId: String): Int
//
//	@Query("DELETE FROM products")
//	suspend fun deleteAllProducts()
//}