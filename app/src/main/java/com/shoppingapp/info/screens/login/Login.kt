package com.shoppingapp.info.screens.login



import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.MainActivity
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.LoginBinding
import com.shoppingapp.info.utils.StoreDataStatus


class Login : Fragment() {

    companion object{
        const val TAG = "Login"
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginBinding

    private var email: String = ""
    private var password: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.login, container, false)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]




        binding.apply {

            /** signup button **/
            btnSignup.setOnClickListener {
                findNavController().navigate(R.id.action_login_to_registration)
            }


            /** login button **/
            btnLogin.setOnClickListener {
                viewModel.initLogin()
                email = loginEmail.text!!.trim().toString()
                password = loginPassword.text!!.trim().toString()
                val isRemOn = binding.loginRememberSwitch.isChecked

                if (email.isEmpty() && password.isEmpty()){
                    viewModel.setLoginError("all fields is required!")

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
                    }

                    viewModel.login(email, password,isRemOn)
                }


            }




        }


        observation()

        return binding.root

    }

    private fun observation() {
        /** live data error message **/
        viewModel.errorMessage.observe(viewLifecycleOwner,{error ->
            if (error != null ){
                binding.loginErrorMessage.text = error
            }
        })


        /** live data progress **/
        viewModel.inProgress.observe(viewLifecycleOwner, {
            if (it != null){
                when(it){
                    StoreDataStatus.LOADING->{
                        binding.loginErrorMessage.visibility = View.GONE
                        binding.loader.visibility = View.VISIBLE
                    }
                    StoreDataStatus.DONE -> {
                        binding.loginErrorMessage.visibility = View.GONE
                        binding.loader.visibility = View.GONE
                    }
                    StoreDataStatus.ERROR ->{
                        binding.loginErrorMessage.visibility = View.VISIBLE
                        binding.loader.visibility = View.GONE

                    }
                }
            }else{
                binding.loginErrorMessage.visibility = View.GONE
            }
        })


        /** live data isLogin **/
        viewModel.isLogged.observe(viewLifecycleOwner,{ isLogged->
            if (isLogged != null){
                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })

    }

}