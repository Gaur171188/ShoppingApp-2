package com.shoppingapp.info.screens.registration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.UserDataSource
import com.shoppingapp.info.data.UserData
import com.shoppingapp.info.databinding.RegistrationBinding
import com.shoppingapp.info.utils.StoreDataStatus

class Registration : Fragment() {


    private lateinit var viewModel: RegistrationViewModel
    private lateinit var binding : RegistrationBinding

    private var email = ""
    private var password = ""
    private var name = ""
    private var phone = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.registration,container,false)


        binding.apply {


            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registration_to_login)
            }


            btnSignup.setOnClickListener {

                name = signupName.text!!.trim().toString()
                email = signupEmail.text!!.trim().toString()
                phone = signupPhone.text!!.trim().toString()
                password = signupPassword.text!!.trim().toString()

                val user = UserData(
                    "",
                    name,
                    phone,
                    email,
                    password
                )
                viewModel.registration(user)
            }


            observation()

        }




        return binding.root
    }


    private fun observation() {


        /** live data error message **/
        viewModel.errorMessage.observe(viewLifecycleOwner,{error ->
            if (error != null ){
                binding.signupErrorMessage.text = error
            }
        })


        /** live data progress **/
        viewModel.inProgress.observe(viewLifecycleOwner, {
            if (it != null){
                when(it){
                    StoreDataStatus.LOADING->{
                        binding.signupErrorMessage.visibility = View.GONE
                        binding.loader.visibility = View.VISIBLE
                    }
                    StoreDataStatus.DONE -> {
                        binding.signupErrorMessage.visibility = View.GONE
                        binding.loader.visibility = View.GONE
                    }
                    StoreDataStatus.ERROR ->{
                        binding.signupErrorMessage.visibility = View.VISIBLE
                        binding.loader.visibility = View.GONE
                    }
                }
            }
        })


        /** live data is registered **/
        viewModel.isRegistered.observe(viewLifecycleOwner,{user ->
            if (user != null){
                findNavController().navigate(R.id.action_registration_to_login)
            }
        })



    }


}