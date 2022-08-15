package com.shoppingapp.info.repository.user

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.product.RemoteProductRepository
import com.shoppingapp.info.utils.SharePrefManager
import com.shoppingapp.info.utils.UserType
import com.shoppingapp.info.utils.showMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.IndexOutOfBoundsException

@OptIn(DelicateCoroutinesApi::class)
class RemoteUserRepository() {

    private val fireStore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private fun usersPath() = fireStore.collection(USERS_COLLECTION)




//    private suspend fun signInWithEmailAndPassword(
//        email: String,
//        password: String,
//        onSuccess: (Boolean) -> Unit,
//        onError: (String) -> Unit
//    ) {
//        return supervisorScope {
//            val task = async {
//                auth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener {
//                        if (it.isSuccessful){
//                            if (it.result.user!!.isEmailVerified){
//                                Log.d(TAG,"Login Success: ${it.result}")
//                                onSuccess(true)
//
//                            }else{
//                                val error = "email is not verified!"
//                                Log.d(TAG,"Login Error: $error")
//                                onError(error)
//                            }
//                        }
//                    }
//                    .addOnFailureListener {
//                        Log.d(TAG,"Login Error: ${it.message}")
//                        onError(it.message.toString())
//                    }
//            }
//            try {
//                task.await()
//            }catch (ex: Exception){
//                Log.d(TAG,"Login Error: ${ex.message}")
//                onError(ex.message.toString())
//            }
//        }
//    }
//
//
//
//    suspend fun signIn(email: String,
//                       password: String,
//                       onSuccess:(Boolean)-> Unit,
//                       onError:(String)-> Unit) =
//        signInWithEmailAndPassword(email,password,onSuccess, onError)


    fun isUserLogged() = auth.currentUser != null


//    suspend fun signWithEmailAndPassword(context: Context,email: String, password: String,isRemOn:Boolean,onSuccess: (Boolean) -> Unit,onError: (String) -> Unit) {
//       auth.signInWithEmailAndPassword(email, password)
//           .addOnSuccessListener { authResult ->
//               if (authResult.user?.isEmailVerified!!){
//                   Log.d(TAG,"onSuccess: userId: ${authResult.user!!.uid}")
//                   usersPath().document(authResult.user!!.uid).get().addOnSuccessListener {
//
//                       val user = it.toObject(User::class.java)
//                       val isSeller = user?.userType == UserType.SELLER.name
//
//                       val sharePrefManager = SharePrefManager(context)
//                       sharePrefManager.createLoginSession(
//                           id = authResult.user!!.uid,
//                           isRemOn = isRemOn,
//                           isSeller = isSeller)
//                       onSuccess(true)
//                   }
//               }else{
//                   Log.d(TAG,"onFailed: sign failed due to email is not verified")
//                   onError("email is not verified")
//               }
//           }
//           .addOnFailureListener { e ->
//               Log.d(TAG,"onFailed: sign failed due to ${e.message}")
//               onError(e.message!!)
//           }
//   }

    suspend fun signWithEmailAndPassword(email: String, password: String): AuthResult? {
        return auth.signInWithEmailAndPassword(email, password).await()

    }

//    val isSeller = user?.userType == UserType.SELLER.name
//
//    val sharePrefManager = SharePrefManager(context)
//    sharePrefManager.createLoginSession(
//    id = authResult.user!!.uid,
//    isRemOn = isRemOn,
//    isSeller = isSeller)


