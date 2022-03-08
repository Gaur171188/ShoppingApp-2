package com.shoppingapp.info

import com.shoppingapp.info.data.UserData


interface UserDataSource {


	suspend fun addUser(userData: UserData)
	suspend fun deleteUser()

//	suspend fun getUserById(userId: String): Result<UserData?>

	suspend fun getUserById(userId: String, onComplete: (UserData?) -> Unit)

	suspend fun checkUserIsExist(email: String, isExist:(Boolean) -> Unit, onError:(String) -> Unit)

	suspend fun getUserByMobileAndPassword(
		mobile: String,
		password: String): MutableList<UserData> {
		return mutableListOf()
	}

	suspend fun checkPassByUserId(userId: String, password: String,onComplete: (Boolean) -> Unit)

	suspend fun likeProduct(productId: String, userId: String) {}

	suspend fun dislikeProduct(productId: String, userId: String) {}


	suspend fun insertCartItem(newItem: UserData.CartItem, userId: String) {}

	suspend fun updateCartItem(item: UserData.CartItem, userId: String) {}

	suspend fun deleteCartItem(itemId: String, userId: String) {}

	suspend fun placeOrder(newOrder: UserData.OrderItem, userId: String) {}

	suspend fun setStatusOfOrderByUserId(orderId: String, userId: String, status: String) {}

	suspend fun clearUser(userId: String) {}

	suspend fun getUserByMobile(phoneNumber: String): UserData? {
		return null
	}

	suspend fun getOrdersByUserId(userId: String): Result<List<UserData.OrderItem>?>


	suspend fun getLikesByUserId(userId: String): Result<List<String>?>

}