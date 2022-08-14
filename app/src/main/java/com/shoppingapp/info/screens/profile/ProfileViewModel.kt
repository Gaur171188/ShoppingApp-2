package com.shoppingapp.info.screens.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.utils.DataStatus
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _loadImageStatus = MutableLiveData<DataStatus?>()
    val loadImageStatus: LiveData<DataStatus?> = _loadImageStatus


    private val repository = RemoteUserRepository()




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