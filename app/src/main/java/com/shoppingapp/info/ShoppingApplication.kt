package com.shoppingapp.info

import android.app.Application
import android.content.Context
import com.shoppingapp.info.di.*
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.utils.x
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ShoppingApplication() : Application() {


	override fun onCreate() {
		super.onCreate()
		x.context = this

		startKoin {
			androidContext(this@ShoppingApplication)

//			loadKoinModules(
//				module(override = true){ userLiveDataModule} ,
//			)

			modules(listOf(
				productLiveDataModule,
				viewModelsModules,
				userRepositoryModule,
				productRepositoryModule,
				localDataBase,
			))
		}

	}

}