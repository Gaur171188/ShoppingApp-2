
package com.shoppingapp.info.screens.cart
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.airbnb.epoxy.TypedEpoxyController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.*
import com.shoppingapp.info.priceCardLayout
import com.shoppingapp.info.screens.home.HomeViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Cart : Fragment() {

    companion object{
        const val TAG = "Cart"
    }


    private val homeViewModel by sharedViewModel<HomeViewModel>()

    private val viewModel by sharedViewModel<CartViewModel>()


    private lateinit var binding: CartBinding
    private val cartController by lazy {CartController()}
    private val priceCartController by lazy {PriceCartController()}


    private lateinit var concatAdapter: ConcatAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.cart, container, false)


        setViews()
        setObservers()

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        homeViewModel.refreshCartData()
    }

    fun initAdapter(){
        val cartProducts = homeViewModel.cartProducts.value ?: emptyList()
        val itemsPrice = homeViewModel.itemsPrice.value ?: mapOf()
        val cartItems = homeViewModel.cartItems.value ?: emptyList()
        val quantityCount = homeViewModel.getQuantityCount()

        cartController.setData(cartItems)
        cartController.products = cartProducts
        priceCartController.setData(itemsPrice)
        priceCartController.itemsCount = quantityCount
        concatAdapter = ConcatAdapter(cartController.adapter, priceCartController.adapter)
        binding.cartRecyclerView.adapter = concatAdapter

    }


    private fun setViews() {


        initAdapter()

        /** swipe refresh cart items **/
        binding.swipeRefreshCartItems.setOnRefreshListener {
            homeViewModel.refreshCartData()
            homeViewModel.refreshStuckLikesAndCartItems()
        }



        /** controller listener **/
        cartController.clickListener = object : CartController.OnClickListener{

            override fun onItemClick(cartItem: User.CartItem, position: Int) {
                val data = bundleOf("productId" to  cartItem.productId)
                findNavController().navigate(R.id.action_cart_to_product_details,data)
            }

            override fun onDeleteClick(cartItem: User.CartItem, position: Int) {
                showDeleteDialog(cartItem.itemId)
            }

        }

        binding.cartAppBar.topAppBar.title = getString(R.string.cart_fragment_label)
        binding.cartCheckOutBtn.setOnClickListener {
            navigateToSelectAddress()
        }




    }

    private fun resetData(){
        cartController.products = emptyList()
        cartController.setData(emptyList())
        priceCartController.setData(emptyMap())
        cartController.setData(emptyList())
    }
    private fun setObservers() {


        // cart items
        homeViewModel.cartItems.observe(viewLifecycleOwner){ cartItems ->
            binding.swipeRefreshCartItems.isRefreshing = false
            if (!cartItems.isNullOrEmpty()){
                cartController.setData(cartItems)
            }else{
                resetData()
            }
        }


        // products
        homeViewModel.cartProducts.observe(viewLifecycleOwner){ cartProducts ->
            binding.swipeRefreshCartItems.isRefreshing = false
            if (!cartProducts.isNullOrEmpty()){
                cartController.products = cartProducts
            }else{
                resetData()
            }

        }




        // price list
        homeViewModel.itemsPrice.observe(viewLifecycleOwner) { itemsPrice ->
            binding.swipeRefreshCartItems.isRefreshing = false
            val quantityCount = homeViewModel.getQuantityCount()
            if (!itemsPrice.isNullOrEmpty()){
                priceCartController.itemsCount = quantityCount
                priceCartController.setData(itemsPrice)
            }else{
                resetData()
            }
        }



    }




    private fun navigateToSelectAddress() {
//        findNavController().navigate(R.id.action_cartFragment_to_selectAddressFragment)
    }


    private fun showDeleteDialog(itemId: String) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.delete_dialog_title_text))
                .setMessage(getString(R.string.delete_cart_item_message_text))
                .setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
                    dialog.cancel()
                }
                // TODO: make delete item from cart require network
                .setPositiveButton(getString(R.string.delete_dialog_delete_btn_text)) { dialog, _ ->
                    homeViewModel.deleteItemFromCart(itemId)
                    dialog.cancel()
                }
                .show()
        }
    }

    inner class PriceCartController(): TypedEpoxyController<Map<String, Double>>() {
        var itemsCount: Int = 0

        override fun buildModels(data: Map<String, Double>?) {
            val totalItemsPrice = data?.let { homeViewModel.getItemsPriceTotal(it) }
//            val itemsCount = homeViewModel.getItemsCount()

            val shippingPrice = 0.0
            val importCharges = 0.0
            val totalPrice = 0.0

            priceCardLayout {
                id("price")
                itemsPriceTotal(totalItemsPrice)
                itemsCount(itemsCount)
                totalPrice(totalPrice)
            }




        }

    }

}

