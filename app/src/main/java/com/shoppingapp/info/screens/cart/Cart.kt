
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
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.*
import com.shoppingapp.info.priceCardLayout
import com.shoppingapp.info.screens.auth.AuthViewModel
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.product_details.ProductDetailsViewModel
import com.shoppingapp.info.utils.Constants
import com.shoppingapp.info.utils.DataStatus
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Cart : Fragment() {

    companion object{ const val TAG = "Cart" }


    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private val authViewModel by sharedViewModel<AuthViewModel>()

    private val productDetailsViewModel by sharedViewModel<ProductDetailsViewModel>()

    private lateinit var binding: CartBinding
    private val cartController by lazy {CartController()}
    private val priceCartController by lazy {PriceCartController()}

    private lateinit var concatAdapter: ConcatAdapter

    private var userId = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.cart, container, false)


        initData()

        setViews()



        return binding.root
    }


    override fun onResume() {
        super.onResume()

        loadData()

        setObservers()


    }




    private fun loadData() {
        val products = homeViewModel.products.value ?: emptyList()
        homeViewModel.loadCartDetails(products)
        binding.cartRecyclerView.adapter = initAdapter()
    }


    private fun resetCartData() {
        val cartProducts =  ArrayList<Product>()
        val itemsPrice =  mapOf<String,Double>()
        val cartItems =  ArrayList<User.CartItem>()
        val quantityCount = 0

        cartController.setData(cartItems)
        cartController.products = cartProducts
        priceCartController.totalItemsPrice = 0.0
        priceCartController.setData(itemsPrice)
        priceCartController.itemsCount = quantityCount
    }


    private fun initAdapter(): ConcatAdapter {

        /** controller listener **/
        cartController.clickListener = object : CartController.OnClickListener {

            override fun onItemClick(product: Product) {
                val data = bundleOf(
                    Constants.KEY_PRODUCT to product,
                    Constants.KEY_IS_EDIT to true
                )
                findNavController().navigate(R.id.action_cart_to_product_details,data)
            }

            override fun onDeleteClick(cartItem: User.CartItem, position: Int) {
                showDeleteDialog(cartItem.itemId)
            }

        }
        concatAdapter = ConcatAdapter(cartController.adapter, priceCartController.adapter)

        return concatAdapter
    }


    private fun setViews() {
        binding.apply {

            cartAppBar.topAppBar.title = getString(R.string.cart_fragment_label)

            /** swipe refresh cart items **/
            swipeRefreshCartItems.setOnRefreshListener {
                loadData()
            }


            /** button next **/
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_cart_to_selectAddress)
            }

            /** back button **/
            cartAppBar.topAppBar.setOnClickListener {
                findNavController().navigateUp()
            }

        }


    }


    private fun setObservers() {



        /** live data insert Cart Status **/
        homeViewModel.removeCartStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                DataStatus.LOADING -> {}
                DataStatus.SUCCESS -> {
                    homeViewModel.loadData()
                }
                DataStatus.ERROR ->{}

                else -> {}
            }
        }



        // cart items
        homeViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            binding.swipeRefreshCartItems.isRefreshing = false
            if (!cartItems.isNullOrEmpty()){
                val cartProduct = homeViewModel.cartProducts.value ?: emptyList()
                cartController.products = cartProduct
                cartController.setData(cartItems)
            }else{
                resetCartData()
            }
        }


        // products
        homeViewModel.cartProducts.observe(viewLifecycleOwner) { cartProducts ->
            binding.swipeRefreshCartItems.isRefreshing = false
            if (!cartProducts.isNullOrEmpty()){
                cartController.products = cartProducts
                val cartItems = homeViewModel.cartItems.value ?: emptyList()
                cartController.setData(cartItems)
            }else{
                resetCartData()
            }
        }



        // price list
        homeViewModel.itemsPrice.observe(viewLifecycleOwner) { itemsPrice ->
            binding.swipeRefreshCartItems.isRefreshing = false
            if (!itemsPrice.isNullOrEmpty()){

                priceCartController.setData(itemsPrice)
            }else{
                resetCartData()
            }
        }


        // total items price
        homeViewModel.totalItemsPrice.observe(viewLifecycleOwner){ totalPrice ->
            binding.swipeRefreshCartItems.isRefreshing = false
            if (totalPrice != null){
                priceCartController.totalItemsPrice = totalPrice
                val itemsPrice = homeViewModel.itemsPrice.value ?: mapOf()
                priceCartController.setData(itemsPrice)
            }else{
                resetCartData()
            }
        }



        homeViewModel.quantityCount.observe(viewLifecycleOwner){ quantityCount ->
            binding.swipeRefreshCartItems.isRefreshing = false
            if (quantityCount != null){
                priceCartController.itemsCount = quantityCount
                val itemsPrice = homeViewModel.itemsPrice.value ?: mapOf()

                priceCartController.setData(itemsPrice)
            }else{
                resetCartData()
            }
        }


// refresh data after add product to cart

    }


    private fun initData() {

        userId = homeViewModel.userData.value?.userId ?: ""
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
                    homeViewModel.removeCartItem(itemId,userId)




                    dialog.cancel()
                }
                .show()
        }
    }

    inner class PriceCartController(): TypedEpoxyController<Map<String, Double>>() {
        var itemsCount: Int = 0
        var totalItemsPrice: Double = 0.0

        override fun buildModels(data: Map<String, Double>?) {
//            val totalItemsPrice = data?.let { homeViewModel.loadItemsPriceTotal(it) }


            /** to get the total price you must calculate the shipping Price and import charges **/
            val shippingPrice = 0.0
            val importCharges = 0.0


            priceCardLayout {
                id("price")
                itemsPriceTotal(totalItemsPrice)
                itemsCount(itemsCount)
                totalPrice(totalItemsPrice)
            }

        }

    }



}

