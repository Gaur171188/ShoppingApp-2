package com.shoppingapp.info.screens.login


import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shoppingapp.info.R
import com.shoppingapp.info.ShoppingApplication
import com.shoppingapp.info.repository.product.ProductRepository
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.repository.user.UserRepository

import com.shoppingapp.info.utils.SharePrefManager
import com.shoppingapp.info.utils.StoreDataStatus
import com.shoppingapp.info.utils.UserType
import kotlinx.coroutines.*

class LoginViewModel(
    private val remoteUserRepository: RemoteUserRepository,
    private val sharePrefManager: SharePrefManager,
    private val productRepository: ProductRepository): ViewModel() {

//    private val app = application
////    val userPref = SharePref(app.applicationContext, SharePref.FILE_USER)
//    private val appSessionManager = SharePrefManager(application.applicationContext)

    companion object {
        const val TAG = "Login"
    }

//    private val shopApp = ShoppingApplication(application.applicationContext)
//    private val authRepository by lazy{ shopApp.userRepository }


//    private val userRepository by lazy { shopApp.userRepository }

//    private val remoteUserRepository by lazy { RemoteUserRepository() }

//    private val _userLocalDataSource by lazy {
//        UserLocalDataSource(authRepository.in)
//    }


    //    private val userRepositoryOnline = UserRepositoryOnline()
    private val scopeIO = CoroutineScope(Dispatchers.IO + Job())

    private var e: String = ""

    /** live data **/
    private val _inProgress = MutableLiveData<StoreDataStatus?>()
    val inProgress: LiveData<StoreDataStatus?> = _inProgress


    /** live data **/
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /** live data **/
    private val _isLogged = MutableLiveData<Boolean?>()
    val isLogged: LiveData<Boolean?> = _isLogged

    // firebase fire store
    private val _root = FirebaseFirestore.getInstance()
    private val _usersPath = _root.collection("users")

    // firebase authentication
    private val _auth = FirebaseAuth.getInstance()



    fun initLogin(){
        _isLogged.value = null
        _inProgress.value = null
        _errorMessage.value = null
    }

    fun setLoginError(error: String){
        _errorMessage.value = error
        _inProgress.value = StoreDataStatus.ERROR
    }


    fun login(email: String, password: String, isRemOn: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                _inProgress.value = StoreDataStatus.LOADING
            }
            remoteUserRepository.checkUserIsExist(email,
                isExist = { isExist ->
                    if (!isExist) { // user is not exist
                        Log.i(TAG, "user is not exist")
//                        setLoginError(app.resources.getString(R.string.user_is_not_exist))
                    } else {// user is exist
                        Log.i(TAG, "user is exist")
                        signWithEmailAndPassword(email, password,isRemOn)
                        viewModelScope.launch {
                            productRepository.refreshProducts()
                        }
                    }
                },
                onError = { error -> // error network
                    Log.i(TAG, error)
                    setLoginError(error)
                })
        }
    }



// TODO: enhance the function

    private fun signWithEmailAndPassword(email: String, password: String,isRemOn: Boolean){
        scopeIO.launch {
            _auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isComplete) {
                        val userId = _auth.currentUser?.uid
                        Log.i("Login","user is: $userId")
                        if (userId != null) {
                            scopeIO.launch {
                                if (_auth.currentUser?.isEmailVerified!!) {
                                    remoteUserRepository.checkPassByUserId(userId, password){ isTypicalPass ->
                                        if (isTypicalPass){
                                            viewModelScope.launch {
                                                    remoteUserRepository.getUserById(userId){user ->
                                                        if (user != null){
                                                          viewModelScope.launch {
                                                              withContext(Dispatchers.Main){
//                                                                  authRepository.login(user,isRemOn)
                                                                  val isSeller = user.userType == UserType.SELLER.name
                                                                  sharePrefManager.createLoginSession(user.userId,user.name,user.phone,isRemOn,isSeller)
                                                                  _inProgress.value = StoreDataStatus.DONE
                                                                  _isLogged.value = true
                                                              }
                                                          }
                                                        }else{ // user is not found

                                                        }
                                                    }
                                            }
                                        }else{
                                           scopeIO.launch {
                                               withContext(Dispatchers.Main){
                                                   Log.i("Login","password is not correct")
                                                   setLoginError("password is not correct")
                                               }
                                           }

                                        }

                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        setLoginError("email is not verify!")
                                        Log.i(TAG, "email is not verify!")
                                    }
                                }
                            }
                        } else {
                            val error = task.exception!!.message.toString()
                            setLoginError("password is not correct!")
                            Log.i(TAG, error)
                        }

                    } else {
                        val error = task.exception!!.message.toString()
                        _inProgress.value = StoreDataStatus.ERROR
                        Log.i(TAG, error)
                    }
                }

        }
    }



    override fun onCleared() {
        super.onCleared()



        scopeIO.cancel()
    }


}