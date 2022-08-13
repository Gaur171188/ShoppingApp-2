package com.shoppingapp.info.screens.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppingapp.info.data.User
import com.shoppingapp.info.repository.user.RemoteUserRepository
import com.shoppingapp.info.utils.DataStatus
import kotlinx.coroutines.launch

class PaymentViewModel: ViewModel() {

    companion object{
        private const val TAG = "PaymentViewModel"
    }

    private val repository = RemoteUserRepository()

    /** progress **/
    private val _payStatus = MutableLiveData<DataStatus?>()
    val payStatus: LiveData<DataStatus?> = _payStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    fun pay(balance: Int, orderPrice: Int,order: User.OrderItem,userId: String){
        reset()
        viewModelScope.launch {
            if (balance >= orderPrice) { // send order
                val newBalance = balance - orderPrice
                sendOrder(order,userId,newBalance)
            } else{ // balance is not enough
                _errorMessage.value = "Your balance is not enough"
            }
        }
    }


    private fun sendOrder(order: User.OrderItem, userId: String, newBalance: Int) {
        Log.d(TAG,"Sending order...")
        _payStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            repository.insertOrder(order,userId)
                .addOnSuccessListener {
                    Log.d(TAG,"OnSendingOrder: send has been success")
                    updateWallet(newBalance, userId)

                }
                .addOnFailureListener { e->
                    Log.d(TAG,"OnSendingOrder: send has been failed due to ${e.message}")
                    _payStatus.value = DataStatus.ERROR
                    _errorMessage.value = e.message
                }
        }
    }


    private fun updateWallet(newBalance: Int, userId: String) {
        Log.d(TAG,"Updating Balance...")
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            if (user != null){
                user.wallet.balance = newBalance
                repository.updateUser(user)
                    .addOnSuccessListener {
                        Log.d(TAG,"OnUpdateBalance: update balance has been success")
                        _payStatus.value = DataStatus.SUCCESS
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG,"OnUpdateBalance: update balance failed due to ${e.message}")
                    }
            }

        }
    }



    private fun reset(){
        _payStatus.value = null
        _errorMessage.value = null
    }


}