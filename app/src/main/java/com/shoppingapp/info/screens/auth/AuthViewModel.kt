package com.shoppingapp.info.screens.auth



import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.utils.DataStatus
import com.shoppingapp.info.utils.SharePrefManager
import com.shoppingapp.info.utils.UserType
import kotlinx.coroutines.*

class AuthViewModel (): ViewModel() {

    companion object {
        const val TAG = "LoginViewModel"
    }

    private val repository = RemoteUserRepository()

    private val _inProgress = MutableLiveData<DataStatus?>()
    val inProgress: LiveData<DataStatus?> = _inProgress

    val errorMessage = MutableLiveData<String?>()


    private val _isLogged = MutableLiveData<Boolean>()
    val isLogged: LiveData<Boolean> = _isLogged

    private val _isRegister = MutableLiveData<Boolean?>()
    val isRegister: LiveData<Boolean?> = _isRegister





    fun isUserLogged(context: Context){
        val sharePrefManager = SharePrefManager(context)
        val isRemOn = sharePrefManager.isRememberMeOn()
        val isLogged = sharePrefManager.isLoggedIn()
        if (isRemOn && isLogged){
            // if user logged go to main
            _isLogged.value = sharePrefManager.isLoggedIn()
        }
    }

    fun resetData() {
        _isLogged.value = false
        _inProgress.value = null
        errorMessage.value = null
        _isRegister.value = null
    }



    fun login(context: Context,email: String, password: String,isRemOn: Boolean) {
        resetData()
        _inProgress.value = DataStatus.LOADING
        viewModelScope.launch {
            repository.signWithEmailAndPassword(context,email,password,isRemOn,
            onSuccess = {
                Log.d(TAG,"onSuccess: login is success")
                _inProgress.value = DataStatus.SUCCESS
                _isLogged.value = true

            },
            onError = { message ->
                Log.d(TAG,"onError: login is failed due to $message")
                _inProgress.value = DataStatus.ERROR
                errorMessage.value = message
            })
        }
    }


    fun signUp(user: User) {
        resetData()
        _inProgress.value = DataStatus.LOADING
        viewModelScope.launch {
            repository.createUserAccount(user,
            onSuccess = {
                _inProgress.value = DataStatus.SUCCESS
                _isRegister.value = true
            },
            onError = { message ->
                _inProgress.value = DataStatus.ERROR
                errorMessage.value = message
            })
        }
    }





    override fun onCleared() {
        super.onCleared()

    }


}