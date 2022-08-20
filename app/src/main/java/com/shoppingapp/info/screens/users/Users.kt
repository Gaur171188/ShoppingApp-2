package com.shoppingapp.info.screens.users

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.R
import com.shoppingapp.info.activities.MainActivity
import com.shoppingapp.info.activities.RegistrationActivity
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.UsersBinding
import com.shoppingapp.info.screens.account.AccountViewModel
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.profile.ProfileViewModel
import com.shoppingapp.info.screens.statistics.StatisticsViewModel
import com.shoppingapp.info.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


@SuppressLint("NotifyDataSetChanged")
class Users : Fragment() {


    private lateinit var binding: UsersBinding
    private val statisticsViewModel by sharedViewModel<StatisticsViewModel>()
//    private val userController by lazy { UserController() }
    private val userAdapter by lazy { UserAdapter() }
    private val profileViewModel by sharedViewModel<ProfileViewModel>()
    private val homeViewModel by sharedViewModel<HomeViewModel>()


    private lateinit var userBottomSheet : BottomSheetBehavior<View>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.users, container, false)
        userBottomSheet = BottomSheetBehavior.from(binding.userSheet.userSheet)



        setViews()
        setObserves()



        return binding.root
    }




    private fun setObserves() {
        binding.apply {


            /** users **/
            statisticsViewModel.users.observe(viewLifecycleOwner) { users ->
                if (!users.isNullOrEmpty()){
                    userAdapter.putData(users)
                    swipeRefreshLayout.isRefreshing = false
                }
            }


            /** update user Status **/
            statisticsViewModel.updateUserState.observe(viewLifecycleOwner){ status->
                if (status != null) {
                    when(status){
                        DataStatus.LOADING-> {
                            userSheet.loader.root.show()
                        }
                        DataStatus.SUCCESS-> {
                            userSheet.loader.root.hide()
                            userBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            statisticsViewModel.resetUserStatus()
                        }
                        DataStatus.ERROR-> {
                            userSheet.loader.root.hide()
                            statisticsViewModel.resetUserStatus()
                        }
                    }
                }
            }


//
//            /** is sign out **/
//            profileViewModel.isSignOut.observe(viewLifecycleOwner){isSignOut->
//                if (isSignOut == true){
//                    val intent = Intent(requireContext(), RegistrationActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    startActivity(intent)
//                }
//            }



        }
    }



    private fun setViews() {
        binding.apply {
            setUserAdapter()
            setUsersTopAppBar()


            /** swipe refresh layout **/
            swipeRefreshLayout.setOnRefreshListener {
                statisticsViewModel.loadUsers()
            }

        }


    }

    private fun setUserAdapter() {

        /** on user clicked **/
        userAdapter.onClickListener = object: UserAdapter.OnClickListener {
            override fun onItemClicked(user: User, index: Int) {
                userProfileSheet(user.userId,index)
            }

        }
        binding.rvUsers.adapter = userAdapter
//        /** on user clicked **/
//        userController.clickListener = object: UserController.OnClickListener {
//            override fun onUserClicked(user: User, index: Int) {
//                userProfileSheet(user,index)
//            }
//
//        }
//        binding.rvUsers.adapter = userController.adapter
    }


    private fun setUsersTopAppBar(){
        binding.apply {
            usersTopAppBar.apply {

                /** set app bar menu **/
                topAppBar.inflateMenu(R.menu.home_app_bar_menu)
                topAppBar.menu.removeItem(R.id.item_cart)
                topAppBar.menu.removeItem(R.id.item_favorites)
                topAppBar.menu.removeItem(R.id.item_options)

                /** set hint **/
                homeSearchEditText.hint = resources.getString(R.string.search_user)

                /** button search **/
                homeSearchEditText.setOnEditorActionListener { textView, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val query = textView.text.trim().toString()

//                        productController.setData(viewModel.searchByProductName(query))
                        hideKeyboard()
                        true
                    } else {
                        false
                    }
                }


                /** button clear search edit text **/
                searchOutlinedTextLayout.setEndIconOnClickListener {
                    homeSearchEditText.setText("")
                    hideKeyboard()
                }

                /** app bar menu item **/
                topAppBar.setOnMenuItemClickListener { menuItem ->
                    setAppBarItemClicks(menuItem)
                }




            }
        }


    }

    private fun setAppBarItemClicks(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.item_filter -> {
                userFilterSheet()
//                val items = arrayOf("SELLER","CUSTOMER")
//                showDialogWithItems(items, 0)
                true
            }


            else -> false
        }
    }


    private fun userProfileSheet(userId: String, index: Int) {
        binding.apply {
            val user =  statisticsViewModel.users.value?.find { it.userId == userId }!!
            userSheet.user = user

            userSheet.apply {
                userBottomSheet = BottomSheetBehavior.from(userSheet)
                userBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                userSheet.apply {



                    /** button update **/
                    btnUpdate.setOnClickListener {

                        val isActive = btnSwitchActive.isChecked
                        val isPublic = btnSwitchPublic.isChecked
                        val name = name.text?.trim().toString()
                        val phone = phone.text?.trim().toString()
                        val balance = balance.text?.trim().toString().toInt()
                        val promotional = promotional.text?.trim().toString().toInt()
                        val country = selectCountry.selectedCountryName

                        user.name = name
                        user.mobile = phone
                        user.country = country
                        user.isActive = isActive
                        user.isPublic = isPublic
                        user.wallet.balance = balance
                        user.wallet.promotional = promotional

                        statisticsViewModel.updateUser(user,index)
                        hideKeyboard()

                    }


                    /** button close **/
                    btnClose.setOnClickListener {
                        userBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                        hideKeyboard()
                    }


                }
            }

        }
    }


    private fun userFilterSheet() {
        binding.apply {
            filterSheet.apply {
                specificFiltersProduct.root.hide()
                val bottom = BottomSheetBehavior.from(filterSheet)
                bottom.state = BottomSheetBehavior.STATE_EXPANDED
                specificFiltersUser.apply {



                    /** button close sheet **/
                    btnClose.setOnClickListener {
                        bottom.state = BottomSheetBehavior.STATE_COLLAPSED
                    }

                    /** button apply **/
                    btnApply.setOnClickListener {
                        val userType = selectUserType.text.toString()
                        val city = selectCity.text.toString()
                        val country = selectCountry.selectedCountryName.toString()
                        val rate = selectRate.text.toString()

                        val filter = statisticsViewModel.filter(userType,city,country,rate)

                        userAdapter.putData(filter)
//                        userAdapter.data = filter
//                        rvUsers.adapter?.notifyDataSetChanged()
//                        userController.setData(filter)

                        bottom.state = BottomSheetBehavior.STATE_COLLAPSED
                    }

                }
            }
        }
    }




    private fun showDialogWithItems(categoryItems: Array<String>, checkedOption: Int = 0 ) {
        var checkedItem = checkedOption
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Filter")
                .setSingleChoiceItems(categoryItems, checkedItem) { _, which ->
                    checkedItem = which
                }
                .setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(getString(R.string.pro_cat_dialog_ok_btn)) { dialog, _ ->
                    if (checkedItem == -1) {
                        dialog.cancel()
                    } else {
                        val selectedItem = categoryItems[checkedItem]
                        // when click on item do some actoin
                    }
                    dialog.cancel()
                }
                .show()
    }



//    fun showBottomSheed(){
//        val bottomSheet = BottomSheetDialog(requireContext())
//        BottomSheetBehavior<View>
//        val
//    }



}