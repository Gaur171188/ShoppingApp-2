package com.shoppingapp.info.screens.select_address

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.birjuvachhani.locus.Locus
import com.shoppingapp.info.data.Location
import com.shoppingapp.info.data.User
import com.shoppingapp.info.utils.DataStatus
import com.shoppingapp.info.utils.getOrderId
import com.shoppingapp.info.utils.getProductId
import kotlinx.coroutines.launch

class SelectAddressViewModel : ViewModel() {

    val mName = MutableLiveData<String>()
    val mPhone = MutableLiveData<String>()
    val mStreetAddress = MutableLiveData<String>()
    val mCity = MutableLiveData<String>()

    // progress
    private val _locationStatus = MutableLiveData<DataStatus?>()
    val locationStatus: LiveData<DataStatus?> = _locationStatus

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    private val _navigateToOrderDetails = MutableLiveData<User.OrderItem?>()
    val navigateToOrderDetails: LiveData<User.OrderItem?> = _navigateToOrderDetails




    fun navigateToOrderDetails(order: User.OrderItem) {
        _navigateToOrderDetails.value = order
    }


    fun navigateToOrderDetailsDone() {
        _navigateToOrderDetails.value = null
    }


    private fun setStatus(){
        _locationStatus.value = null
    }


    // you location will be updated each second
    fun startLocationUpdates(context: Fragment) {
        setStatus()
        _locationStatus.value = DataStatus.LOADING
        viewModelScope.launch {
            Locus.startLocationUpdates(context) { result ->
                _locationStatus.value = DataStatus.SUCCESS
                val latitude = result.location?.latitude
                val longitude = result.location?.longitude
                val location = Location(latitude!!,longitude!!)
                _location.value = location
                if (result.error != null){
                    _locationStatus.value = DataStatus.ERROR
                }
            }
        }
    }

    fun stopLocationUpdates() {
        Locus.stopLocationUpdates()
    }


}