    suspend fun createUserAccount(user: User,onSuccess: (Boolean) -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener {
                val userId = it.user?.uid
                if (userId != null) {
                    user.userId = userId
                    it.user?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Log.d(TAG,"Verification Success: email verification has been sent")

                            addUser(user).addOnSuccessListener {
                                Log.d(TAG,"Adding User Success: user has been added")
                                onSuccess(true)
                            }
                        }
                        ?.addOnFailureListener { e ->
                            onError(e.message!!)
                            Log.d(TAG,"Verification Failed: due to ${e.message}")
                        }
                }
            }
    }


    fun signOut() = auth.signOut()

    suspend fun uploadFile(uri: Uri, fileName: String): Uri? {
        val imgRef = storageRef.child("${IMAGE_PROFILE}/$fileName")
        val uploadTask = imgRef.putFile(uri)
        val uriRef = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imgRef.downloadUrl
        }
        return uriRef.await()
    }


    suspend fun getOrders (userId: String): List<User.OrderItem>? {
            val ref = usersPath().document(userId).get().await()
            val orders = if (ref != null) {
                val user = ref.toObject(User::class.java)
                return user?.orders
            } else { null }
        return orders
    }




     suspend fun insertOrder(order: User.OrderItem, userId: String): Task<Void> {
        // owner
        val ownerId = order.items[0].ownerId
        return usersPath().document(ownerId)
            .update(ORDERS_FIELD, FieldValue.arrayUnion(order))
            .addOnSuccessListener {
                // remove cart items from customer after send the order to owner
                order.items.forEach { item->
                    GlobalScope.launch(Dispatchers.IO) {
                        removeCartItem(item.itemId,userId).await()
                    }
                }

//                // customer
//                usersPath().document(ownerId)
//                    .update(ORDERS_FIELD, FieldValue.arrayUnion(order))
            }
    }


     suspend fun deleteOrder(order: User.OrderItem): Task<Void> {
        // owner
        val ownerId = order.items[0].ownerId
        return  usersPath().document(ownerId)
            .update(ORDERS_FIELD, FieldValue.arrayRemove(order))
    }



    suspend fun checkUserIsExist(email: String, isExist:(Boolean) -> Unit, onError:(String) -> Unit) {
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).addOnCompleteListener {
                try {
                    val isUserExist = it.result.signInMethods?.isEmpty()
                    if (isUserExist!!){ // user is not exist
                        isExist(false)
                    }else{ // user is exist
                        isExist(true)
                    }
                }
                catch (ex: Exception){ // maybe there is another error..
//                    onError(context.resources.getString(R.string.no_connection))
                }
            }
    }



    suspend fun updateUser(user: User) = usersPath().document(user.userId).update(user.toHashMap())


    private fun addUser(user: User) = usersPath().document(user.userId).set(user)

    suspend fun deleteUser(userId: String) {

        // if you want to delete user you must remove all data too, (products ,orders)
        usersPath().document(userId).delete().await()
    }

    suspend fun getUserById(userId: String) = usersPath().document(userId).get().await().toObject(User::class.java)

    suspend fun getOrdersByUserId(userId: String): Result<List<User.OrderItem>?> {
        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
        return if (!userRef.isEmpty) {
            val userData = userRef.documents[0].toObject(User::class.java)
            Result.Success(userData!!.orders)
        } else {
            Result.Error(Exception("User Not Found!"))
        }
    }


//    // return list of product id
//    suspend fun getStuckLikes(userId: String, products: List<Product>): List<String> {
//        var diff: List<String> = emptyList()
//            val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
//             if (!userRef.isEmpty){
//                val userData = userRef.documents[0].toObject(User::class.java)
//                val likes = userData?.likes
//                val productsId = products.map { it.productId }
//                diff = likes?.minus(productsId.toSet())!!
//
//            }
//        return diff
//    }


//    // return list of product id
//    suspend fun getStuckCartItems (userId: String,products: List<Product>): List<String> {
//        var diff: List<String> = emptyList()
//        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
//        if (!userRef.isEmpty){
//            val userData = userRef.documents[0].toObject(User::class.java)
//            val cart = userData?.cart?.map { it.productId }
//            val productsId = products.map { it.productId }
//            diff = cart?.minus(productsId.toSet())!!
//        }
//        return diff
//    }


//    // return list of orders id
//    suspend fun getStuckOrdersIds (userId: String,orders: List<User.OrderItem>): List<String> {
//        var diff: List<String> = emptyList()
//        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
//        if (!userRef.isEmpty){
//            val userData = userRef.documents[0].toObject(User::class.java)
//            val order = userData?.orders?.map { it.orderId }
//            val orderId = orders.map { it.orderId }
//            diff = order?.minus(orderId.toSet())!!
//        }
//        return diff
//    }



    suspend fun likeProduct(productId: String, userId: String): Task<Void> {
        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
        val docId = userRef.documents[0].id
        return usersPath().document(docId)
            .update(LIKES_FIELD, FieldValue.arrayUnion(productId))
    }


    suspend fun dislikeProduct(productId: String, userId: String): Task<Void> {
        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
        val docId = userRef.documents[0].id
        return usersPath().document(docId)
            .update(LIKES_FIELD, FieldValue.arrayRemove(productId))
    }


