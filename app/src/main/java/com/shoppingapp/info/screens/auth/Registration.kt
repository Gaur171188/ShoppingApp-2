package com.shoppingapp.info.screens.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.RegistrationBinding
import com.shoppingapp.info.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Registration : Fragment() {


    private val viewModel by sharedViewModel<AuthViewModel>()
    private lateinit var binding : RegistrationBinding

    private var email = ""
    private var password = ""
    private var name = ""
    private var phone = ""
    private var country = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


//        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.registration,container,false)


        binding.apply {


            /** select country **/
            selectCountry.setOnCountryChangeListener {
                country = selectCountry.selectedCountryName // set country name
            }



            /** button login **/
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registration_to_login)
            }


            /** button sign up **/
            btnSignup.setOnClickListener {

                name = signupName.text!!.trim().toString()
                email = signupEmail.text!!.trim().toString()
                phone = signupPhone.text!!.trim().toString()
                password = signupPassword.text!!.trim().toString()

                val usertype = if(signupSellerSwitch.isChecked) UserType.SELLER.name else UserType.CUSTOMER.name


                if (name.isEmpty() && email.isEmpty() && email.isEmpty() && password.isEmpty()){
                    viewModel.errorMessage.value = "please fill information"
                    signupName.requestFocus()
                    signupPhone.requestFocus()
                    signupEmail.requestFocus()
                    signupPassword.requestFocus()

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

                    if (password.length < 6) {
                        binding.signupPassword.error = "6 char required!"
                        binding.signupPassword.requestFocus()
                        return@setOnClickListener
                    }
                    if (country.isEmpty()){
                        showMessage(requireContext(),"please select your country")
                    }

                    if (signupPolicySwitch.isChecked){
                        val user = User("", name, phone, email, password, userType = usertype, isPublic = true, isActive = true)
                        viewModel.signUp(user)
                    }else{
                        viewModel.errorMessage.value = "You must confirm to the privacy policy!"
                    }


                }




            }


            observation()

        }




        return binding.root
    }


    private fun observation() {


        /** live data error message **/
        with(viewModel) {

            /** live data error message **/
            errorMessage.observe(viewLifecycleOwner) { error ->
                if (error != null) {
                    binding.signupErrorMessage.text = error
                    binding.signupErrorMessage.show()
                }else{
                    binding.signupErrorMessage.hide()
                }
            }


            /** live data progress **/
            registerStatus.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        DataStatus.LOADING -> {
                            binding.loader.visibility = View.VISIBLE
                        }
                        DataStatus.SUCCESS -> {
                            binding.loader.visibility = View.GONE
                        }
                        DataStatus.ERROR -> {
                            binding.loader.visibility = View.GONE
                        }
                    }
                } else {
                    binding.signupErrorMessage.visibility = View.GONE
                }
            }


            /** live data is registered **/
            isRegister.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    findNavController().navigate(R.id.action_registration_to_login)
                }
            }


        }


    }


}