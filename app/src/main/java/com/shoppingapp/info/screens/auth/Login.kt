package com.shoppingapp.info.screens.auth


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.activities.MainActivity
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.LoginBinding
import com.shoppingapp.info.utils.Constants
import com.shoppingapp.info.utils.DataStatus
import com.shoppingapp.info.utils.hide
import com.shoppingapp.info.utils.show
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Login : Fragment() {

    companion object{
        const val TAG = "Login"
    }

    private val viewModel by sharedViewModel<AuthViewModel>()
    private lateinit var binding: LoginBinding

    private var email: String = ""
    private var password: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.login, container, false)
//        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]



        setViews()

        setObservers()

        return binding.root

    }



    private fun setViews() {
        binding.apply {

            /** signup button **/
            btnSignup.setOnClickListener {
                findNavController().navigate(R.id.action_login_to_registration)
            }


            /** login button **/
            btnLogin.setOnClickListener {
                email = loginEmail.text!!.trim().toString()
                password = loginPassword.text!!.trim().toString()
                val isRemOn = binding.loginRememberSwitch.isChecked

                if (email.isEmpty() && password.isEmpty()){
                    viewModel.errorMessage.value = "all fields is required!"
                    binding.loginEmail.requestFocus()
                    binding.loginPassword.requestFocus()

                }else{
                    if (email.isEmpty()){
                        binding.loginEmail.error = "please enter your email!"
                        binding.loginEmail.requestFocus()
                        return@setOnClickListener
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        binding.loginEmail.error = "please enter a valid email"
                        binding.loginEmail.requestFocus()
                        return@setOnClickListener
                    }

                    if(password.isEmpty()){
                        binding.loginPassword.error = "please enter your password!"
                        binding.loginPassword.requestFocus()
                        return@setOnClickListener
                    }

                    if (password.length < 6){
                        binding.loginPassword.error = "6 char required!"
                        binding.loginPassword.requestFocus()
                        return@setOnClickListener
                    }else{
                        viewModel.login(email, password,isRemOn)
                    }


                }


            }

        }
    }

    private fun setObservers() {



        /** live data error message **/
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.loginErrorMessage.text = error
                binding.loginErrorMessage.show()
            }else{
                binding.loginErrorMessage.hide()
            }
        }


        /** live data progress **/
        viewModel.loggingStatus.observe(viewLifecycleOwner) {
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
            }
        }


        /** live data isLogin **/
        viewModel.isLogged.observe(viewLifecycleOwner) { isLogged ->
            if (isLogged != null) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }



    }

}