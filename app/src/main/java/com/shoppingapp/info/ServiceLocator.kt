package com.shoppingapp.info

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.shoppingapp.info.local.*
import com.shoppingapp.info.repository.product.LocalProductRepository
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.product.RemoteProductRepository
import com.shoppingapp.info.repository.user.LocalUserRepository
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.utils.SharePrefManager


object ServiceLocator {
	private var database: ShoppingAppDatabase? = null
	private val lock = Any()

	@Volatile
	var userRepository: UserRepository? = null
		@VisibleForTesting set

	@Volatile
	var productRepository: ProductRepository? = null
		@VisibleForTesting set

	fun provideAuthRepository(context: Context): UserRepository {
		synchronized(this) {
			return userRepository ?: createAuthRepository(context)
		}
	}

	fun provideProductsRepository(context: Context): ProductRepository {
		synchronized(this) {
			return productRepository ?: createProductsRepository(context)
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
			userRepository = null
		}
	}

	private fun createProductsRepository(context: Context): ProductRepository {
		val newRepo =
			ProductRepository(RemoteProductRepository(), createProductsLocalDataSource(context))
		productRepository = newRepo
		return newRepo
	}

	private fun createAuthRepository(context: Context): UserRepository {
		val appSession = SharePrefManager(context.applicationContext)
		val newRepo =
			UserRepository(createUserLocalDataSource(context), RemoteUserRepository(context), appSession)
		userRepository = newRepo
		return newRepo
	}

	private fun createProductsLocalDataSource(context: Context): LocalProductRepository {
		val database = database ?: ShoppingAppDatabase.getInstance(context.applicationContext)
		return LocalProductRepository(database.productsDao())
	}

	private fun createUserLocalDataSource(context: Context): LocalUserRepository {
		val database = database ?: ShoppingAppDatabase.getInstance(context.applicationContext)
		return LocalUserRepository(database.userDao())
	}
}