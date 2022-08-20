package com.shoppingapp.info.screens.select_address

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.SelectAddressBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.*
import com.shoppingapp.info.utils.getAddressId
import com.shoppingapp.info.utils.getOrderId
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SelectAddress : Fragment() {

    companion object { const val TAG = "SelectAddress" }

    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private val viewModel by sharedViewModel<SelectAddressViewModel>()

    private lateinit var binding: SelectAddressBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.select_address, container, false)

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
                    phone.setText(user.mobile)
                    viewModel.mName.value = user.name
                    viewModel.mPhone.value = user.mobile
                }
            }



            /** navigate to order details **/
            viewModel.navigateToOrderDetails.observe(viewLifecycleOwner){ order ->
                if (order != null){
                    val data = bundleOf(Constants.KEY_ORDER to order)
                    findNavController().navigate(R.id.action_selectAddress_to_orderDetails,data)
                    viewModel.navigateToOrderDetailsDone()
                }
            }




        }
    }

    override fun onResume() {
        super.onResume()

        // init cities
        initCities()
    }

    override fun onPause() {
        super.onPause()

        // stop updating the location of customer
        viewModel.stopLocationUpdates()
    }


    private fun setViews() {
        binding.apply {

            selectAddressAppBar.topAppBar.title = "Add Address"

            /** back button **/
            selectAddressAppBar.topAppBar.setOnClickListener {
                findNavController().navigateUp()
            }


            /** button next **/
            btnNext.setOnClickListener {
                sendData()
            }


        }

    }



    private fun initCities() {
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

            val userId = homeViewModel.userData.value?.userId!!
            val cart = homeViewModel.cartItems.value ?: emptyList()
            val itemPrice = homeViewModel.itemsPrice.value!!


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
            }
            if (viewModel.location.value == null){
                // need a litte time until location load
            } else {
                val address = User.Address(
                    getAddressId(),
                    viewModel.mName.value.toString(),
                    viewModel.mStreetAddress.value.toString(),
                    viewModel.mCity.value.toString(),
                    viewModel.location.value!!,
                    viewModel.mPhone.value.toString()
                )
                val order = User.OrderItem(getOrderId(),userId,cart,itemPrice, address = address)
                viewModel.navigateToOrderDetails(order)



            }



        }


    }

}