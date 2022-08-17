package com.shoppingapp.info.screens.order_details


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.OrderDetailsBinding
import com.shoppingapp.info.screens.cart.CartController
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.properties.Delegates


class OrderDetails: Fragment() {

    companion object{
        const val TAG = "OrderDetails"
    }


    private lateinit var binding: OrderDetailsBinding
    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private val cartController by lazy { CartController() }

    private lateinit var sharePrefManager: SharePrefManager
    private var isSeller by Delegates.notNull<Boolean>()

    private lateinit var order: User.OrderItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.order_details, container, false)

        sharePrefManager = SharePrefManager(requireContext())
        isSeller = sharePrefManager.isUserSeller()


        initData()
        setViews()

//        setObserves()




        return binding.root

    }


    private fun initData() {
        order = try { arguments?.getParcelable(Constants.KEY_ORDER)!! }
        catch (ex: Exception){ User.OrderItem() }
    }



//    private fun setObserves() {
//
//    }



    private fun setAdapter() {
        val products = homeViewModel.products.value ?: emptyList()
//        val cartItems = homeViewModel.cartItems.value ?: emptyList()

        cartController.isSeller = true // to hide the delete cart icon.

        cartController.products = products

        cartController.setData(order.items)


        cartController.clickListener = object: CartController.OnClickListener {

            override fun onDeleteClick(cartItem: User.CartItem, position: Int) {

                // you can delete the cart item only from the customer side.
                val userId = homeViewModel.userData.value?.userId!!
                homeViewModel.removeCartItem(cartItem.itemId)
            }

            override fun onItemClick(product: Product) {}


        }

        binding.productsRecyclerView.adapter = cartController.adapter


    }


    private fun setUserViews(){

        binding.apply {
            val isSeller = homeViewModel.userData.value?.userType == UserType.SELLER.name
            if (isSeller){ // seller
                btnNextAccept.text = resources.getString(R.string.accept_order)
                btnNextAccept.setTextColor(Color.WHITE)
                btnNextAccept.setBackgroundColor(Color.GREEN)
            }else{ // admin or customer
                btnNextAccept.text = resources.getString(R.string.next)
            }
        }
    }


    private fun setViews() {



        binding.apply {

            orderDetailAppBar.topAppBar.title = "Order Details"


            setUserViews()
            setAdapter()


            /** back button **/
            orderDetailAppBar.topAppBar.setOnClickListener {
                findNavController().navigateUp()
            }

            // order details layout
            orderDetails.apply {
                orderDate = order.orderDate
                orderAddress = order.address.streetAddress
                orderStatus = order.status
            }


            // paymentDetails layout
            paymentDetails.apply {
                itemsCount = order.items.size
                itemsPriceTotal = getItemsPriceTotal(order.itemsPrices,order.items)
                totalPrice = 0.0
            }


            /** button next, accept **/
            btnNextAccept.setOnClickListener {
                val data = bundleOf(Constants.KEY_ORDER to order)
                val isSeller = homeViewModel.userData.value?.userType == UserType.SELLER.name
                if (isSeller){ // seller
                    acceptOrder()
                }else{ // admin or customer
                    findNavController().navigate(R.id.action_orderDetails_to_payment,data)
                }

            }




        }
    }

    fun acceptOrder() {

    }

}