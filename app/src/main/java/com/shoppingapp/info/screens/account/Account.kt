package com.shoppingapp.info.screens.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shoppingapp.info.R
import com.shoppingapp.info.activities.RegistrationActivity
import com.shoppingapp.info.databinding.AccountBinding
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder



class Account : Fragment() {

    companion object{
        private const val TAG = "Account"
    }


    private lateinit var viewModel: AccountViewModel
    private lateinit var binding: AccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.account, container, false)
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        setViews()
        setObserves()

        return binding.root
    }

    private fun setObserves() {

        /** isSignOut live data **/
        viewModel.isSignOut.observe(viewLifecycleOwner) {
            if (it != null){
                navigateToRegistrationActivity()
            }
        }

    }

    private fun setViews() {
        binding.apply {

            // set Title
            accountTopAppBar.topAppBar.title = getString(R.string.account)



            /** button profile **/
            btnProfile.setOnClickListener {
                Log.d(TAG, "Profile Selected")
                findNavController().navigate(R.id.action_accountFragment_to_profileFragment)
            }


            /** button sign out **/
            btnSignout.setOnClickListener {
                Log.d(TAG, "Sign Out Selected")
                showSignOutDialog()
            }

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
                   viewModel.signOut(requireContext())
                }
                .show()
        }
    }


    private fun navigateToRegistrationActivity() {
        val homeIntent = Intent(context, RegistrationActivity::class.java)
        homeIntent.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context?.startActivity(homeIntent)
        requireActivity().finish()
    }


}


