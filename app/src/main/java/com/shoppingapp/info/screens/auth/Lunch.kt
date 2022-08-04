package com.shoppingapp.info.screens.auth

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.activities.MainActivity
import com.shoppingapp.info.databinding.LunchBinding
import com.shoppingapp.info.utils.SharePrefManager

class Lunch : Fragment() {


    companion object {
        private const val ONE_SECOND = 1000L
        private const val THREE_SECOND = 3000L

        private const val TAG = "Lunch"
    }

    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: LunchBinding
    private lateinit var timer: CountDownTimer


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.lunch, container, false)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]



        viewModel.isUserLogged(requireContext())

        timer = object : CountDownTimer(THREE_SECOND, ONE_SECOND){
            override fun onTick(p0: Long) {}
            override fun onFinish() { isUserLogged() }
        }
        timer.start()


        return binding.root


    }



    fun isUserLogged() {
        val isLogged = viewModel.isLogged.value
        if (isLogged == true) { // user is logged
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            Log.d(TAG,"user is logged")
        } else { // user is not logged
            Log.d(TAG,"user is not logged")
            findNavController().navigate(R.id.action_lunch_to_login)
        }
    }


}