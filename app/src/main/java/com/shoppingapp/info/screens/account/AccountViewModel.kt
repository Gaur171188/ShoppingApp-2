package com.shoppingapp.info.screens.account

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.screens.payment.PaymentViewModel
import com.shoppingapp.info.utils.DataStatus
import com.shoppingapp.info.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountViewModel(private val userRepo: UserRepository): ViewModel() {

    companion object{
        private const val TAG = "AccountViewModel"
    }

    private val repository = RemoteUserRepository()


    private val _updateUserState = MutableLiveData<DataStatus?>()
    val updateUserState: LiveData<DataStatus?> = _updateUserState

    private var _isSignOut = MutableLiveData<Boolean?>()
    val isSignOut: LiveData<Boolean?> get() = _isSignOut

    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage



    fun signOut() {
        viewModelScope.launch {
            userRepo.signOut()
            _isSignOut.value = true
            Log.d(TAG,"signOut Success")
        }
    }




    fun updateImageProfile(uri: Uri?) {
        viewModelScope.launch {

            val fileName = uri.toString()
            val imageUri = userRepo.uploadFile(uri!!, fileName).toString()
            Log.d("Images",imageUri)
//            val user = _userData.value!!
//            user.imageProfile = imageUri
//            _userData.value = user
//            userRepository.updateUser(user)




        }
    }



    fun reset(){
        _updateUserState.value = null
    }



}