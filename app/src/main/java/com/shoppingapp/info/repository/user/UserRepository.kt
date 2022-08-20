package com.shoppingapp.info.repository.user

import android.content.Context
import android.net.Uri
import android.util.Log
import com.airbnb.epoxy.stickyheader.StickyHeaderCallbacks
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.shoppingapp.info.data.User
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.utils.SharePrefManager
import kotlinx.coroutines.*
import java.util.*


class UserRepository(val context: Context, private val remote: RemoteUserRepository) {


    companion object{
        const val TAG = "UserRepository"
    }

    private val sharePref = SharePrefManager(context)
//    private val userId = sharePref.loadUser().userId
    val isUserSeller = sharePref.isUserSeller()
    val isRem = sharePref.isRememberMeOn()
//    val userType = sharePref.getUserType()


    suspend fun signWithEmailAndPassword(email: String,
                                         password: String) =
        remote.signWithEmailAndPassword(email, password)




    suspend fun login(email: String, password: String,isRemOn: Boolean): Result<Boolean> {
        return supervisorScope {
            try {
                val signOutPref = async { sharePref.signOut() }
                val loginTask = async {
                    val userId = remote.signWithEmailAndPassword(email,password)?.user!!.uid
                    val user = remote.getUserById(userId)!!
                    createUserLogging(user,isRemOn)
                }
                Log.d(TAG,"sign out pref start: ${Calendar.getInstance().time.seconds}")
                signOutPref.await()
                Log.d(TAG,"sign out pref end: ${Calendar.getInstance().time.seconds}")
                Log.d(TAG,"login task start: ${Calendar.getInstance().time.seconds}")
                loginTask.await()
                Log.d(TAG,"login task end: ${Calendar.getInstance().time.seconds}")
                Result.Success(true)
            }catch (ex: FirebaseAuthInvalidUserException) {
                val message = "user is not exist"
                Log.d(TAG, message)
                Result.Error(Exception(message))
            }catch (ex: FirebaseAuthInvalidCredentialsException) {
                val message = "incorrect password"
                Log.d(TAG, message)
                Result.Error(Exception(message))

            }catch (ex: FirebaseNetworkException) {
                val message = "network connection required"
                Log.d(TAG, message)
                Result.Error(Exception(message))

            }
        }
    }


    suspend fun loadUsers(): Result<List<User>> {
        return supervisorScope {
            try {
                val adminId = "rB2sqfqafrZnBCB2wcszcnvi44D3"
                val task = async { remote.getUsers().filter { it.userId != adminId } }
                Result.Success(task.await())
            }catch (ex: Exception){
                Result.Error(Exception(ex.message))
            }
        }
    }


    suspend fun createUserAccount(user: User, onSuccess: (Boolean) -> Unit, onError: (String) -> Unit) =
        remote.createUserAccount(user,onSuccess, onError)


    suspend fun signOut() {
        return supervisorScope {
            val sharePrefSignOut = async {  sharePref.signOut() }
            val remoteSignOut = async {   remote.signOut() }
            try {
                sharePrefSignOut.await()
                remoteSignOut.await()
            }catch (ex: Exception){}
        }


    }

    fun createUserLogging(user: User,isRemOn: Boolean) = sharePref.saveUser(user,isRemOn)

//    fun createUserLogging(id: String, isRemOn: Boolean,userType: String, isSeller: Boolean) = sharePref.createLoginSession(id, isRemOn, userType,isSeller)

    fun isUserLogged() = remote.isUserLogged()

    suspend fun uploadFile(uri: Uri, fileName: String) = remote.uploadFile(uri,fileName)

    suspend fun getOrders(userId: String) = remote.getOrders(userId)

    suspend fun insertOrder(order: User.OrderItem, userId: String) = remote.insertOrder(order, userId)

    suspend fun deleteOrder(order: User.OrderItem) = remote.deleteOrder(order)

    suspend fun checkUserIsExist(email: String, isExist:(Boolean) -> Unit, onError:(String)-> Unit ) = remote.checkUserIsExist(email,isExist,onError )

    suspend fun updateUser(user: User): Result<Boolean> {
        return supervisorScope {
            val task = async { remote.updateUser(user) }
            try {
                task.await()
                Result.Success(true)
            }catch (ex: Exception){
                Result.Error(Exception(ex))
            }
        }
    }


    suspend fun deleteUser(userId: String) = remote.deleteUser(userId)

    suspend fun getUser(userId: String) = remote.getUserById(userId)

    suspend fun getUserById(userId: String) = remote.getUserById(userId)

    suspend fun getOrdersByUserId(userId: String) = remote.getOrdersByUserId(userId)

