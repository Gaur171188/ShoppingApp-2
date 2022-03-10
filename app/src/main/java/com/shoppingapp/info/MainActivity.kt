package com.shoppingapp.info

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.shoppingapp.info.databinding.ActivityMainBinding
import com.shoppingapp.info.receiver.NetworkReceiver
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.ShoppingAppSessionManager

class MainActivity : AppCompatActivity(),NetworkReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityMainBinding
    private val networkReceiver = NetworkReceiver()
//    private var isConnected: Boolean? = null
    private val homeViewModel by viewModels<HomeViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)


        // set up navigation bottom
        setUpNav()


        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))


    }

    override fun onResume() {
        super.onResume()
        NetworkReceiver.connectivityReceiverListener = this
    }


    private fun setUpNav() {
        val navFragment =
            supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(binding.homeBottomNavigation, navFragment.navController)

        navFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home -> setBottomNavVisibility(View.VISIBLE)
                R.id.cart -> setBottomNavVisibility(View.VISIBLE)
                R.id.account -> setBottomNavVisibility(View.VISIBLE)
                R.id.orders -> setBottomNavVisibility(View.VISIBLE)
                R.id.orderSuccess -> setBottomNavVisibility(View.VISIBLE)
                else -> setBottomNavVisibility(View.GONE)
            }
        }

        val sessionManager = ShoppingAppSessionManager(this.applicationContext)
        if (sessionManager.isUserSeller()) { // Seller
            binding.homeBottomNavigation.menu.removeItem(R.id.cart)
        }else { // customer
            binding.homeBottomNavigation.menu.removeItem(R.id.orders)
        }
    }

    private fun setBottomNavVisibility(visibility: Int) {
        binding.homeBottomNavigation.visibility = visibility
    }


    // this function with listen if there is any change in network
    override fun onNetworkConnectionChanged(isConnect: Boolean) {
        homeViewModel.setConnectivityState(isConnect)
//        if (isConnected != null){
//            homeViewModel.setConnectivityState(isConnected!!)
//        }
//        isConnected = isConnect
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }

    // TODO: display a toast message the connection is not exist.

    // TODO: make broadcast receiver to check from the connection of network

}