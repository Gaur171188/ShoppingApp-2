package com.shoppingapp.info.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.ActivityMainBinding
import com.shoppingapp.info.receiver.NetworkReceiver
import com.shoppingapp.info.utils.Constants
import com.shoppingapp.info.utils.SharePrefManager
import com.shoppingapp.info.utils.UserType
import com.shoppingapp.info.utils.showMessage

class MainActivity : AppCompatActivity(),NetworkReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityMainBinding
    private val networkReceiver = NetworkReceiver()
    private var isConnected: Boolean? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)



        // set up navigation bottom
        setUpNav()
        setUserTypeViews()

//        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
//

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



    private fun setUserTypeViews() {
        val sessionManager = SharePrefManager(this.applicationContext)
        when(sessionManager.loadUser().userType){ // user type
            UserType.CUSTOMER.name-> {
                binding.homeBottomNavigation.menu.removeItem(R.id.orders)
                binding.homeBottomNavigation.menu.removeItem(R.id.users)
                binding.homeBottomNavigation.menu.removeItem(R.id.statistics)
            }
            UserType.SELLER.name-> {
                binding.homeBottomNavigation.menu.removeItem(R.id.users)
                binding.homeBottomNavigation.menu.removeItem(R.id.statistics)
            }
            UserType.ADMIN.name-> {
                binding.homeBottomNavigation.menu.removeItem(R.id.orders)
                binding.homeBottomNavigation.menu.removeItem(R.id.account)

            }
        }
        
    }

    private fun setUpNav() {
        val navFragment =
            supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(binding.homeBottomNavigation, navFragment.navController)

        navFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home -> setBottomNavVisibility(View.VISIBLE)
                R.id.account -> setBottomNavVisibility(View.VISIBLE)
                R.id.orders -> setBottomNavVisibility(View.VISIBLE)
                R.id.orderSuccess -> setBottomNavVisibility(View.VISIBLE)
                R.id.users -> setBottomNavVisibility(View.VISIBLE)
                R.id.statistics -> setBottomNavVisibility(View.VISIBLE)
                else -> setBottomNavVisibility(View.GONE)
            }
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
//        unregisterReceiver(networkReceiver)
    }


}