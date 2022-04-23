
package com.shoppingapp.info.screens.cart
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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

//    private val ordersViewModel by sharedViewModel<OrdersViewModel>()
    private val homeViewModel by sharedViewModel<HomeViewModel>()


    private val viewModel by sharedViewModel<CartViewModel>()


    private lateinit var binding: CartBinding
    private val cartController by lazy {CartController()}
    private val priceCartController by lazy {PriceCartController()}


//    private lateinit var itemsAdapter: CartItemAdapter
    private lateinit var concatAdapter: ConcatAdapter




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.cart, container, false)


        // TODO: improve the cart item design.


//        cartController = CartController()



        setViews()
        setObservers()
        viewModel.getCartItems()

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        viewModel.getCartItems()
    }

    private fun setViews() {


        /** controller listener **/
        cartController.clickListener = object : CartController.OnClickListener{

            override fun onItemClick(cartItem: User.CartItem, position: Int) {
                Toast.makeText(requireContext(),"onCartClick",Toast.LENGTH_SHORT).show()
            }

            override fun onDeleteClick(cartItem: User.CartItem, position: Int) {
                showDeleteDialog(cartItem.itemId)
            }

        }

        binding.cartAppBar.topAppBar.title = getString(R.string.cart_fragment_label)
        binding.cartEmptyMessage.visibility = View.GONE
        binding.cartCheckOutBtn.setOnClickListener {
            navigateToSelectAddress()
        }


        if (context != null) {
            try {
                concatAdapter = ConcatAdapter(cartController.adapter, priceCartController.adapter)
                binding.cartRecyclerView.adapter = concatAdapter
            }catch (ex: Exception){

            }

        }



    }

    private fun setObservers() {


        // cart items
        viewModel.cartItems.observe(viewLifecycleOwner){ cartItems ->
            if (cartItems != null){
                val items = viewModel.cartItems.value ?: emptyList()
                val likeList = homeViewModel.userLikes.value ?: emptyList()
                val prosList = viewModel.cartProducts.value ?: emptyList()
                cartController.products = prosList
                cartController.likes = likeList
                cartController.setData(items)

//                binding.cartRecyclerView.adapter?.notifyDataSetChanged()

                Log.d(TAG,"cartItems: ${cartItems.size}")
                Log.d(TAG,"likes: ${likeList.size}")
                Log.d(TAG,"prosList: ${prosList.size}")

            }else{
                cartController.setData(emptyList())
            }
        }



        // price list
        viewModel.priceList.observe(viewLifecycleOwner) { priceList ->
            if (priceList != null){
                val items = viewModel.cartItems.value ?: emptyList()
                val likeList = homeViewModel.userLikes.value ?: emptyList()
                val prosList = viewModel.cartProducts.value ?: emptyList()
                cartController.products = prosList
                cartController.likes = likeList
                cartController.setData(items)
                priceCartController.setData(priceList)
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
                    viewModel.deleteItemFromCart(itemId)
                    dialog.cancel()
                }
                .show()
        }
    }

    inner class PriceCartController(): TypedEpoxyController<Map<String, Double>>() {

        override fun buildModels(data: Map<String, Double>) {
            val totalItemsPrice = viewModel.getItemsPriceTotal(data)
            val itemsCount = viewModel.getItemsCount()

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

