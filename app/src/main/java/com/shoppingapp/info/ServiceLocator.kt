package com.shoppingapp.info

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.shoppingapp.info.local.ProductDataSource
import com.shoppingapp.info.repository.AuthRepoInterface
import com.shoppingapp.info.repository.AuthRepository
import com.shoppingapp.info.utils.ShoppingAppSessionManager
import com.shoppingapp.info.local.ProductsLocalDataSource
import com.shoppingapp.info.local.ShoppingAppDatabase
import com.shoppingapp.info.local.UserLocalDataSource
import com.shoppingapp.info.remote.AuthRemoteDataSource
import com.shoppingapp.info.remote.ProductsRemoteDataSource
import com.shoppingapp.info.repository.ProductsRepoInterface
import com.shoppingapp.info.repository.ProductsRepository


object ServiceLocator {
	private var database: ShoppingAppDatabase? = null
	private val lock = Any()

	@Volatile
	var authRepository: AuthRepoInterface? = null
		@VisibleForTesting set

	@Volatile
	var productsRepository: ProductsRepoInterface? = null
		@VisibleForTesting set

	fun provideAuthRepository(context: Context): AuthRepoInterface {
		synchronized(this) {
			return authRepository ?: createAuthRepository(context)
		}
	}

	fun provideProductsRepository(context: Context): ProductsRepoInterface {
		synchronized(this) {
			return productsRepository ?: createProductsRepository(context)
		}
	}

//	@VisibleForTesting
	fun resetRepository() {
		synchronized(lock) {
			database?.apply {
				clearAllTables()
				close()
			}
			database = null
			authRepository = null
		}
	}

	private fun createProductsRepository(context: Context): ProductsRepoInterface {
		val newRepo =
			ProductsRepository(ProductsRemoteDataSource(), createProductsLocalDataSource(context))
		productsRepository = newRepo
		return newRepo
	}

	private fun createAuthRepository(context: Context): AuthRepoInterface {
		val appSession = ShoppingAppSessionManager(context.applicationContext)
		val newRepo =
			AuthRepository(createUserLocalDataSource(context), AuthRemoteDataSource(context), appSession)
		authRepository = newRepo
		return newRepo
	}

	private fun createProductsLocalDataSource(context: Context): ProductDataSource {
		val database = database ?: ShoppingAppDatabase.getInstance(context.applicationContext)
		return ProductsLocalDataSource(database.productsDao())
	}

	private fun createUserLocalDataSource(context: Context): UserDataSource {
		val database = database ?: ShoppingAppDatabase.getInstance(context.applicationContext)
		return UserLocalDataSource(database.userDao())
	}
}