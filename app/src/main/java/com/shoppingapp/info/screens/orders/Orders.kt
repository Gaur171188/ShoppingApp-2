package com.shoppingapp.info.screens.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.OrdersBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Orders : Fragment() {

    companion object{
        const val TAG = "Orders"
    }

    private val homeViewModel by sharedViewModel<HomeViewModel>()

    private lateinit var binding: OrdersBinding
    private val orderController by lazy { OrderController() }
//    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.orders, container, false)


        setViews()
        setObservers()

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        homeViewModel.getAllOrders()
//    }


    private fun setAdapter(){
//        val orders = homeViewModel.orders.value
        orderController.setData(emptyList())


        orderController.clickListener = object : OrderController.OnClickListener {

            override fun onItemClick(order: User.OrderItem) {
                Toast.makeText(requireContext(),"click",Toast.LENGTH_SHORT).show()
            }

        }


        binding.orderRecyclerView.adapter = orderController.adapter
    }


    private fun setViews() {

        setAdapter()
        binding.apply {

            ordersAppBar.topAppBar.title = "Orders"


        }


//        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//        binding.ordersAppBar.topAppBar.title = getString(R.string.orders)
//        binding.ordersAppBar.topAppBar.setNavigationOnClickListener {
//            findNavController().navigateUp()
//        }
//        binding.ordersEmptyMessage.visibility = View.GONE
//        if (context != null) {
//            ordersAdapter = OrdersAdapter(emptyList(), requireContext())
//            ordersAdapter.onClickListener = object : OrdersAdapter.OnClickListener {
//                override fun onCardClick(orderId: String) {
//                    Log.d(TAG, "onOrderSummaryClick: Getting order details")
//                    findNavController().navigate(
//                        R.id.action_orders_to_orderDetailsFragment,
//                        bundleOf("orderId" to orderId)
//                    )
//                }
//            }
//            binding.orderAllOrdersRecyclerView.adapter = ordersAdapter
//        }

    }


    private fun setObservers() {


        /** orders live data **/
        homeViewModel.orders.observe(viewLifecycleOwner){orders->
            if (orders != null){
                orderController.setData(orders)
            }
        }


//        homeViewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
//            when (status) {
//                StoreDataStatus.LOADING -> {
//                    binding.orderAllOrdersRecyclerView.visibility = View.GONE
//                    binding.ordersEmptyMessage.visibility = View.GONE
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
//                    binding.loaderLayout.circularLoader.showAnimationBehavior
//                }
//                else -> {
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                }
//            }
//
//            if (status != null && status != StoreDataStatus.LOADING) {
//                homeViewModel.userOrders.observe(viewLifecycleOwner) { orders ->
//                    if (orders.isNotEmpty()) {
//                        ordersAdapter.data = orders.sortedByDescending { it.orderDate }
//                        binding.orderAllOrdersRecyclerView.adapter?.notifyDataSetChanged()
//                        binding.orderAllOrdersRecyclerView.visibility = View.VISIBLE
//                    } else if (orders.isEmpty()) {
//                        binding.loaderLayout.circularLoader.hideAnimationBehavior
//                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                        binding.ordersEmptyMessage.visibility = View.VISIBLE
//                    }
//                }
//            }
//        }
    }
}