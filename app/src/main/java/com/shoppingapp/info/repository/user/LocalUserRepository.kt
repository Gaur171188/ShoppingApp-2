package com.shoppingapp.info.repository.user

import android.util.Log
import com.shoppingapp.info.data.User
import com.shoppingapp.info.local.api.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.shoppingapp.info.utils.Result

// this class to fitch the data from local database UserLocalDataSource

class LocalUserRepository(private val userApi: UserApi) {

	private val ioDispatcher = Dispatchers.IO

	suspend fun addUser(user: User) {
		withContext(ioDispatcher) {
			userApi.insert(user)
		}
	}

	suspend fun deleteUser(userId: String) {
		withContext(ioDispatcher){
			userApi.deleteUserById(userId)
		}
	}

	suspend fun getUser(userId: String) = userApi.getUserById(userId)

	suspend fun getUserById(userId: String): User? = withContext(ioDispatcher) {
		try {
			val user = userApi.getUserById(userId)
			if (user != null) return@withContext user
			else return@withContext null
		}catch (ex: Exception){
			return@withContext null
		}
	}

	suspend fun checkUserIsExist(email: String, isExist: (Boolean) -> Unit, onError: (String) -> Unit) {

	}

	suspend fun checkPassByUserId(userId: String, password: String, onComplete: (Boolean) -> Unit) {}

	suspend fun getUserByPhone(phoneNumber: String): User? = withContext(ioDispatcher) {
			try {
				val uData = userApi.getUserByMobile(phoneNumber)
				if (uData != null) {
					return@withContext uData
				} else {
					return@withContext null
				}
			} catch (e: Exception) {
				Log.d("UserLocalSource", "onGetUser: Error Occurred, $e")
				return@withContext null
			}
		}

	suspend fun getOrdersByUserId(userId: String): Result<List<User.OrderItem>?> = withContext(ioDispatcher) {
			try {
				val uData = userApi.getUserById(userId)
				if (uData != null) {
					val ordersList = uData.orders
					return@withContext Result.Success(ordersList)
				} else {
					return@withContext Result.Error(Exception("User Not Found"))
				}

			} catch (e: Exception) {
				Log.d("UserLocalSource", "onGetOrders: Error Occurred, ${e.message}")
				return@withContext Result.Error(e)
			}
		}

	suspend fun getLikesByUserId(userId: String): Result<List<String>?> = withContext(ioDispatcher) {
			try {
				val uData = userApi.getUserById(userId)
				if (uData != null) {
					val likesList = uData.likes
					return@withContext Result.Success(likesList)
				} else {
					return@withContext Result.Error(Exception("User Not Found"))
				}

			} catch (e: Exception) {
				Log.d("UserLocalSource", "onGetLikes: Error Occurred, ${e.message}")
				return@withContext Result.Error(e)
			}
		}

	suspend fun dislikeProduct(productId: String, userId: String) = withContext(ioDispatcher) {
			try {
				val uData = userApi.getUserById(userId)
				if (uData != null) {
					val likesList = uData.likes.toMutableList()
					likesList.remove(productId)
					uData.likes = likesList
					userApi.updateUser(uData)
				} else {
					throw Exception("User Not Found")
				}
			} catch (e: Exception) {
				Log.d("UserLocalSource", "onGetLikes: Error Occurred, ${e.message}")
				throw e
			}
		}

	suspend fun likeProduct(productId: String, userId: String) = withContext(ioDispatcher) {
			try {
				val uData = userApi.getUserById(userId)
				if (uData != null) {
					val likesList = uData.likes.toMutableList()
					likesList.add(productId)
					uData.likes = likesList
					userApi.updateUser(uData)
				} else {
					throw Exception("User Not Found")
				}
			} catch (e: Exception) {
				Log.d("UserLocalSource", "onGetLikes: Error Occurred, ${e.message}")
				throw e
			}
		}

	suspend fun insertCartItem(newItem: User.CartItem, userId: String) = withContext(ioDispatcher) {
			try {
				val uData = userApi.getUserById(userId)
				if (uData != null) {
					val cartItems = uData.cart.toMutableList()
					cartItems.add(newItem)
					uData.cart = cartItems
					userApi.updateUser(uData)
				} else {
					throw Exception("User Not Found")
				}
			} catch (e: Exception) {
				Log.d("UserLocalSource", "onInsertCartItem: Error Occurred, ${e.message}")
				throw e
			}
		}

	suspend fun updateCartItem(item: User.CartItem, userId: String) = withContext(ioDispatcher) {
			try {
				val uData = userApi.getUserById(userId)
				if (uData != null) {
					val cartItems = uData.cart.toMutableList()
					val pos = cartItems.indexOfFirst { it.itemId == item.itemId }
					if (pos >= 0) {
						cartItems[pos] = item
					}
					uData.cart = cartItems
					userApi.updateUser(uData)
				} else {
					throw Exception("User Not Found")
				}
			} catch (e: Exception) {
				Log.d("UserLocalSource", "onInsertCartItem: Error Occurred, ${e.message}")
				throw e
			}
		}

	suspend fun deleteCartItem(itemId: String, userId: String) = withContext(ioDispatcher) {
			try {
				val uData = userApi.getUserById(userId)
				if (uData != null) {
					val cartItems = uData.cart.toMutableList()
					val pos = cartItems.indexOfFirst { it.itemId == itemId }
					if (pos >= 0) {
						cartItems.removeAt(pos)
					}
					uData.cart = cartItems
					userApi.updateUser(uData)
				} else {
					throw Exception("User Not Found")
				}
			} catch (e: Exception) {
				Log.d("UserLocalSource", "onInsertCartItem: Error Occurred, ${e.message}")
				throw e
			}
		}

	suspend fun setStatusOfOrderByUserId(orderId: String, userId: String, status: String) = withContext(ioDispatcher) {
			try {
				val uData = userApi.getUserById(userId)
				if (uData != null) {
					val orders = uData.orders.toMutableList()
					val pos = orders.indexOfFirst { it.orderId == orderId }
					if (pos >= 0) {
						orders[pos].status = status
						val custId = orders[pos].customerId
						val custData = userApi.getUserById(custId)
						if (custData != null) {
							val orderList = custData.orders.toMutableList()
							val idx = orderList.indexOfFirst { it.orderId == orderId }
							if (idx >= 0) {
								orderList[idx].status = status
							}
							custData.orders = orderList
							userApi.updateUser(custData)
						}
					}
					uData.orders = orders
					userApi.updateUser(uData)
				} else {
					throw Exception("User Not Found")
				}
			} catch (e: Exception) {
				Log.d("UserLocalSource", "onInsertCartItem: Error Occurred, ${e.message}")
				throw e
			}
		}


	//    suspend fun getAddressesByUserId(userId: String): Result<List<UserData.Address>?> =
//		withContext(ioDispatcher) {
//			try {
//				val uData = userDao.getById(userId)
//				if (uData != null) {
//					val addressList = uData.addresses
//					return@withContext Success(addressList)
//				} else {
//					return@withContext Error(Exception("User Not Found"))
//				}
//
//			} catch (e: Exception) {
//				Log.d("UserLocalSource", "onGetAddress: Error Occurred, ${e.message}")
//				return@withContext Error(e)
//			}
//		}


}