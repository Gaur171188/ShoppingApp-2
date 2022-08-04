package com.shoppingapp.info
import android.app.Application
import com.shoppingapp.info.utils.x
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import viewModelModules


class ShoppingApplication() : Application() {


	override fun onCreate() {
		super.onCreate()
		x.context = this

		startKoin {
			androidContext(this@ShoppingApplication)

			modules(listOf( viewModelModules ))
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