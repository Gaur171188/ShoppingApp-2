package com.shoppingapp.info.screens.select_address

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.birjuvachhani.locus.Locus
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Location
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.SelectAddressBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.Constants
import com.shoppingapp.info.utils.showMessage
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SelectAddress : Fragment() {

    companion object { const val TAG = "SelectAddress" }

    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private val viewModel by sharedViewModel<SelectAddressViewModel>()

    private lateinit var binding: SelectAddressBinding

//    var mName = ""
//    var mPhone = ""
//    var mStreetAddress = ""
//    var mCity = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.select_address, container, false)


        val cartProducts =  homeViewModel.cartProducts.value ?: emptyList()
        val itemsPrice =  homeViewModel.itemsPrice.value ?: emptyMap()
        val cartItems =  homeViewModel.cartItems.value ?: emptyList()
        val quantityCount = homeViewModel.quantityCount.value ?: 0


//        Log.d(TAG,"cartProducts: ${cartProducts.size} \n itemsPrice: $itemsPrice \n cartItems: ${cartItems.size} \n quantityCount: $quantityCount")

        setViews()

        setObserves()


        viewModel.startLocationUpdates(this)


        return binding.root
    }



    private fun setObserves() {
        binding.apply {


            /** user data **/
            homeViewModel.userData.observe(viewLifecycleOwner){user->
                if (user != null){
                    name.setText(user.name)
                    phone.setText(user.phone)
                    viewModel.mName.value = user.name
                    viewModel.mPhone.value = user.phone
                }
            }




        }
    }

    override fun onPause() {
        super.onPause()

        viewModel.stopLocationUpdates()
    }
    private fun setViews() {
        binding.apply {

            selectAddressAppBar.topAppBar.title = "Add Address"

            /** button next **/
            btnNext.setOnClickListener {
                sendData()
            }


            initCities()



        }



    }


    // set the libyan cities
    private fun initCities(){
        val libyanCities = this.resources.getStringArray(R.array.cities)
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,libyanCities)
        binding.selectCity.setAdapter(adapter)
    }


    private fun sendData() {
        binding.apply {
            viewModel.mName.value = name.text?.trim().toString()
            viewModel.mCity.value = selectCity.text?.trim().toString()
            viewModel.mPhone.value = phone.text?.trim().toString()
            viewModel.mStreetAddress.value = street.text?.trim().toString()


            if (viewModel.mName.value.isNullOrEmpty()){
                name.error = "Name Required!"
                name.requestFocus()
            }
            if (viewModel.mPhone.value.isNullOrEmpty()){
                phone.error = "Phone Required!"
                phone.requestFocus()
            }

            if (viewModel.mStreetAddress.value.isNullOrEmpty()){
                street.error = "Street Address Required!"
                street.requestFocus()
            }

            if (viewModel.mCity.value.isNullOrEmpty()) {
                showMessage(requireContext(),"Please Select City")
            }else {
                findNavController().navigate(R.id.action_orders_to_orderDetailsFragment)
            }



        }


    }

}