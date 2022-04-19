package com.shoppingapp.info

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.shoppingapp.info.databinding.ActivityMainBinding
import com.shoppingapp.info.receiver.NetworkReceiver
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.SharePrefManager

class MainActivity : AppCompatActivity(),NetworkReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityMainBinding
    private val networkReceiver = NetworkReceiver()
    private var isConnected: Boolean? = null
    private val homeViewModel by viewModels<HomeViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)


        // set up navigation bottom
        setUpNav()


        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))


        binding.apply {


            /** button refresh **/
            noConnectionLayout.btnRefresh.setOnClickListener {
                if (isConnected!!){
                    binding.mainActivity.visibility = View.VISIBLE
                    binding.noConnectionLayout.root.visibility = View.GONE
                    setBottomNavVisibility(View.VISIBLE)
                }
            }




        }

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

        val sessionManager = SharePrefManager(this.applicationContext)
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
//        homeViewModel.setConnectivityState(isConnect)
        isConnected = isConnect
        if (isConnect){
//            binding.mainActivity.visibility = View.VISIBLE
//            binding.noConnectionLayout.visibility = View.GONE
        }else{
            binding.mainActivity.visibility = View.GONE
            binding.noConnectionLayout.root.visibility = View.VISIBLE
            setBottomNavVisibility(View.GONE)
        }


//        if (isConnected != null){
//            homeViewModel.setConnectivityState(isConnected!!)
//        }

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }


}