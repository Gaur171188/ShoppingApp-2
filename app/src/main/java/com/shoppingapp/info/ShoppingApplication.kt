package com.shoppingapp.info

import android.app.Application
import android.content.Context
import com.shoppingapp.info.repository.AuthRepoInterface
import com.shoppingapp.info.repository.ProductsRepoInterface


class ShoppingApplication(private val context: Context) : Application() {
	val authRepository: AuthRepoInterface
		get() = ServiceLocator.provideAuthRepository(context)

	val productsRepository: ProductsRepoInterface
		get() = ServiceLocator.provideProductsRepository(context)

	fun removeDB(){
		ServiceLocator.resetRepository()
	}

	override fun onCreate() {
		super.onCreate()
	}

}