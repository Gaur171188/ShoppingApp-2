package com.shoppingapp.info.screens.login


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shoppingapp.info.AuthRemoteDataSource
import com.shoppingapp.info.R
import com.shoppingapp.info.screens.registration.RegistrationViewModel
import com.shoppingapp.info.utils.SharePref
import com.shoppingapp.info.utils.StoreDataStatus
import kotlinx.coroutines.*

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application
    val userPref = SharePref(app.applicationContext, SharePref.FILE_USER)

    companion object {
        const val TAG = "Login"
    }

    private val _authRemoteDataSource by lazy {
        AuthRemoteDataSource(app)
    }


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
        scopeIO.launch {
            withContext(Dispatchers.Main){
                _inProgress.value = StoreDataStatus.LOADING
            }
            _authRemoteDataSource.checkUserIsExist(email,
                isExist = { isExist ->
                    if (!isExist) { // user is not exist
                        Log.i(TAG, "user is not exist")
                        setLoginError(app.resources.getString(R.string.user_is_not_exist))
                    } else {// user is exist
                        Log.i(TAG, "user is exist")
                        signWithEmailAndPassword(email, password,isRemOn)
                    }
                },
                onError = { error -> // error network
                    Log.i(RegistrationViewModel.TAG, error)
                    setLoginError(error)
                })
        }
    }



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
                                    _authRemoteDataSource.checkPassByUserId(userId, password){ isTypicalPass ->
                                        if (isTypicalPass){
                                            scopeIO.launch {
                                                withContext(Dispatchers.Main){
                                                    Log.i("Login","password is correct")
                                                    _inProgress.value = StoreDataStatus.DONE
                                                    _errorMessage.value = "password correct"
                                                    userPref.setRememberMe(isRemOn)
                                                    _isLogged.value = true
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
                            _inProgress.value = StoreDataStatus.ERROR
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