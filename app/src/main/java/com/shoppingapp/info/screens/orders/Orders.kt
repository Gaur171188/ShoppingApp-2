package com.shoppingapp.info.screens.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.OrdersBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.StoreDataStatus
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Orders : Fragment() {

    companion object{
        const val TAG = "Orders"
    }

    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private lateinit var binding: OrdersBinding
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.orders, container, false)
//        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setViews()
        setObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getAllOrders()
    }


    private fun setViews() {
        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
        binding.ordersAppBar.topAppBar.title = getString(R.string.orders)
        binding.ordersAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.ordersEmptyMessage.visibility = View.GONE
        if (context != null) {
            ordersAdapter = OrdersAdapter(emptyList(), requireContext())
            ordersAdapter.onClickListener = object : OrdersAdapter.OnClickListener {
                override fun onCardClick(orderId: String) {
                    Log.d(TAG, "onOrderSummaryClick: Getting order details")
                    findNavController().navigate(
                        R.id.action_orders_to_orderDetailsFragment,
                        bundleOf("orderId" to orderId)
                    )
                }
            }
            binding.orderAllOrdersRecyclerView.adapter = ordersAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {
        homeViewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                StoreDataStatus.LOADING -> {
                    binding.orderAllOrdersRecyclerView.visibility = View.GONE
                    binding.ordersEmptyMessage.visibility = View.GONE
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                    binding.loaderLayout.circularLoader.showAnimationBehavior
                }
                else -> {
                    binding.loaderLayout.circularLoader.hideAnimationBehavior
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                }
            }

            if (status != null && status != StoreDataStatus.LOADING) {
                homeViewModel.userOrders.observe(viewLifecycleOwner) { orders ->
                    if (orders.isNotEmpty()) {
                        ordersAdapter.data = orders.sortedByDescending { it.orderDate }
                        binding.orderAllOrdersRecyclerView.adapter?.notifyDataSetChanged()
                        binding.orderAllOrdersRecyclerView.visibility = View.VISIBLE
                    } else if (orders.isEmpty()) {
                        binding.loaderLayout.circularLoader.hideAnimationBehavior
                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                        binding.ordersEmptyMessage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}