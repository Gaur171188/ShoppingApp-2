package com.shoppingapp.info
import android.app.Application
import com.shoppingapp.info.utils.x
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import productRepoModule
import userRepoModule
import viewModelModules


class ShoppingApplication() : Application() {


	override fun onCreate() {
		super.onCreate()
		x.context = this

		startKoin {
			androidContext(this@ShoppingApplication)

			modules(
				listOf(
				viewModelModules,
				productRepoModule,
				userRepoModule
			))

//			modules(listOf(
//				productLiveDataModule,
//				viewModelsModules,
//				userRepositoryModule,
//				productRepositoryModule,
//				localDataBase,
//			))
		}

	}

}