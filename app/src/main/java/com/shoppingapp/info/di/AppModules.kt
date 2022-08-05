import com.shoppingapp.info.screens.favorites.FavoritesViewModel
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.product_details.ProductDetailsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

//package com.shoppingapp.info.di
//
//import android.app.Application
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Transformations
//import androidx.room.Room
//import com.shoppingapp.info.data.Product
//import com.shoppingapp.info.data.User
//import com.shoppingapp.info.local.ShoppingAppDatabase
//import com.shoppingapp.info.local.api.ProductApi
//import com.shoppingapp.info.local.api.UserApi
//import com.shoppingapp.info.repository.product.LocalProductRepository
//import com.shoppingapp.info.repository.product.ProductRepository
//import com.shoppingapp.info.repository.product.RemoteProductRepository
//import com.shoppingapp.info.repository.user.LocalUserRepository
//import com.shoppingapp.info.repository.user.RemoteUserRepository
//import com.shoppingapp.info.repository.user.UserRepository
//import com.shoppingapp.info.screens.account.AccountViewModel
//import com.shoppingapp.info.screens.add_edit_product.AddEditProductViewModel
//import com.shoppingapp.info.screens.cart.CartViewModel
//import com.shoppingapp.info.screens.favorites.FavoritesViewModel
//import com.shoppingapp.info.screens.home.HomeViewModel
//import com.shoppingapp.info.screens.auth.login.LoginViewModel
//import com.shoppingapp.info.screens.order_details.OrderDetailsViewModel
//import com.shoppingapp.info.screens.order_success.OrderSuccessViewModel
//import com.shoppingapp.info.screens.orders.OrdersViewModel
//import com.shoppingapp.info.screens.product_details.ProductDetailsViewModel
//import com.shoppingapp.info.screens.profile.ProfileViewModel
//import com.shoppingapp.info.screens.auth.RegistrationViewModel
//import com.shoppingapp.info.screens.select_payment.SelectPaymentViewModel
//import com.shoppingapp.info.utils.SharePrefManager
//import org.koin.android.ext.koin.androidApplication
//import org.koin.android.ext.koin.androidContext
//import org.koin.android.viewmodel.dsl.viewModel
//import com.shoppingapp.info.utils.Result
//import com.shoppingapp.info.utils.Result.Success
//import com.shoppingapp.info.utils.Result.Error
//import org.koin.dsl.module
//
//


val viewModelModules = module {
    viewModel { HomeViewModel() }
    viewModel { FavoritesViewModel() }
    viewModel { ProductDetailsViewModel() }
}


//val viewModelsModules = module {
//
//    viewModel {
//        // TODO: 4/19/2022 check if seller pass the owner product else pass all products
//        HomeViewModel(
//            userRepository = get(),
//            productRepository = get(),
//            allProducts = get(),
////            ownerProducts = get()
//        )
//    }
//    viewModel {
//        AccountViewModel(userRepository = get())
//    }
//    viewModel {
//        AddEditProductViewModel(productRepository = get())
//    }
//    viewModel {
//        CartViewModel(userRepository = get(), productRepository = get())
//    }
//    viewModel {
//        FavoritesViewModel(userRepository = get(), productRepository = get(), allProducts = get())
//    }
//    viewModel {
//        OrderDetailsViewModel()
//    }
//    viewModel {
//        OrderSuccessViewModel()
//    }
//    viewModel {
//        OrdersViewModel(userRepository = get(), productRepository = get(), allProducts = get())
//    }
////    viewModel {
////
////        ProductDetailsViewModel(
////            productRepository = get(),
////            userRepository = get(),
//////            user = get(),
////            allProducts = get())
////    }
//        viewModel {
//                (productId: String) ->
//            ProductDetailsViewModel(
//                productRepository = get(),
//                userRepository = get(),
////                productId = productId,
//                allProducts = get())
//        }
//
//    viewModel {
//        ProfileViewModel()
//    }
//
//    viewModel {
//        SelectPaymentViewModel()
//    }
//    viewModel {
//        LoginViewModel(remoteUserRepository = get(), sharePrefManager = get(), productRepository = get())
//    }
//    viewModel {
//        RegistrationViewModel(userRepository = get())
//    }
//
//}
//
//
//val userRepositoryModule = module {
//
//        single { LocalUserRepository(userApi = get()) }
//        single {RemoteUserRepository()}
//        single { UserRepository(localUserRepository = get() , remoteUserRepository = get(), sharePrefManager = get() ) }
//    }
//
//val productRepositoryModule = module {
//
//        single { LocalProductRepository(productApi = get()) }
//        single { RemoteProductRepository() }
//        single { ProductRepository( remoteProductRepository = get(), sharePrefManager = get()) }
//    }
//
//val localDataBase = module {
//
//        fun createDataBase(application: Application): ShoppingAppDatabase {
//            return Room.databaseBuilder(
//                application, ShoppingAppDatabase::class.java, "ShoppingAppDb")
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build()
//        }
//
//        fun userApi(dataBase: ShoppingAppDatabase): UserApi {
//            return dataBase.userDao()
//        }
//
//        fun productApi(dataBase: ShoppingAppDatabase): ProductApi {
//            return dataBase.productsDao()
//        }
//
//
//
//
//    single { SharePrefManager(androidContext()) }
//    single { createDataBase(androidApplication()) }
//    single { userApi(dataBase = get()) }
//    single { productApi(dataBase = get()) }
//
//    }
//
//val productLiveDataModule = module {
//
//    // todo: observe the products from the firestore
//
////        fun getProductsLiveData(result: Result<List<Product>>): LiveData<List<Product>?> {
////            val res = MutableLiveData<List<Product>?>()
////            if (result is Success) {
////                res.value = result.data
////            } else {
////                if (result is Error)
////                    res.value = null
////            }
////            return res
////        }
////
////
////        fun observeLocalProducts(productRepository: ProductRepository): LiveData<List<Product>?> {
////            return Transformations.switchMap(productRepository.observeProducts()) {
////                getProductsLiveData(it!!)
////            }as MutableLiveData<List<Product>>
////        }
//
//
//        /** products live data **/
////        single { observeLocalProducts(productRepository = get()) }
//
//
//
//    }
//
//val userLiveDataModule = module {
//
//    fun getUserLiveData(result: Result<User>): LiveData<User?> {
//        val res = MutableLiveData<User?>()
//        if (result is Success) {
//            res.value = result.data
//        } else {
//            res.value = null
//        }
//        return res
//    }
//
//
//    fun observeLocalUser(userRepository: UserRepository): MutableLiveData<User> {
//        return Transformations.switchMap(userRepository.observeLocalUser()) {
//            getUserLiveData(it!!)
//        } as MutableLiveData<User>
//    }
//
//
//    /** user live data **/
//    single  { observeLocalUser(userRepository = get()) }
//
//
//}
//
//
//
//
//
////fun <T>getLiveData(result: Result<List<T>>): LiveData<List<T>?> {
////    val res = MutableLiveData<List<T>?>()
////    if (result is Success) {
////        res.value = result.data
////    } else {
////        if (result is Error)
////            res.value = null
////    }
////    return res
////}
////
////
////fun <T>observeLocalChats(productRepository: ProductRepository): LiveData<List<T>?> {
////    return Transformations.switchMap(productRepository.observeProducts()) {
////        getLiveData(it!!)
////    }as MutableLiveData<List<T>>
////}