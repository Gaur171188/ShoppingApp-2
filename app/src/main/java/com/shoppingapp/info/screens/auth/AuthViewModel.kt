package com.shoppingapp.info.screens.auth



import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.utils.DataStatus
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.utils.UserType
import kotlinx.coroutines.*

class AuthViewModel (val userRepo: UserRepository): ViewModel() {

    companion object {
        const val TAG = "LoginViewModel"
    }

//    private val userRepo = RemoteUserRepository()
//
    val isUserLogged = userRepo.isUserLogged()
    val isRem = userRepo.isRem
//    val userType = userRepo.userType


    private val _loggingStatus = MutableLiveData<DataStatus?>()
    val loggingStatus: LiveData<DataStatus?> = _loggingStatus


    private val _registerStatus = MutableLiveData<DataStatus?>()
    val registerStatus: LiveData<DataStatus?> = _registerStatus


    val errorMessage = MutableLiveData<String?>()

    private val _isLogged = MutableLiveData<Boolean?>()
    val isLogged: LiveData<Boolean?> = _isLogged

    private val _isRegister = MutableLiveData<Boolean?>()
    val isRegister: LiveData<Boolean?> = _isRegister



    private fun resetData() {
        _isLogged.value = null
        _loggingStatus.value = null
        _registerStatus.value = null
        errorMessage.value = null
        _isRegister.value = null
    }




    fun login(email: String, password: String,isRemOn: Boolean) {
        resetData()
        _loggingStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            val task = userRepo.login(email, password, isRemOn)
            if (task is Result.Success) {
                _loggingStatus.value = DataStatus.SUCCESS
                _isLogged.value = true
            }else if (task is Result.Error){
                _loggingStatus.value = DataStatus.ERROR
                errorMessage.value = task.exception.message
            }
        }
    }



//    fun login(email: String, password: String,isRemOn: Boolean) {
//        resetData()
//        _loggingStatus.value = DataStatus.LOADING
//        viewModelScope.launch {
//            try {
//                val userId = userRepo.signWithEmailAndPassword(email,password)?.user!!.uid
//                val user = userRepo.getUserById(userId)
//                val isSeller = user?.userType == UserType.SELLER.name
//                val userType = user?.userType!!
//                userRepo.createUserLogging(userId,isRemOn,userType,isSeller)
//                _loggingStatus.value = DataStatus.SUCCESS
//                _isLogged.value = user
//            }catch (ex: FirebaseAuthInvalidUserException) {
//                val message = "user is not exist"
//                Log.d(TAG, message)
//                _loggingStatus.value = DataStatus.ERROR
//                errorMessage.value = message
//            }catch (ex: FirebaseAuthInvalidCredentialsException) {
//                val message = "incorrect password"
//                Log.d(TAG, message)
//                _loggingStatus.value = DataStatus.ERROR
//                errorMessage.value = message
//            }catch (ex: FirebaseNetworkException) {
//                val message = "network connection required"
//                Log.d(TAG, message)
//                errorMessage.value = message
//                _loggingStatus.value = DataStatus.ERROR
//            }
//
//        }
//    }


    fun signUp(user: User) {
        resetData()
        _registerStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            userRepo.createUserAccount(user,
            onSuccess = {
                _registerStatus.value = DataStatus.SUCCESS
                _isRegister.value = true
            },
            onError = { message ->
                _registerStatus.value = DataStatus.ERROR
                errorMessage.value = message
            })
        }
    }





    override fun onCleared() {
        super.onCleared()

    }


}