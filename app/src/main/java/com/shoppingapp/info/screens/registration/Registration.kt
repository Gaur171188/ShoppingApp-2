package com.shoppingapp.info.screens.registration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.RegistrationBinding

import com.shoppingapp.info.utils.StoreDataStatus
import com.shoppingapp.info.utils.UserType

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
                viewModel.initRegister()

                name = signupName.text!!.trim().toString()
                email = signupEmail.text!!.trim().toString()
                phone = signupPhone.text!!.trim().toString()
                password = signupPassword.text!!.trim().toString()

                val usertype = if(signupSellerSwitch.isChecked) UserType.SELLER.name else UserType.CUSTOMER.name


                if (name.isEmpty() && email.isEmpty() && email.isEmpty() && password.isEmpty()){
                    viewModel.setRegistrationError("all fields is required!")
                }else{

                    if (name.isEmpty()){
                        binding.signupName.error = "please enter your name!"
                        binding.signupName.requestFocus()
                        return@setOnClickListener
                    }

                    if (phone.length < 9){
                        binding.signupPhone.error = "9 char required!"
                        binding.signupPhone.requestFocus()
                        return@setOnClickListener
                    }

                    if (phone.length < 6){
                        binding.signupPhone.error = "6 char required!"
                        binding.signupPhone.requestFocus()
                        return@setOnClickListener
                    }
                    if (email.isEmpty()){
                        binding.signupEmail.error = "please enter your email!"
                        binding.signupEmail.requestFocus()
                        return@setOnClickListener
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        binding.signupEmail.error = "please enter a valid email"
                        binding.signupEmail.requestFocus()
                        return@setOnClickListener
                    }

                    if(password.isEmpty()){
                        binding.signupPassword.error = "please enter your password!"
                        binding.signupPassword.requestFocus()
                        return@setOnClickListener
                    }

                    if (password.length < 6){
                        binding.signupPassword.error = "6 char required!"
                        binding.signupPassword.requestFocus()
                        return@setOnClickListener
                    }

                    if (signupPolicySwitch.isChecked){
                        val user = User("", name, phone, email, password, userType = usertype)
                        viewModel.registration(user)
                    }else{
                        viewModel.setRegistrationError("You must confirm to the privacy policy!")
                    }


                }




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
            }else{
                binding.signupErrorMessage.visibility = View.GONE
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