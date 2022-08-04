package com.shoppingapp.info.screens.account

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.utils.Result
import com.shoppingapp.info.utils.SharePrefManager
import kotlinx.coroutines.launch

class AccountViewModel(): ViewModel() {

    companion object{
        private const val TAG = "AccountViewModel"
    }

    private val repository = RemoteUserRepository()

    private var _isSignOut = MutableLiveData<Boolean?>()
    val isSignOut: LiveData<Boolean?> get() = _isSignOut

    private var _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error



    fun signOut(context: Context) {
        viewModelScope.launch {
            repository.signOut()
            SharePrefManager(context).signOut()
            _isSignOut.value = true
            Log.d(TAG,"signOut Success")
        }
    }



}