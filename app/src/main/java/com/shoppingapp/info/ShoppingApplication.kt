package com.shoppingapp.info

import android.app.Application
import android.content.Context
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.user.UserRepository

class ShoppingApplication(private val context: Context) : Application() {

	val userRepository: UserRepository
		get() = ServiceLocator.provideAuthRepository(context)

	val productRepository: ProductRepository
		get() = ServiceLocator.provideProductsRepository(context)

	fun removeDB(){
		ServiceLocator.resetRepository()
	}

	override fun onCreate() {
		super.onCreate()
	}

}