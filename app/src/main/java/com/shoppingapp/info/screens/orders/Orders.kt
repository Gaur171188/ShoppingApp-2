package com.shoppingapp.info.screens.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.OrdersBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Orders: Fragment() {

    companion object{
        const val TAG = "Orders"
    }

    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private lateinit var viewModel: OrdersViewModel

    private lateinit var binding: OrdersBinding
    private val orderController by lazy { OrderController() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.orders, container, false)
        viewModel = ViewModelProvider(this)[OrdersViewModel::class.java]


        setViews()
        setObservers()

        return binding.root
    }


    private fun navigateToOrderDetails(order: User.OrderItem) {
        val data = bundleOf(Constants.KEY_ORDER to order)
        findNavController().navigate(R.id.action_orders_to_orderDetails,data)
    }


    private fun setAdapter() {
//        val orders = homeViewModel.orders.value ?: emptyList()
        orderController.setData(emptyList())

        orderController.clickListener = object : OrderController.OnClickListener {

            override fun onItemClick(order: User.OrderItem) {
                navigateToOrderDetails(order)

            }

        }


        binding.orderRecyclerView.adapter = orderController.adapter
    }


    private fun setViews() {

        setAdapter()
        setOrderAppBar()
        binding.apply {

//            ordersAppBar.topAppBar.title = "Orders"

            /** refresh layout **/
            swipeRefreshLayout.setOnRefreshListener {
                homeViewModel.loadData()
            }


        }


    }

    private fun setOrderAppBar() {
        binding.apply {

            /** set hint **/
            orderTopAppBar.homeSearchEditText.hint = resources.getString(R.string.search_order)

            /** set app bar menu **/
            orderTopAppBar.topAppBar.inflateMenu(R.menu.home_app_bar_menu)
            orderTopAppBar.topAppBar.menu.removeItem(R.id.item_favorites)
            orderTopAppBar.topAppBar.menu.removeItem(R.id.item_cart)
            orderTopAppBar.topAppBar.menu.removeItem(R.id.item_options)


            /** button search **/
            orderTopAppBar.homeSearchEditText.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) { // search
                    val orders = homeViewModel.orders.value ?: emptyList()
                    val query = textView.text.trim().toString()
                    orderController.setData(viewModel.searchByOrderId(query,orders))
                    hideKeyboard()
                    true }
                else { false }
            }

            /** button clear search edit text **/
            orderTopAppBar.searchOutlinedTextLayout.setEndIconOnClickListener {
                orderTopAppBar.homeSearchEditText.setText("")
                hideKeyboard()
            }

            /** select menu item **/
            orderTopAppBar.topAppBar.setOnMenuItemClickListener { menuItem ->
                setAppBarItemClicks(menuItem)
            }


        }

    }


    private fun setObservers() {

        /** orders live data **/
        homeViewModel.orders.observe(viewLifecycleOwner) { orders->
            if (!orders.isNullOrEmpty()) {
                orderController.setData(orders)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }


    private fun setAppBarItemClicks(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.item_filter -> {
                orderFilter()
//                val checkedItem = orderStatusFilters.indexOf(viewModel.filterOrderStatus.value)
//                showDialogWithItems(orderStatusFilters, checkedItem)
                true
            }
            else -> false
        }
    }

    private fun orderFilter() {
        binding.apply {
            orderFilter.apply {

                val bottom = BottomSheetBehavior.from(filterSheet)
                bottom.state = BottomSheetBehavior.STATE_EXPANDED
                setSortItems()

                filterSheet.apply {

                    /** button close sheet **/
                    btnClose.setOnClickListener {
                        bottom.state = BottomSheetBehavior.STATE_COLLAPSED
                        hideKeyboard()
                    }

                    /** button apply **/
                    btnApply.setOnClickListener {
                        val sort = selectSort.text.toString()

                        val orders = homeViewModel.orders.value ?: emptyList()

                        val filter = viewModel.filter(orders,sort)
                        showMessage(requireContext(),filter.size.toString())
                        orderController.setData(filter)
                        bottom.state = BottomSheetBehavior.STATE_COLLAPSED
                        hideKeyboard()
                    }

                }
            }
        }
    }

    private fun setSortItems() {
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, sortOrderItems)
        binding.orderFilter.selectSort.setAdapter(adapter)
    }


//    private fun showDialogWithItems(
//        categoryItems: Array<String>,
//        checkedOption: Int = 0) {
//        var checkedItem = checkedOption
//
//        MaterialAlertDialogBuilder(requireContext())
//                .setTitle(getString(R.string.select_order_status))
//                .setSingleChoiceItems(categoryItems, checkedItem) { _, which ->
//                    checkedItem = which
//                }
//                .setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
//                    dialog.cancel()
//                }
//                .setPositiveButton(getString(R.string.pro_cat_dialog_ok_btn)) { dialog, _ ->
//                    if (checkedItem == -1) {
//                        dialog.cancel()
//                    } else {
//                        // apply the filter
//                        val filter = categoryItems[checkedItem]
//                        val orders = homeViewModel.orders.value ?: emptyList()
//                        orderController.setData(viewModel.filter(filter, orders))
//                    }
//                    dialog.cancel()
//                }
//                .show()
//    }




}