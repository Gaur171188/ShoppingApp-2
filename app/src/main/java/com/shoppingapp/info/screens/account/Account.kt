package com.shoppingapp.info.screens.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.RegistrationActivity
import com.shoppingapp.info.databinding.AccountBinding

import android.content.Intent

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.screens.home.HomeViewModel


class Account : Fragment() {

    companion object{
        private const val TAG = "Account"
    }

//
//    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val viewModel by activityViewModels<AccountViewModel>()
    private lateinit var binding: AccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.account, container, false)



        setViews()

        return binding.root
    }

    private fun setViews() {
        binding.accountTopAppBar.topAppBar.title = getString(R.string.account)

        // TODO: 4/19/2022   user this after dependency injection
//        if (viewModel.isUserIsSeller()){
//            binding.btnOrders.visibility = View.GONE
//        }


        binding.btnProfile.setOnClickListener {
            Log.d(TAG, "Profile Selected")
            findNavController().navigate(R.id.action_accountFragment_to_profileFragment)
        }
        binding.btnOrders.setOnClickListener {
            Log.d(TAG, "Orders Selected")
            findNavController().navigate(R.id.action_accountFragment_to_ordersFragment)
        }

        binding.btnSignout.setOnClickListener {
            Log.d(TAG, "Sign Out Selected")
            showSignOutDialog()
        }
    }

    private fun showSignOutDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.sign_out_dialog_title_text))
                .setMessage(getString(R.string.sign_out_dialog_message_text))
                .setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(getString(R.string.dialog_sign_out_btn_text)) { _, _ ->

                    // TODO: 4/19/2022 user this after dependency injection
//                   viewModel.signOut()
                }
                .show()
        }
    }


    private fun navigateToSignUpActivity() {
        val homeIntent = Intent(context, RegistrationActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(homeIntent)
        requireActivity().finish()
    }


}