    suspend fun likeProduct(productId: String,userId: String) = remote.likeProduct(productId, userId)

    suspend fun dislikeProduct(productId: String,userId: String) = remote.dislikeProduct(productId, userId)

    suspend fun insertCartItem(newItem: User.CartItem,userId: String) = remote.insertCartItem(newItem, userId)

    suspend fun removeCartItem(itemId: String, userId: String) = remote.removeCartItem(itemId, userId)

    suspend fun updateCartItem(item: User.CartItem, userId: String) = remote.updateCartItem(item, userId)


    suspend fun placeOrder(newOrder: User.OrderItem,userId: String) = remote.placeOrder(newOrder, userId)

    // update both customer and owner order status
    suspend fun updateOrderStatus(orderId: String, status: String,userId: String) = remote.setStatusOfOrderByUserId(orderId, userId, status)


}





//
//import android.content.Context
//import android.util.Log
//import android.widget.Toast
//import androidx.lifecycle.MutableLiveData
//import com.google.firebase.auth.PhoneAuthCredential
//import com.shoppingapp.info.data.Product
//import com.shoppingapp.info.data.User
//import com.shoppingapp.info.utils.SharePrefManager
//import com.shoppingapp.info.utils.UserType
//import kotlinx.coroutines.async
//import kotlinx.coroutines.supervisorScope
//import com.shoppingapp.info.utils.Result
//
//class UserRepository(
//	val context: Context,
//	val remoteUserRepository: RemoteUserRepository) {
//
//	companion object {
//		private const val TAG = "UserRepository"
//	}
//
//	private var sharePrefManager = SharePrefManager(context)
//	private val userId = sharePrefManager.getUserIdFromSession()!!
//
//
//	suspend fun updateUser(user: User) {
//
//	}
//
////	suspend fun refreshOrders(): Result<Boolean> {
////		return supervisorScope {
////			val task = async {
////				val updateOrders = remoteUserRepository.getOrdersFromRemote(userId)
////				val user = localUserRepository.getUser(userId)
////				user?.orders = updateOrders!!
////				localUserRepository.addUser(user!!)
////			}
////			try {
////				task.await()
////			    Result.Success(true)
////			}catch (ex: Exception){
////				Result.Error(ex)
////			}
////		}
////	}
//
//
////	suspend fun refreshProductLikes(products: List<Product>){
////		val oldLikes = remoteUserRepository.getStuckLikes(userId,products)
////		deleteAllUserLikes(oldLikes)
////		Log.d(TAG,"diff likes = ${oldLikes.size}")
////	}
//
////	suspend fun refreshCartItems(products: List<Product>) {
////		val oldCartItems = remoteUserRepository.getStuckCartItems(userId,products)
////		Log.d(TAG,"diff cart items = ${oldCartItems.size}")
////		deleteAllCartItems(oldCartItems)
////	}
//
////	suspend fun refreshOrderItems(orders: List<User.OrderItem>){
////		val oldOrders = remoteUserRepository.getStuckOrdersIds(userId,orders)
////		Log.d(TAG,"diff cart items = ${oldOrders.size}")
////		deleteAllOrders(orders)
////	}
//
//	fun getUserId() = sharePrefManager.getUserIdFromSession()
//
//	fun isRememberMeOn() = sharePrefManager.isRememberMeOn()
//
//	// TODO: 4/19/2022 add the result
//	suspend fun signUp(user: User) {
//		val isSeller = user.userType == UserType.SELLER.name
//		sharePrefManager.createLoginSession(
//			user.userId,
//			user.name,
//			user.phone,
//			false,
//			isSeller
//		)
//		Log.d(TAG, "on SignUp: Updating user in Local Source")
////		localUserRepository.addUser(user)
//		Log.d(TAG, "on SignUp: Updating userdata on Remote Source")
//		remoteUserRepository.addUser(user)
////
//	}
//
//	suspend fun login(user: User, rememberMe: Boolean) {
//		val isSeller = user.userType == UserType.SELLER.name
//		sharePrefManager.createLoginSession(
//			user.userId,
//			user.name,
//			user.phone,
//			rememberMe,
//			isSeller
//		)
//
////		localUserRepository.addUser(user)
//	}
//
//
////	fun observeLocalUser() = localUserRepository.observeUser(userId)
//
//
//	suspend fun deleteAllUserLikes(likes: List<String>){
//		likes.forEach {productId -> removeProductFromLikes(productId)}
//	}
//
//	suspend fun deleteAllCartItems(cartItems: List<String>) {
//		cartItems.forEach { itemId -> deleteCartItem(itemId) }
//	}
//
//	suspend fun deleteAllOrders(orders: List<User.OrderItem>) {
//		orders.forEach { order ->  deleteOrder(order) }
//	}
//
//
//
//	suspend fun checkLogin(mobile: String, password: String): User? {
//		Log.d(TAG, "on Login: checking mobile and password")
//		var queryResult = mutableListOf<User>()
//		try {
//			queryResult = remoteUserRepository.getUserByMobileAndPassword(mobile, password)
//		} catch (e: Exception) {
//			// No Handling
//		}
//		return if (queryResult.size > 0) {
//			queryResult[0]
//		} else {
//			null
//		}
//	}
//
////	fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential,
////									  isUserLoggedIn: MutableLiveData<Boolean>, ){
////		remoteUserRepository.signInWithPhoneAuthCredential(credential, isUserLoggedIn)
////	}
//
//	// TODO: 4/19/2022 also remove all data from data base
//	suspend fun signOut(): Result<Boolean> {
//	 	return supervisorScope {
//	 		val signOutSharPre = async { sharePrefManager.signOut() }
//			val fireBaseSignOut = async { remoteUserRepository.signOut() }
//			try {
//				signOutSharPre.await()
//				fireBaseSignOut.await()
//				Result.Success(true)
//
//			}catch (ex: Exception){
//				Result.Error(ex)
//			}
//		}
//	}
//
//	fun isUserSeller() = sharePrefManager.isUserSeller()
//
//	suspend fun deleteUser() : Result<Boolean> {
//		return supervisorScope {
////			val localRes = async {localUserRepository.deleteUser(userId) }
//			val remoteRes = async { remoteUserRepository.deleteUser(userId) }
//			try {
////			    localRes.await()
//				remoteRes.await()
//				Result.Success(true)
//			}catch (ex: Exception){
//				Result.Error(ex)
//			}
//		}
//	}
//
////	suspend fun hardRefreshUserData(): Result<Boolean> {
////		return supervisorScope {
//////			val localRes = async { 	localUserRepository.deleteUser(userId) }
////			val remoteRes = async {
////				val user = remoteUserRepository.getUserById(userId)
//////				localUserRepository.addUser(user)
////			}
////			try {
//////				localRes.await()
////				remoteRes.await()
////			    Result.Success(true)
////			}catch (ex: Exception){
////				Result.Error(ex)
////			}
////		}
////	}
//
//	suspend fun insertProductToLikes(productId: String): Result<Boolean> {
//		return supervisorScope {
//			val remoteRes = async {
//				Log.d(TAG, "onLikeProduct: adding product to remote source")
//				remoteUserRepository.likeProduct(productId, userId)
//			}
////			val localRes = async {
////				Log.d(TAG, "onLikeProduct: updating product to local source")
////				localUserRepository.likeProduct(productId, userId)
////			}
//			try {
////				localRes.await()
//				remoteRes.await()
//				Result.Success(true)
//			} catch (e: Exception) {
//				Result.Error(e)
//			}
//		}
//	}
//
//	suspend fun removeProductFromLikes(productId: String): Result<Boolean> {
//		return supervisorScope {
//			val remoteRes = async {
//				Log.d(TAG, "onDislikeProduct: deleting product from remote source")
//				remoteUserRepository.dislikeProduct(productId, userId)
//			}
////			val localRes = async {
////				Log.d(TAG, "onDislikeProduct: updating product to local source")
////				localUserRepository.dislikeProduct(productId, userId)
////			}
//			try {
////				localRes.await()
//				remoteRes.await()
//				Result.Success(true)
//			} catch (e: Exception) {
//				Result.Error(e)
//			}
//		}
//	}
//
//	suspend fun insertCartItemByUserId(cartItem: User.CartItem): Result<Boolean> {
//		Log.d(TAG,"inserting item in cart ...")
//		return supervisorScope {
//			val localRes = async {
//				Log.d(TAG, "onInsertCartItem: adding item to remote source")
//				remoteUserRepository.insertCartItem(cartItem, userId)
//			}
////			val remoteRes = async {
////				Log.d(TAG, "onInsertCartItem: updating item to local source")
////				localUserRepository.insertCartItem(cartItem, userId)
////			}
//			try {
//				localRes.await()
////				remoteRes.await()
//				Result.Success(true)
//			} catch (e: Exception) {
//				Result.Error(e)
//			}
//
//		}
//
//	}
//
//
//
////	suspend fun isItemInCart(item: User.CartItem): Boolean {
////		return supervisorScope {
////			val cart  = getUser()!!.cart
////			return@supervisorScope cart.contains(item)
////		}
////	}
//
//	suspend fun updateCartItemByUserId(cartItem: User.CartItem): Result<Boolean> {
//		return supervisorScope {
//			val remoteRes = async {
//				Log.d(TAG, "onUpdateCartItem: updating cart item on remote source")
//				remoteUserRepository.updateCartItem(cartItem, userId)
//			}
////			val localRes = async {
////				Log.d(TAG, "onUpdateCartItem: updating cart item on local source")
////				localUserRepository.updateCartItem(cartItem, userId)
////			}
//			try {
////				localRes.await()
//				remoteRes.await()
//				Result.Success(true)
//			} catch (e: Exception) {
//				Result.Error(e)
//			}
//
//		}
//	}
//
//
//	suspend fun insertOrder(order: User.OrderItem): Result<Boolean> {
//
//		return supervisorScope {
//			val remoteRes = async {
//				Log.d(TAG, "onInsert: inserting order item from remote source")
//				remoteUserRepository.insertOrder(order)
//			}
//
////			val localRes = async {
////				Log.d(TAG, "onInsert: inserting order item from local source")
////				localUserRepository.insertOrder(order)
////			}
//
//			try {
//				remoteRes.await()
////				localRes.await()
//				Result.Success(true)
//			}catch (ex: Exception){
//				Result.Error(ex)
//			}
//		}
//
//
//	}
//
//
//
//	suspend fun deleteOrder(order: User.OrderItem): Result<Boolean> {
//
//		return supervisorScope {
//			val remoteRes = async {
//				Log.d(TAG, "onDelete: deleting order item from remote source")
//				remoteUserRepository.deleteOrder(order)
//			}
//
////			val localRes = async {
////				Log.d(TAG, "onDelete: deleting order item from remote source")
////				localUserRepository.deleteOrder(order)
////			}
//
//			try {
//			    remoteRes.await()
////				localRes.await()
//				Result.Success(true)
//			}catch (ex: Exception){
//				Result.Error(ex)
//			}
//		}
//
//
//	}
//
//	suspend fun deleteCartItem(itemId: String): Result<Boolean> {
//		return supervisorScope {
//			val remoteRes = async {
//				Log.d(TAG, "onDelete: deleting cart item from remote source")
//				remoteUserRepository.deleteCartItem(itemId, userId)
//			}
////			val localRes = async {
////				Log.d(TAG, "onDelete: deleting cart item from local source")
////				localUserRepository.deleteCartItem(itemId, userId)
////			}
//			try {
////				localRes.await()
//				remoteRes.await()
//				Result.Success(true)
//			} catch (e: Exception) {
//				Result.Error(e)
//			}
//		}
//	}
//
////	suspend fun placeOrder(newOrder: User.OrderItem): Result<Boolean> {
////		return supervisorScope {
////			val remoteRes = async {
////				Log.d(TAG, "onPlaceOrder: adding item to remote source")
////				remoteUserRepository.placeOrder(newOrder, userId)
////			}
////			val localRes = async {
////				Log.d(TAG, "onPlaceOrder: adding item to local source")
////				remoteUserRepository.getUserById(userId){ user ->
////					async {
////						localUserRepository.deleteUser(user!!.userId)
////					}
////					async {
////						if (user != null) {
////							localUserRepository.addUser(user)
////						}
////					}
////				}
////			}
////			try {
////				remoteRes.await()
////				localRes.await()
////				Result.Success(true)
////			} catch (e: Exception) {
////				Result.Error(e)
////			}
////		}
////	}
//
//	suspend fun setStatusOfOrder(orderId: String, userId: String, status: String): Result<Boolean> {
//		return supervisorScope {
//			val remoteRes = async {
//				Log.d(TAG, "onSetStatus: updating status on remote source")
//				remoteUserRepository.setStatusOfOrderByUserId(orderId, userId, status)
//			}
////			val localRes = async {
////				Log.d(TAG, "onSetStatus: updating status on local source")
////				localUserRepository.setStatusOfOrderByUserId(orderId, userId, status)
////			}
//			try {
////				localRes.await()
//				remoteRes.await()
//				Result.Success(true)
//			} catch (e: Exception) {
//				Result.Error(e)
//			}
//		}
//	}
//
////	suspend fun getOrders() = localUserRepository.getOrdersByUserId(userId)
////
////	suspend fun getLikesByUserId() = localUserRepository.getLikesByUserId(userId)
////
////	suspend fun getUser() = localUserRepository.getUserById(userId)
//
//	private fun makeErrToast(text: String, context: Context) {
//		Toast.makeText(context, text, Toast.LENGTH_LONG).show()
//	}
//
//
//	//  suspend fun getAddressesByUserId(userId: String): Result<List<UserData.Address>?> {
////		return userLocalDataSource.getAddressesByUserId(userId)
////	}
//
//
//}