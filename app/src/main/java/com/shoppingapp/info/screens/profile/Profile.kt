package com.shoppingapp.info.screens.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.ProductDetailsBinding
import com.shoppingapp.info.databinding.ProfileBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.product_details.ProductDetailsViewModel
import com.shoppingapp.info.utils.StoreDataStatus


class Profile: Fragment() {

    companion object{
        const val TAG = "Profile"
    }

    private lateinit var binding: ProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.profile, container, false)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

//        homeViewModel.getUserData()
        setObserves()


        binding.apply {

            /** profile label **/
            profileTopAppBar.topAppBar.title = getString(R.string.profile)

            /** button back **/
            profileTopAppBar.topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }


            btnImageAccount.setOnClickListener {

            }


        }




        return binding.root

    }


    private fun setObserves() {

        /** live data user data **/
        homeViewModel.userData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.userName.text = it.name
                binding.email.text = it.email
                binding.phone.text = it.phone
            }
        }


        homeViewModel.dataStatus.observe(viewLifecycleOwner){
            if (it != null){
                when(it){
                    StoreDataStatus.LOADING ->{
                        binding.loader.root.visibility = View.VISIBLE
                        binding.profileLayout.visibility = View.GONE
                    }
                    StoreDataStatus.DONE ->{
                        binding.loader.root.visibility = View.GONE
                        binding.profileLayout.visibility = View.VISIBLE
                    }
                    StoreDataStatus.ERROR ->{

                    }
                }
            }
        }
    }

}