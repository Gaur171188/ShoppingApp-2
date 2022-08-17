package com.shoppingapp.info.screens.users

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.UsersBinding
import com.shoppingapp.info.screens.statistics.StatisticsViewModel
import com.shoppingapp.info.utils.Constants
import com.shoppingapp.info.utils.ProductCategories
import com.shoppingapp.info.utils.hideKeyboard
import org.koin.android.viewmodel.ext.android.sharedViewModel

class Users : Fragment() {


    private lateinit var binding: UsersBinding
    private val statisticsViewModel by sharedViewModel<StatisticsViewModel>()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//        viewModel = ViewModelProvider(this)[UsersViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater,R.layout.users, container, false)


        setViews()
        setObserves()



        return binding.root
    }



    private fun setObserves() {

    }

    private fun setViews() {

    }

    private fun setUsersTopAppBar(){
        binding.apply {
            usersTopAppBar.apply {
                /** set app bar menu **/
                topAppBar.inflateMenu(R.menu.home_app_bar_menu)



                /** set hint **/
                homeSearchEditText.hint = resources.getString(R.string.search_product)

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

                val items = arrayOf("SELLER","CUSTOMER")

                showDialogWithItems(items, 0)
                true
            }

            else -> false
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



}