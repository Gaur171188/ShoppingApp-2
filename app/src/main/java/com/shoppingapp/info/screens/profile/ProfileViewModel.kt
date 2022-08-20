package com.shoppingapp.info.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.screens.account.AccountViewModel
import com.shoppingapp.info.utils.DataStatus
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepo: UserRepository) : ViewModel() {

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    private val _loadImageStatus = MutableLiveData<DataStatus?>()
    val loadImageStatus: LiveData<DataStatus?> = _loadImageStatus

    private var _isSignOut = MutableLiveData<Boolean?>()
    val isSignOut: LiveData<Boolean?> = _isSignOut

    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage





    fun signOut() {
        viewModelScope.launch {
            userRepo.signOut()
            _isSignOut.value = true
            Log.d(TAG,"signOut Success")
        }
    }




//    fun uploadImage(uri: Uri,userId: String) {
//        viewModelScope.launch {
//            val fileName = uri.toString()
//            val imageUri = repository.uploadFile(uri, fileName)
//            val user = repository.getUserById(userId)!!
//            user.imageProfile = imageUri.
//
//        }
//
//    }






}