package com.shoppingapp.info.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager


class NetworkReceiver(): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedOrConnecting(context!!))
        }



    }



private fun isConnectedOrConnecting(context: Context): Boolean {
    val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connMgr.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnectedOrConnecting
}


interface ConnectivityReceiverListener {
    fun onNetworkConnectionChanged(isConnect: Boolean)
}

companion object {
    var connectivityReceiverListener: ConnectivityReceiverListener? = null
}

}