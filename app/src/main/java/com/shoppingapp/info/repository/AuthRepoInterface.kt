package com.shoppingapp.info.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.shoppingapp.info.data.UserData
import com.shoppingapp.info.Result

interface AuthRepoInterface {
	suspend fun refreshData()
	suspend fun signUp(userData: UserData)
	suspend fun login(userData: UserData, rememberMe: Boolean)
//	suspend fun checkLogin(mobile: String, password: String): UserData?
	suspend fun signOut()
	suspend fun deleteUser()

	suspend fun hardRefreshUserData()
	suspend fun insertProductToLikes(productId: String, userId: String): Result<Boolean>
	suspend fun removeProductFromLikes(productId: String, userId: String): Result<Boolean>
	suspend fun insertCartItemByUserId(cartItem: UserData.CartItem, userId: String): Result<Boolean>
	suspend fun updateCartItemByUserId(cartItem: UserData.CartItem, userId: String): Result<Boolean>
	suspend fun deleteCartItemByUserId(itemId: String, userId: String): Result<Boolean>
	suspend fun placeOrder(newOrder: UserData.OrderItem, userId: String): Result<Boolean>
	suspend fun setStatusOfOrder(orderId: String, userId: String, status: String): Result<Boolean>
	suspend fun getOrdersByUserId(userId: String): Result<List<UserData.OrderItem>?>
	suspend fun getLikesByUserId(userId: String): Result<List<String>?>
//	suspend fun getUserData(userId: String): Result<UserData?>
	suspend fun getUserDataById(userId: String, onComplete:(UserData?) -> Unit)
	fun getFirebaseAuth(): FirebaseAuth
	fun signInWithPhoneAuthCredential(
		credential: PhoneAuthCredential,
		isUserLoggedIn: MutableLiveData<Boolean>,
		context: Context
	)

	fun isRememberMeOn(): Boolean
}