//     suspend fun insertCartItem(newItem: User.CartItem, userId: String): Task<Void> {
//        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
//        val docId = userRef.documents[0].id
//        return usersPath().document(docId)
//            .update(CART_FIELD, FieldValue.arrayUnion(newItem.toHashMap()))
//    }

    suspend fun insertCartItem(newItem: User.CartItem, userId: String): Task<Void> {
       return usersPath().document(userId).update(CART_FIELD, FieldValue.arrayUnion(newItem.toHashMap()))
//        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
//        val docId = userRef.documents[0].id
//        return usersPath().document(docId)
//            .update(CART_FIELD, FieldValue.arrayUnion(newItem.toHashMap()))
    }


    suspend fun removeCartItem(itemId: String, userId: String): Task<Void> {
        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()

        val docId = userRef.documents[0].id
        val oldCart = userRef.documents[0].toObject(User::class.java)?.cart?.toMutableList()
        val idx = oldCart?.indexOfFirst { it.itemId == itemId } ?: -1
        if (idx != -1) {
            oldCart?.removeAt(idx)
        }
        return usersPath().document(docId)
            .update(CART_FIELD, oldCart?.map { it.toHashMap() })

    }

    suspend fun updateCartItem(item: User.CartItem, userId: String): Task<Void> {
        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()

            val docId = userRef.documents[0].id
            val oldCart = userRef.documents[0].toObject(User::class.java)?.cart?.toMutableList()
            val idx = oldCart?.indexOfFirst { it.itemId == item.itemId } ?: -1
            if (idx != -1) {
                oldCart?.set(idx, item)
            }
           return usersPath().document(docId)
                .update(CART_FIELD, oldCart?.map { it.toHashMap() })

    }





    suspend fun placeOrder(newOrder: User.OrderItem, userId: String) {
        // add order to customer and
        // specific items to their owners
        // empty customers cart
        val ownerProducts: MutableMap<String, MutableList<User.CartItem>> = mutableMapOf()
        for (item in newOrder.items) {
            if (!ownerProducts.containsKey(item.ownerId)) {
                ownerProducts[item.ownerId] = mutableListOf()
            }
            ownerProducts[item.ownerId]?.add(item)
        }
        ownerProducts.forEach { (ownerId, items) ->
            run {
                val itemPrices = mutableMapOf<String, Double>()
                items.forEach { item ->
                    itemPrices[item.itemId] = newOrder.itemsPrices[item.itemId] ?: 0.0
                }
                val ownerOrder = User.OrderItem(
                    newOrder.orderId,
                    userId,
                    items,
                    itemPrices,
                    newOrder.shippingCharges,
                    newOrder.paymentMethod,
                    newOrder.address,
                    newOrder.orderDate,
                    newOrder.status)

                val ownerRef = usersPath().whereEqualTo(USER_ID_FIELD, ownerId).get().await()
                if (!ownerRef.isEmpty) {
                    val docId = ownerRef.documents[0].id
                    usersPath().document(docId)
                        .update(ORDERS_FIELD, FieldValue.arrayUnion(ownerOrder.toHashMap()))
                }
            }
        }

        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
        if (!userRef.isEmpty) {
            val docId = userRef.documents[0].id
            usersPath().document(docId)
                .update(ORDERS_FIELD, FieldValue.arrayUnion(newOrder.toHashMap()))
            usersPath().document(docId)
                .update(CART_FIELD, ArrayList<User.CartItem>())
        }
    }



    suspend fun setStatusOfOrderByUserId(orderId: String, userId: String, status: String) {

        val userRef = usersPath().whereEqualTo(USER_ID_FIELD, userId).get().await()
        if (!userRef.isEmpty) {
            val docId = userRef.documents[0].id
            val ordersList =
                userRef.documents[0].toObject(User::class.java)?.orders?.toMutableList()
            val idx = ordersList?.indexOfFirst { it.orderId == orderId } ?: -1
            if (idx != -1) {
                val orderData = ordersList?.get(idx)
                if (orderData != null) {
                    usersPath().document(docId)
                        .update(ORDERS_FIELD, FieldValue.arrayRemove(orderData.toHashMap()))
                    orderData.status = status
                    usersPath().document(docId)
                        .update(ORDERS_FIELD, FieldValue.arrayUnion(orderData.toHashMap()))

                    // updating customer status
                    val custRef =
                        usersPath().whereEqualTo(USER_ID_FIELD, orderData.customerId)
                            .get().await()
                    if (!custRef.isEmpty) {
                        val did = custRef.documents[0].id
                        val orders =
                            custRef.documents[0].toObject(User::class.java)?.orders?.toMutableList()
                        val pos = orders?.indexOfFirst { it.orderId == orderId } ?: -1
                        if (pos != -1) {
                            val order = orders?.get(pos)
                            if (order != null) {
                                usersPath().document(did).update(
                                    ORDERS_FIELD,
                                    FieldValue.arrayRemove(order.toHashMap())
                                )
                                order.status = status
                                usersPath().document(did).update(
                                    ORDERS_FIELD,
                                    FieldValue.arrayUnion(order.toHashMap())
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    companion object {

        private const val TAG = "RemoteUserRepository"

        private const val USERS_COLLECTION = "users"
        private const val USER_ID_FIELD = "userId"
        private const val LIKES_FIELD = "likes"
        private const val EMAIL_FIELD = "email"
        private const val CART_FIELD = "cart"
        private const val ORDERS_FIELD = "orders"
        private const val PHONE_FIELD = "phone"
        private const val PASSWORD_FIELD = "password"
        private const val EMAIL_MOBILE_DOC = "emailAndMobiles"

        private const val IMAGE_PROFILE = "imageProfile"
    }
}