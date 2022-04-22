package com.shoppingapp.info.screens.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.utils.Result
import kotlinx.coroutines.launch

class AccountViewModel(private val userRepository: UserRepository): ViewModel() {

    private var _isSignOut = MutableLiveData<Boolean?>()
    val isSignOut: LiveData<Boolean?> get() = _isSignOut

    private var _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error




    fun signOut() {
        viewModelScope.launch {
            val res = userRepository.signOut()
            if (res is Result.Success) {
                _isSignOut.value = true
            }else{
                _error.value = "error happening!"
                _isSignOut.value = null
            }
        }
    }

    fun isUserIsSeller() = userRepository.isUserSeller()

}